import { useState } from "react";
import Button from "@mui/material/Button";
import { Container, Grid, Paper, Stack, TextField } from "@mui/material";
import logo from "../cuban-flag.jpg";
import { SearchResultCard } from "../Components/SearchResultCard";
import { generateResults } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";

export function Search() {
  // const [searchResults, setSearchResults] = useState([
  //   // Uncomment this to test. Do not commit unncommented
  //   { url: "http://adncuba.com/url1", title: "bad news about cuba" },
  //   { url: "http://adncuba.com/url2", title: "more bad news about cuba" }
  // ]);

  const [searchResults, setSearchResults] = useState(generateResults(10));

  const [queryString, setQueryString] = useState("");

  function search() {
    fetch(`http://localhost:8080/api/search/1234?query=${queryString}`)
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
            <Stack spacing={1} sx={{ paddingTop: 2,paddingBottom: 2 }}>
              {searchResults.map((res: any) => (
                // <Paper elevation={0}>
                //   <h4>
                //     <a href={res.url}>{res.title}</a>
                //   </h4>
                // </Paper>
                <SearchResultCard
                  key={res.url}
                  content={res}
                />
              ))}
            </Stack>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
}
