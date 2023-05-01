import { useState, useEffect } from "react";
import Button from "@mui/material/Button";
import {
  Chip,
  Container,
  Divider,
  Grid,
  Stack,
  TextField,
} from "@mui/material";
import { generateResults, ResultCard } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";
import { ResultStack } from "../Components/ResultStack";
import ReactGA from "react-ga4";
import { UaEventOptions } from "react-ga4/types/ga4";
import { NavBar } from "../Components/NavBar";
import { CopyrightRounded } from "@material-ui/icons";
import moment from "moment";
// This needs to be hardcoded to Spanish as independently of the browser language the news are in Spanish.
import "moment/locale/es";

export function Search() {
  const env = process.env.REACT_APP_ENV;
  const base_url = process.env.REACT_APP_BASE_URL;
  const [searchResults, setSearchResults] = useState<Array<ResultCard>>([]);
  const [queryString, setQueryString] = useState("");
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
    if (qs === null || qs.trim() === "") {
      const d = new Date();
      const formatDate = moment(d).format("dddd MMMM D YYYY");
      qs = formatDate;
    }
    setFirstLoad(false);
    setSearchResults([]);
    if (env === "PROD") {
      ReactGA.event({
        category: "Search",
        action: "Search",
        label: queryString,
      } as UaEventOptions);
    }
    const data = {
      query: qs,
      indices: ["cibercuba", "14ymedio", "adnCuba", "diarioDeCuba"],
      maxResultsPerIndex: 3,
      grouping: "BY_INDEX_MAX",
    };
    // TODO: Parameterize the project name name.
    fetch(`${base_url}/api/search/project/cubanews_project`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
      .then((res) => res.json())
      .then((data) => {
        console.log(data);
        setSearchResults(cleanNoResultsBlock(data));
        if (env === "DEV") {
          console.log(data);
        }
      })
      .catch((reason: any) => {
        if (env === "PROD") {
          ReactGA.event({
            category: "Error",
            action: reason,
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

  if (env === "PROD") {
    ReactGA.send("pageview");
  }

  function cleanNoResultsBlock(data: ResultCard[]): ResultCard[] {
    if (data.length === 1) {
      return data;
    }
    return data.filter((d) => d.title !== "No results :(");
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
