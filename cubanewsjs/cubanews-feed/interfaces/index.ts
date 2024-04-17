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
  title: string;
  url: string;
};

export type NewsFeed = {
  timestamp: number;
  feed: Array<NewsItem>;
};

export type FeedResponseData = {
  banter: string;
  content: NewsFeed;
};
