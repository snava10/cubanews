import { FeedResponseData, NewsItem, NewsSourceName } from "../../interfaces";
import { createKysely } from "@vercel/postgres-kysely";
import { Database, FeedTable } from "../dataschema";
import { NextRequest, NextResponse } from "next/server";
import { ApifyClient, Dataset } from "apify-client";
import { sql } from "kysely";

type RefreshFeedResult = {
  datasetName: string;
  insertedRows: bigint | number;
};

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
      tags: f.tags ? f.tags.split(",") : [],
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

async function refreshFeed(): Promise<Array<RefreshFeedResult>> {
  const datasetCollectionClient = client.datasets();
  const listDatasets = await datasetCollectionClient.list();
  const newsSourcesSet = new Set(
    Object.values(NewsSourceName).map((s) => s.toLocaleLowerCase() + "-dataset")
  );
  // Include only the news sources with propper crawlers.
  const datasets = listDatasets.items.filter(
    (dataset) => dataset && dataset.name && newsSourcesSet.has(dataset.name)
  );
  const currentDate = new Date();
  const feedRefreshResult = await Promise.all(
    datasets.map((dataset) => {
      console.log("Refreshing dataset: ", dataset.name);
      return refreshFeedDataset(dataset, currentDate);
    })
  );
  return feedRefreshResult;
}

async function refreshFeedDataset(
  dataset: Dataset,
  feedRefreshDate: Date
): Promise<RefreshFeedResult> {
  if (!dataset || !dataset.name) {
    return { datasetName: "unknown", insertedRows: 0 };
  }
  const { items } = await client.dataset(dataset.id).listItems();
  const newsItems = items
    .map((item) => item as unknown)
    .map((item) => item as NewsItem);
  const values = newsItems.map(
    (x) => newsItemToFeedTable(x, feedRefreshDate) as any
  );
  const insertResult = await db
    .insertInto("feed")
    .values(values)
    .executeTakeFirst();

  return {
    datasetName: dataset.name as string,
    insertedRows: insertResult.numInsertedOrUpdatedRows?.valueOf() as bigint,
  };
}

function newsItemToFeedTable(ni: NewsItem, currentDate: Date): FeedTable {
  const isoDateString = currentDate.toISOString();
  const epochTimestamp = currentDate.getTime();
  return {
    content: ni.content,
    feedisodate: isoDateString,
    feedts: epochTimestamp,
    isodate: ni.isoDate,
    source: ni.source,
    title: ni.title,
    updated: ni.updated,
    url: ni.url,
  } as FeedTable;
}
