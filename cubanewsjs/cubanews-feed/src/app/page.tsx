"use client";
import { FeedResponseData } from "./interfaces";
import NewsItemComponent from "./components/NewsItem";
import useSWR from "swr";

export default function Home() {
  const fetcher = (url: string) => fetch(url).then((res) => res.json());

  const { data, error, isLoading } = useSWR<FeedResponseData>(
    "/api/feed",
    fetcher
  );

  if (error) return <div>Failed to load</div>;
  if (isLoading) return <div>Loading...</div>;
  if (!data) return null;

  console.log(data);

  return (
    <ul>
      {data.content?.feed.map((x) => (
        <NewsItemComponent key={x.title} item={x} />
      ))}
    </ul>
  );
}
