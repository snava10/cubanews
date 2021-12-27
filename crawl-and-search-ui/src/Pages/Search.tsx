import { useState } from "react";
import Button from "@mui/material/Button";
import { Container, Grid, Paper, Stack, TextField } from "@mui/material";
import logo from "../cuban-flag.jpg";
import { SearchResultCard } from "../Components/SearchResultCard";

export function Search() {
  const [searchResults, setSearchResults] = useState([
    // Uncomment this to test. Do not commit unncommented
    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },
    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },

    { url: "http://adncuba.com/url1", title: "bad news about cuba" },
    { url: "http://adncuba.com/url2", title: "more bad news about cuba" },
  ]);
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
      <Container maxWidth="lg" style={{ paddingTop: 10 }}>
        <Grid container spacing={1}>
          <Grid item xs={3} md={2}>
            <img src={logo} height={70} width={100} />
          </Grid>
          <Grid item xs={9} md={10}>
            <h2>Cuba Today</h2>
          </Grid>
          <Grid item xs={10}>
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
          <Grid item xs={2}>
            <Button variant="contained" onClick={search}>
              Search
            </Button>
          </Grid>
          <Grid item xs={10}>
            <Stack spacing={2}>
              {searchResults.map((res: any) => (
                // <Paper elevation={0}>
                //   <h4>
                //     <a href={res.url}>{res.title}</a>
                //   </h4>
                // </Paper>
                SearchResultCard({
                  url: res.url,
                  title: res.title
                })
              ))}
            </Stack>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
}
