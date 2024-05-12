import { NewsItem } from "@/app/interfaces";
import { Kysely, sql } from "kysely";
import { Database, FeedTable } from "../dataschema";

function dbItemToNewsItem(dbitem: any): NewsItem {
  return {
    title: dbitem.title,
    source: dbitem.source,
    url: dbitem.url,
    updated: dbitem.updated,
    isoDate: dbitem.isodate.toString(),
    feedts: dbitem.feedts,
    content: dbitem.content,
    tags: dbitem.tags ? dbitem.tags.split(",") : [],
  } as NewsItem;
}

/**
 *
 * @param db
 * @param x
 * @param feedts
 * @param page Starts on 1
 * @returns
 */
export async function xOfEachSource(
  db: Kysely<Database>,
  feedts: number,
  page: number,
  pageSize: number
): Promise<Array<NewsItem>> {
  const query = sql<FeedTable>`
  SELECT *
  FROM (
   SELECT *, ROW_NUMBER() OVER (PARTITION BY
   source ORDER BY updated desc) AS row_number
   FROM feed where feedts = ${feedts}
  ) AS t
  WHERE t.row_number > ${pageSize * (page - 1)} AND t.row_number <= ${
    pageSize * page
  }
  order by updated desc
  `;
  const result = (await query.execute(db)).rows;
  return result.map(dbItemToNewsItem);
}

export async function allSortedByUpdated(
  db: Kysely<Database>,
  feedts: number,
  page: number,
  pageSize: number
): Promise<Array<NewsItem>> {
  const feeds = await db
    .selectFrom("feed")
    .selectAll()
    .where("feed.feedts", "=", feedts)
    .orderBy("updated desc")
    .offset((page - 1) * pageSize)
    .limit(pageSize)
    .execute();

  return feeds.map(dbItemToNewsItem);
}
