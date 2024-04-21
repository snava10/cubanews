import useSWR from "swr";
import type { FeedResponseData } from "../interfaces";
import NewsItemComponent from "../components/NewsItem";
import Head from "next/head";

const fetcher = (url: string) => fetch(url).then((res) => res.json());

export default function Index() {
  const { data, error, isLoading } = useSWR<FeedResponseData>(
    "/api/feed",
    fetcher
  );

  if (error) return <div>Failed to load</div>;
  if (isLoading) return <div>Loading...</div>;
  if (!data) return null;

  console.log(data);

  return (
    <>
      <Head>
        <link rel="icon" href="/cuban-flag.ico" />
      </Head>
      <ul>
        {data.content.feed.map((x) => (
          <NewsItemComponent item={x} />
        ))}
      </ul>
    </>
  );
}
