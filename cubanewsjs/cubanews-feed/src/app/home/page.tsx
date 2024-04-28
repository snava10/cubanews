"use client";
import useSWR from "swr";
import { FeedResponseData } from "../interfaces";
import NewsItemComponent from "../components/NewsItem";
import { Box, Stack, Typography } from "@mui/material";
import Container from "@mui/material/Container";

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
    <>
      <Container>
        <Typography>Noticias Destacadas</Typography>
        <Stack spacing={1}>
          {data.content?.feed.map((x) => (
            <NewsItemComponent key={x.title} item={x} />
          ))}
        </Stack>
      </Container>
      <Container>
        <Typography>Publicaciones</Typography>
      </Container>
    </>
  );
}
