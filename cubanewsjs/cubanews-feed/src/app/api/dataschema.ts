import { Generated } from "kysely";

export interface FeedTable {
  id: Generated<number>;
  feedts: number;
  feedisodate: string;
  source: string;
  title: string;
  url: string;
  content: string;
  updated: number; // This is the news item updated field.
  isodate: string;
  tags: string;
  score: number;
}

export interface Database {
  feed: FeedTable;
}
