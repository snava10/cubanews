import { Box, Stack, Typography } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

// We go with this while the masonry component is fucky. 

export function ResultStack(props: any) {
  // console.log("[Result stack] called")

  return (
    <Stack spacing={1}>
      {props.searchResults.length === 0 ?
        // TODO: Make this a better UI.
        <Box sx={{textAlign: 'center'}}>
          <img src="not-found.webp" style={{width:200}} />
          <Typography variant="body1">
            No pudimos encontrar lo que busca :(
          </Typography>
        </Box>
        :
        props.searchResults.map((res: any) => (
          <SearchResultCard
            key={res.url}
            content={res}
          />
        ))
      }
    </Stack>
  )
}
