import { Box, CircularProgress, Stack } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

// We go with this while the masonry component is fucky.

export function ResultStack(props: any) {
  // console.log("[Result stack] called")

  function ConditionalResults() {
    if (props.searchResults.length === 0) {
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
