"use client";
import useSWR from "swr";
import {
  FeedResponseData,
  ResolveNewsletterSubscriptionData,
} from "../interfaces";
import NewsItemComponent from "../components/NewsItem";
import {
  Box,
  Stack,
  Typography,
  Container,
  Divider,
  Chip,
  CircularProgress,
} from "@mui/joy";
import CopyrightIcon from "@mui/icons-material/Copyright";
import NewsLetterSubscriptionComponent from "../components/NewsLetterSubscription";
import React from "react";

export default function Home() {
  const fetcher = (url: string) => fetch(url).then((res) => res.json());

  const [showSubscription, setShowSubscription] = React.useState<boolean>(
    localStorage.getItem("showSubscription") === null ||
      localStorage.getItem("showSubscription") === "true"
      ? true
      : false
  );

  const { data, error, isLoading } = useSWR<FeedResponseData>(
    "/api/feed",
    fetcher
  );

  if (error) return <div>Failed to load</div>;
  if (isLoading)
    return (
      <Container
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
        }}
      >
        <CircularProgress color="primary" size="lg" variant="soft" />
      </Container>
    );
  if (!data) return null;

  function resolveNewsletterSubscription(
    data: ResolveNewsletterSubscriptionData
  ) {
    const operation = data.operation;
    if (operation === "close") {
      if (data.dontShowAgain) {
        localStorage.setItem("showSubscription", "false");
      }
      setShowSubscription(false);
    }
    console.log(data);
  }

  return (
    <Stack>
      {showSubscription ? (
        <Container sx={{ mt: 2 }}>
          <NewsLetterSubscriptionComponent
            onResolve={resolveNewsletterSubscription}
          />
        </Container>
      ) : null}
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
