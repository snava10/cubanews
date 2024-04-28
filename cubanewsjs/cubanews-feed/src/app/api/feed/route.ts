import { FeedResponseData, NewsItem } from "../../interfaces";
import { createKysely } from "@vercel/postgres-kysely";
import { Database, FeedTable } from "../dataschema";
import { NextRequest, NextResponse } from "next/server";
import { ApifyClient } from "apify-client";
import { sql } from "kysely";

const db = createKysely<Database>();

const client = new ApifyClient({
  token: process.env.APIFY_TOKEN,
});

export async function GET(
  request: NextRequest
): Promise<NextResponse<FeedResponseData | null>> {
  if (request.nextUrl.searchParams.get("refresh")) {
    refreshFeed()
      .then((data) => console.log(data))
      .catch((error) => console.error(error));

    return NextResponse.json(
      {
        banter: "Refreshing cubanews feed",
      },
      { status: 200 }
    );
  }

  const page = parseInt(request.nextUrl.searchParams.get("page") ?? "0");
  const pageSize = parseInt(
    request.nextUrl.searchParams.get("pageSize") ?? "10"
  );

  return getFeed(page, pageSize);
}

async function getFeed(
  page: number,
  pageSize: number
): Promise<NextResponse<FeedResponseData | null>> {
  const latestFeedts = await db
    .selectFrom("feed")
    .select([sql`max(feed.feedts)`.as("feedts")])
    .executeTakeFirst();

  if (!latestFeedts?.feedts) {
    return NextResponse.json(
      {
        banter: "No feeds available",
      },
      { status: 500 }
    );
  }

  const latestFeedtsValue = latestFeedts.feedts as number;
  const feeds = await db
    .selectFrom("feed")
    .selectAll()
    .where("feed.feedts", "=", latestFeedtsValue)
    .orderBy("updated desc")
    .offset(page * pageSize)
    .limit(pageSize)
    .execute();

  const items = feeds.map((f) => {
    const ni = {
      title: f.title,
      source: f.source,
      url: f.url,
      updated: f.updated,
      isoDate: f.isodate.toString(),
      feedts: f.feedts,
      content: f.content,
      tags: ["derechos-humanos", "deporte", "politica"],
    } as NewsItem;
    return ni;
  });

  const timestamp = items.length > 0 ? items[0].feedts : 0;
  return NextResponse.json(
    {
      banter: "Cubanews feed!",
      content: {
        timestamp,
        feed: items,
      },
    },
    { status: 200 }
  );
}

async function refreshFeed(): Promise<NewsItem[]> {
  const datasetCollectionClient = client.datasets();
  const listDatasets = await datasetCollectionClient.list();
  const dataset = listDatasets.items.find((dataset) =>
    dataset.name?.startsWith("adncuba")
  );
  // If dataset found, return its ID
  if (!dataset) {
    return [];
  }

  const { items } = await client.dataset(dataset.id).listItems();

  const newsItems = items
    .map((item) => item as unknown)
    .map((item) => item as NewsItem);

  const currentDate = new Date();

  const values = newsItems.map(
    (x) => newsItemToFeedTable(x, currentDate) as any
  );
  const insertResult = await db
    .insertInto("feed")
    .values(values)
    .executeTakeFirst();
  console.log(insertResult.numInsertedOrUpdatedRows);
  return newsItems;
}

function newsItemToFeedTable(ni: NewsItem, currentDate: Date): FeedTable {
  const isoDateString = currentDate.toISOString();
  const epochTimestamp = currentDate.getTime();

  return {
    content: "",
    feedisodate: isoDateString,
    feedts: epochTimestamp,
    isodate: ni.isoDate,
    source: ni.source,
    title: ni.title,
    updated: ni.updated,
    url: ni.url,
  } as FeedTable;
}
