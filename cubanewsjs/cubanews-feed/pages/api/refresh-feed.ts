import { ApifyClient } from "apify-client";
import type { NextApiRequest, NextApiResponse } from "next";
import { NewsItem } from "../../interfaces";
import { createKysely } from "@vercel/postgres-kysely";
import { Database, FeedTable } from "./dataschema";

type ResponseData = {
  banter: string;
};

const client = new ApifyClient({
  token: process.env.APIFY_TOKEN,
});

const db = createKysely<Database>();

async function getData(): Promise<NewsItem[]> {
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
  const isoDateString = currentDate.toISOString();
  const epochTimestamp = currentDate.getTime();

  const insertResult = await db
    .insertInto("feed")
    .values(
      newsItems.map((ni) => {
        const feedItem = {
          content: "",
          feedisodate: isoDateString,
          feedts: epochTimestamp,
          isodate: ni.isoDate,
          source: ni.source,
          title: ni.title,
          updated: ni.updated,
          url: ni.url,
        } as FeedTable;
        return feedItem;
      })
    )
    .executeTakeFirst();
  console.log(insertResult.numInsertedOrUpdatedRows);
  return newsItems;
}

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<ResponseData>
) {
  getData()
    .then((data) => console.log(data))
    .catch((error) => console.log(error));

  res.status(200).json({
    banter: "Cubanews feed being updated!",
  });
}
