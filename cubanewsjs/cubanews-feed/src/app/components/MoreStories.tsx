import { Stack } from "@mui/joy";
import { NewsItem } from "../interfaces";
import { useEffect, useState } from "react";
import NewsItemComponent from "./NewsItem";

type MoreStoriesProps = {
  newsItems: NewsItem[];
};

export default function MoreStoriesComponent(props: MoreStoriesProps) {
  const [newsItems, setNewsItems] = useState<NewsItem[]>(props.newsItems);

  useEffect(() => {
    setNewsItems(props.newsItems);
  }, [props.newsItems]);

  return (
    <Stack spacing={2}>
      {newsItems.map((x) => (
        <NewsItemComponent key={x.title} item={x} />
      ))}
    </Stack>
  );
}
