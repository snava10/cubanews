import { Box, CircularProgress, Stack, Typography } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

// We go with this while the masonry component is fucky.

export function ResultStack(props: any) {
  // console.log("[Result stack] called")

  function ConditionalResults() {
    if (
      props.searchResults.length === 1 &&
      props.searchResults[0].title === "No results :("
    ) {
      return (
        <Box sx={{ textAlign: "center" }}>
          <img src="not-found.webp" style={{ width: 200 }} />
          <Typography variant="body1">
            No pudimos encontrar lo que busca :(
          </Typography>
        </Box>
      );
    } else if (props.searchResults.length === 0) {
      return (
        <Box sx={{ textAlign: "center" }}>
          <CircularProgress />
        </Box>
      );
    }
    return props.searchResults.map((res: any) => (
      <SearchResultCard key={res.url} content={res} bucketName="cubanews" />
    ));
  }

  return (
    <Stack spacing={1}>
      <ConditionalResults />
    </Stack>
  );
}
