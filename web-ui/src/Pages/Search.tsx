import { useState, useEffect } from "react";
import Button from "@mui/material/Button";
import { Chip, Container, Divider, Grid, Stack, TextField } from "@mui/material";
import { generateResults, ResultCard } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";
import { ResultStack } from "../Components/ResultStack";
import ReactGA from "react-ga4";
import { UaEventOptions } from "react-ga4/types/ga4";
import { NavBar } from "../Components/NavBar";
import { CopyrightRounded } from "@material-ui/icons";

export function Search() {
  const env = process.env.REACT_APP_ENV;
  const base_url = process.env.REACT_APP_BASE_URL;
  const [searchResults, setSearchResults] = useState<Array<ResultCard>>([]);
  const [queryString, setQueryString] = useState('');
  const [firstLoad, setFirstLoad] = useState(true);


  function search() {

    // uncomment and use pattern above to update the state.
    // timeout(2000).then(() => {
    //   console.log("Timeout ended - [Search] state should be updated")
    //   setSearchResults(generateResults(10));
    //   // setSearchResults([{title: 'No results'} as ResultCard]);
    // })

    // Clear the search results
    // TODO: Put a searching annimation
    let qs = queryString;
    if (qs === null || qs.trim() === '') {
      qs = 'cuba';
    }
    setFirstLoad(false);
    setSearchResults([]);
    if (env === 'PROD') {
      ReactGA.event({
        category: 'Search',
        action: 'Search',
        label: queryString
      } as UaEventOptions);
    }

    // TODO: Parameterize the index name.
    fetch(`${base_url}/api/search/name/cubanews?query=${qs}`)
      .then((res) => res.json())
      .then((data) => {
        setSearchResults(data);
        if (env === "DEV") {
          console.log(data);
        }
      })
      .catch((reason: any) => {
        if (env === 'PROD') {
          ReactGA.event({
            category: 'Error',
            action: reason
          });
        } else {
          setSearchResults([]);
          console.log(reason);
        }
      });
  }

  function handleChange(event: any) {
    setQueryString(event.target.value);
  }

  function handleKeyPress(event: any) {
    if (event.code === "Enter") {
      search();
    }
  }

  useEffect(() => {
    if (firstLoad) {
      search();
    }
  });

  if (env === 'PROD') {
    ReactGA.send("pageview");
  }

  return (
    <Box sx={{ mt: 12 }}>
      <NavBar inSearchPage={true} />
      <Container>
        <Grid container spacing={1}>
          {/* Search bar and button */}
          <Grid item xs={12}>
            <Stack direction={{ xs: "column", md: "row" }} spacing={1}>
              <TextField
                onChange={handleChange}
                onKeyPress={handleKeyPress}
                value={queryString}
                label="Escribe algo que desees buscar!"
                variant="outlined"
                size="small"
                fullWidth
              />
              <Button sx={{ flexGrow: 1 }} variant="contained" onClick={search}>
                Buscar
              </Button>
            </Stack>
          </Grid>

          {/* Results grid */}
          <Grid item xs={12} sx={{ mt: 1 }}>
            <ResultStack searchResults={searchResults} />
            {/* <ResultMasonry searchResults={searchResults} /> */}
          </Grid>
        </Grid>
      </Container>
      <Divider sx={{ m: 4 }}>
        <Chip icon={<CopyrightRounded />} label="Cuba News" />
      </Divider>
    </Box>
  );
}
