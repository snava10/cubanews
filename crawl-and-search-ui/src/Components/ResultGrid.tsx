import { Grid } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

/*
* Not in use but has potential. 
*/
export function ResultGrid(props: any) {
  console.log("[Result grid] called")
  return (
    <Grid container spacing={{ xs: 2, md: 3 }} columns={{ xs: 4, sm: 8, md: 12 }}>
      {props.searchResults.map((res: any) => (
        <Grid item xs={2} sm={4} md={4} key={res.url}>
          <SearchResultCard
            key={res.url}
            content={res}
          />
        </Grid>
      ))}
    </Grid>
  )
}
