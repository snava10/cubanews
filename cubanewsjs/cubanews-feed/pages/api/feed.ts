import type { NextApiRequest, NextApiResponse } from "next";
import { FeedResponseData, NewsItem } from "../../interfaces";
import { createKysely } from "@vercel/postgres-kysely";
import { Database } from "./dataschema";

const db = createKysely<Database>();

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<FeedResponseData>
) {
  const feeds = await db.selectFrom("feed").selectAll().execute();

  const items = feeds.map((f) => {
    console.log(f.isodate);
    const ni = {
      title: f.title,
      source: f.source,
      url: f.url,
      updated: f.updated,
      isoDate: f.isodate.toString(),
      feedts: f.feedts,
    } as NewsItem;
    return ni;
  });

  const timestamp = items.length > 0 ? items[0].feedts : 0;

  res.status(200).json({
    banter: "Cubanews feed!",
    content: {
      timestamp,
      feed: items,
    },
  });
}
