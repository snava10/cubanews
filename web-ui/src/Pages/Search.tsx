import { useState, useEffect } from "react";
import Button from "@mui/material/Button";
import { Container, Grid, TextField } from "@mui/material";
import { generateResults, ResultCard } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";
import { ResultStack } from "../Components/ResultStack";
import ReactGA from "react-ga4";
import { UaEventOptions } from "react-ga4/types/ga4";
import { NavBar } from "../Components/NavBar";
import { DonationButton } from "../Components/DonationButton";


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
          setSearchResults(generateResults(10));
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
    <div>
      <NavBar />
      <DonationButton />
      <Container maxWidth="lg">
        <Grid container spacing={0} sx={{ paddingTop: 1 }}>
          <Grid item xs={10} md={10}>
          </Grid>                  
        </Grid>
        <Grid container spacing={1}>
          {/* Name and logo */}
          <Grid item xs={12} md={12}>
            <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'center', alignItems: 'center', padding: 3 }}>
              <Box sx={{ margin: 0, paddingLeft: 1, paddingRight: 1 }}>
                <img src={`${process.env.PUBLIC_URL}/cuban-flag.svg`} height={70} width={100} alt="Cuban FLag" />
              </Box>
              <h2>Cuba News</h2>
            </Box>
          </Grid>

          {/* Search bar and button */}
          <Grid item xs={12} md={10}>
            <TextField
              onChange={handleChange}
              onKeyPress={handleKeyPress}
              value={queryString}
              label="Escribe algo que desees buscar!"
              variant="outlined"
              size="small"
              fullWidth
            />
          </Grid>
          <Grid item xs={12} md={2} sx={{ display: 'flex' }}>
            <Button sx={{ flexGrow: 1 }} variant="contained" onClick={search}>
              Buscar
            </Button>
          </Grid>

          {/* Results grid */}
          <div>
          <Grid item xs={12}>
            <ResultStack
              searchResults={searchResults}
            />
          </Grid>
          </div>
        </Grid>
      </Container>
    </div>
  );
}
