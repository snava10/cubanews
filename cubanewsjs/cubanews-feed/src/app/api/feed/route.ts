import {
  FeedResponseData,
  Interaction,
  NewsItem,
  NewsSourceName,
} from "../../interfaces";
import { NextRequest, NextResponse } from "next/server";
import { ApifyClient, Dataset } from "apify-client";
import { sql } from "kysely";
import { xOfEachSource } from "./feedStrategies";
import { exec } from "child_process";
import { newsItemToFeedTable } from "@/local/localFeedLib";
import cubanewsApp from "@/app/cubanewsApp";

export type RefreshFeedResult = {
  datasetName: string;
  insertedRows: bigint | number;
};

const db = cubanewsApp.getDatabase;
const client = new ApifyClient({
  token: process.env.APIFY_TOKEN,
});

export async function GET(
  request: NextRequest
): Promise<NextResponse<FeedResponseData | null>> {
  if (request.nextUrl.searchParams.get("refresh")) {
    if (request.headers.get("ADMIN_TOKEN") !== process.env.ADMIN_TOKEN) {
      return NextResponse.json(
        {
          banter: "You are not authorized to refresh the feed",
        },
        { status: 401, statusText: "Unauthorized" }
      );
    }
    if (request.nextUrl.searchParams.get("dryrun")) {
      return NextResponse.json(
        {
          banter: "Dry Run. Refreshing cubanews feed",
        },
        { status: 200 }
      );
    }

    if (request.nextUrl.searchParams.get("local")) {
      exec("/home/sergio/github/cubanews/cubanewsjs/cubanews-crawler/run.sh");
      return NextResponse.json(
        {
          banter: "Refreshing cubanews feed from Local Sources.",
        },
        { status: 200 }
      );
    }

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

  const page = parseInt(request.nextUrl.searchParams.get("page") ?? "1");
  const pageSize = parseInt(
    request.nextUrl.searchParams.get("pageSize") ?? "2"
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

  // This strategy gets the top x news of every source.
  // X is the page size, if implemented page 2 would mean skipping the first x for each news source
  // and getting the following x. This is temporary until a better, ranked version of the feed is conceived.
  const items = await xOfEachSource(
    db,
    latestFeedts.feedts as number,
    page,
    pageSize
  );

  const itemsMap = new Map<number, NewsItem>();
  items.forEach((x) => {
    itemsMap.set(x.id as number, x);
  });

  const interactions = await db
    .selectFrom("interactions")
    .select([
      "interaction",
      "feedid",
      db.fn.count("id").$castTo<string>().as("count"),
    ])
    .where(
      "feedid",
      "in",
      items.map((x) => x.id as number)
    )
    .groupBy("interaction")
    .groupBy("feedid")
    .execute();

  interactions.forEach((x) => {
    const action: Interaction = x.interaction;
    const feedid = x.feedid as number;
    const count = parseInt(x.count);
    const item = itemsMap.get(feedid);
    if (item && item.interactions) {
      item.interactions[action] = count;
    }
  });

  const timestamp = items.length > 0 ? items[0].feedts : 0;

  return NextResponse.json(
    {
      banter: "Cubanews feed!",
      content: {
        timestamp,
        feed: Array.from(itemsMap.values()),
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
  const values = newsItems
    .filter((newsItem) => isNewsItemValid(newsItem))
    .map((x) => newsItemToFeedTable(x, feedRefreshDate) as any);
  const insertResult = await db
    .insertInto("feed")
    .values(values)
    .executeTakeFirst();

  return {
    datasetName: dataset.name as string,
    insertedRows: insertResult.numInsertedOrUpdatedRows?.valueOf() as bigint,
  };
}

function isNewsItemValid(newsItem: NewsItem): boolean {
  return (
    newsItem.isoDate !== null &&
    newsItem.updated !== null &&
    newsItem.title !== null &&
    newsItem.title.length > 0 &&
    newsItem.url !== null &&
    newsItem.url.length > 0
  );
}
