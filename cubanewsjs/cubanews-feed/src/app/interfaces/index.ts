export type Person = {
  id: string;
  name: string;
  height: string;
  mass: string;
  hair_color: string;
  skin_color: string;
  eye_color: string;
  gender: string;
};

export type ResponseError = {
  message: string;
};

export type NewsItem = {
  id?: number;
  title: string;
  source: NewsSourceName;
  url: string;
  updated: number;
  isoDate: string;
  feedts: number | null | undefined;
  content: string | null | undefined;
  tags: Array<string>;
};

export type NewsFeed = {
  timestamp: number | null | undefined;
  feed: Array<NewsItem>;
};

export type FeedResponseData = {
  banter: string;
  content?: NewsFeed;
};

export enum NewsSourceName {
  ADNCUBA = "adncuba",
  CATORCEYMEDIO = "catorceymedio",
  DIARIODECUBA = "diariodecuba",
  CIBERCUBA = "cibercuba",
}
