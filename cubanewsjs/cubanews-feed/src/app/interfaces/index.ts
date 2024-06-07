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
  score: number;
};

export type NewsFeed = {
  timestamp: number | null | undefined;
  feed: Array<NewsItem>;
};

export type FeedResponseData = {
  banter: string;
  content?: NewsFeed;
};

export type InteractionResponseData = {
  banter: string;
};

export enum NewsSourceName {
  ADNCUBA = "adncuba",
  CATORCEYMEDIO = "catorceymedio",
  DIARIODECUBA = "diariodecuba",
  CIBERCUBA = "cibercuba",
  ELTOQUE = "eltoque",
  CUBANET = "cubanet",
}

export enum NewsSourceDisplayName {
  ADNCUBA = "AdnCuba",
  CATORCEYMEDIO = "14yMedio",
  DIARIODECUBA = "Diario de Cuba",
  CIBERCUBA = "Cibercuba",
  ELTOQUE = "elToque",
  CUBANET = "Cubanet",
  EMPTY = "",
}
