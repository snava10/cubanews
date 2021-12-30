import { useState } from "react";
import Button from "@mui/material/Button";
import { Container, Grid, TextField } from "@mui/material";
import logo from "../cuban-flag.jpg";
import { generateResults, ResultCard } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";
import { ResultStack } from "../Components/ResultStack";
import { ResultGrid } from "../Components/ResultGrid";

function timeout(ms: any) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

export function Search() {
  console.log("[Search] called")
  const [searchResults, setSearchResults] = useState<Array<ResultCard>>([]);
  const [queryString, setQueryString] = useState("");

  function search() {
    console.log("Search button pressed")

    // timeout(2000).then(() => {
    //   console.log("Timeout ended - [Search] state should be updated")
    //   setSearchResults(generateResults(10));
    // })

    // TODO: uncomment and use pattern above to update the state.
    fetch(`http://crawlandsearch.info/api/search/1234?query=${queryString}`)
      .then((res) => res.json())
      .then((data) => {
        setSearchResults(data);
        console.log(data);
      })
      .catch(console.log);
  }

  function handleChange(event: any) {
    setQueryString(event.target.value);
  }

  function handleKeyPress(event: any) {
    if (event.code === "Enter") {
      search();
    }
  }

  return (
    <div>
      <Container maxWidth="lg">
        <Grid container spacing={1}>
          {/* Name and logo */}
          <Grid item xs={12} md={12}>
            <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'center', padding: 3 }}>
              <img src={logo} height={70} width={100} />
              <h2>Cuba Today</h2>
            </Box>
          </Grid>

          {/* Search bar and button */}
          <Grid item xs={12} md={10}>
            <TextField
              onChange={handleChange}
              onKeyPress={handleKeyPress}
              value={queryString}
              label="Type something you want to search!"
              variant="outlined"
              size="small"
              fullWidth
            />
          </Grid>
          <Grid item xs={12} md={2} sx={{ display: 'flex' }}>
            <Button sx={{ flexGrow: 1 }} variant="contained" onClick={search}>
              Search
            </Button>
          </Grid>

          {/* Results grid */}
          <Grid item xs={12}>
            <ResultStack
              searchResults={searchResults}
            />
          </Grid>
        </Grid>
      </Container>
    </div>
  );
}
