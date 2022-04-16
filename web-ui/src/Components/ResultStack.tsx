import { Box, CircularProgress, Stack, Typography } from "@mui/material";
import { CompactOriginalCard } from "./CompactOriginalCard";
import { GNewsInspiredCard } from "./GNewsInspiredCard";
import { GNewsWithImageCard } from "./GNewsWithImageCard";
import { SearchResultCard } from "./SearchResultCard";

export function ResultStack(props: any) {
  // console.log("[Result stack] called")
  
  function ConditionalResults() {
    if (props.searchResults.length === 1 && props.searchResults[0].title === 'No results :(') {
      return <Box sx={{textAlign: 'center'}}>
      <img src="not-found.webp" style={{width:200}} />
      <Typography variant="body1">
        No pudimos encontrar lo que busca :(
      </Typography>
    </Box>
    } else if (props.searchResults.length === 0) {
      return <Box sx={{textAlign: 'center'}}
      ><CircularProgress />
      </Box>
    } 
    return props.searchResults.map((res: any) => (
      // <SearchResultCard
      //   key={res.url}
      //   content={res}
      // />
      <GNewsWithImageCard
        key={res.url}
        content={res}
        stackLayout={true}
      />
    ));
  }

  return (
    // Spacing value changes with card styles:
    // - original = 1
    // - compact original = 1
    // - google news = 2
    <Stack spacing={2}>
      <ConditionalResults />
    </Stack>
  )
}
