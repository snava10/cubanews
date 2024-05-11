"use client";
import useSWR from "swr";
import { FeedResponseData } from "../interfaces";
import NewsItemComponent from "../components/NewsItem";
import { Box, Stack, Typography, Container, Divider, Chip } from "@mui/joy";
import CopyrightIcon from "@mui/icons-material/Copyright";

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
    <Stack>
      <Container sx={{ mt: 2 }}>
        <Box>
          <Typography level="h2" sx={{ mb: 2, ml: 1 }}>
            Noticias Destacadas
          </Typography>
        </Box>

        <Stack spacing={2}>
          {data.content?.feed.map((x) => (
            <NewsItemComponent key={x.title} item={x} />
          ))}
        </Stack>
        <Divider sx={{ m: 4 }}>
          <Chip
            variant="outlined"
            startDecorator={<CopyrightIcon />}
            sx={{ p: 1 }}
          >
            Cuba News
          </Chip>
        </Divider>
      </Container>
    </Stack>
  );
}
