import { Stack } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

export function ResultStack(props: any) {
  console.log("[Result stack] called")
  return (
    <Stack spacing={1} sx={{ paddingTop: 2, paddingBottom: 2 }}>
      {props.searchResults.map((res: any) => (
        <SearchResultCard
          key={res.url}
          content={res}
        />
      ))}
    </Stack>
  )
}
