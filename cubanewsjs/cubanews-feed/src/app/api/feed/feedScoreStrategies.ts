import { NewsItem } from "@/app/interfaces";

export async function getDefaultFeedScore(): Promise<number> {
  return 0.0;
}

export async function getFeedScore(newsItem: NewsItem): Promise<number> {
  return getDefaultFeedScore();
}
