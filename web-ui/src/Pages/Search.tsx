import { useState, useEffect } from "react";
import { AppBar, Avatar, Chip, Container, Divider, FormControl, FormControlLabel, FormGroup, FormHelperText, FormLabel, Grid, IconButton, InputBase, Paper, SwipeableDrawer, Switch, Toolbar, Typography } from "@mui/material";
import { generateResults, ResultCard } from "../Helpers/resultsGenerator";
import { Box } from "@mui/system";
import { ResultStack } from "../Components/ResultStack";
import ReactGA from "react-ga4";
import { UaEventOptions } from "react-ga4/types/ga4";
import { CopyrightRounded } from "@material-ui/icons";
import ResultMasonry from "../Components/ResultMasonry";
import { Link } from "react-router-dom";
import SearchIcon from '@mui/icons-material/Search';
import MenuIcon from '@mui/icons-material/Menu';
import React from "react";

export function Search() {
  const env = process.env.REACT_APP_ENV;
  const base_url = process.env.REACT_APP_BASE_URL;
  const [searchResults, setSearchResults] = useState<Array<ResultCard>>([]);
  const [queryString, setQueryString] = useState('');
  const [firstLoad, setFirstLoad] = useState(true);

  const [drawer, setDrawer] = React.useState({
    top: false,
    left: false,
    bottom: false,
    right: false,
  });

  const [styles, setStyles] = React.useState({
    compact: false,
    images: true,
    girdLayout: false
  })

  type Anchor = 'top' | 'left' | 'bottom' | 'right';

  const toggleDrawer =
    (anchor: Anchor, open: boolean) =>
      (event: React.KeyboardEvent | React.MouseEvent) => {
        if (
          event &&
          event.type === 'keydown' &&
          ((event as React.KeyboardEvent).key === 'Tab' ||
            (event as React.KeyboardEvent).key === 'Shift')
        ) {
          return;
        }

        setDrawer({ ...drawer, [anchor]: open });
      };

  const list = (anchor: Anchor) => (
    <Box
      sx={{ width: anchor === 'top' || anchor === 'bottom' ? 'auto' : 250 }}
      role="presentation"
      // onClick={toggleDrawer(anchor, false)}
      onKeyDown={toggleDrawer(anchor, false)}
    >
      <FormControl component="fieldset" variant="standard" sx={{ m: 2 }}>
        <FormLabel component="legend">Result cards styles</FormLabel>
        <FormGroup>
          <FormControlLabel
            control={
              <Switch checked={styles.compact} onChange={() => setStyles({ ...styles, compact: !styles.compact })} />
            }
            label="Compact"
          />
          <FormControlLabel
            control={
              <Switch checked={styles.images} onChange={() => setStyles({ ...styles, images: !styles.images })} />
            }
            label="Images"
          />
          <FormControlLabel
            control={
              <Switch checked={styles.girdLayout} onChange={() => setStyles({ ...styles, girdLayout: !styles.girdLayout })} />
            }
            label="Grid layout"
          />
        </FormGroup>
      </FormControl>
      <Divider />
      <FormControl component="fieldset" variant="standard" sx={{ m: 2 }}>
        <FormLabel component="legend">Result filters</FormLabel>
        <FormGroup>
        </FormGroup>
      </FormControl>
      <Divider />
      <FormControl component="fieldset" variant="standard" sx={{ m: 2 }}>
        <FormLabel component="legend">Support us</FormLabel>
        <FormGroup>
        </FormGroup>
      </FormControl>
    </Box>
  );

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
    <Box sx={{ mt: 12 }}>
      {/* Drawer */}
      <React.Fragment key={"right"}>
        <SwipeableDrawer
          anchor={"right"}
          open={drawer["right"]}
          onClose={toggleDrawer("right", false)}
          onOpen={toggleDrawer("right", true)}
        >
          {list("right")}
        </SwipeableDrawer>
      </React.Fragment>

      {/* App bar */}
      <AppBar position="fixed" sx={{ boxShadow: 3 }}>
        <Toolbar
          disableGutters
          sx={{
            pl: 1,
            pr: 1,
            backgroundColor: "white"
          }}
        >
          <Grid
            container
            alignItems="center"
          >
            <Grid item xs={2} sm={3} >
              <Typography variant="body1" fontWeight="bold">
                <Link to="about">
                  <IconButton>
                    <Avatar variant="square" src={`${process.env.PUBLIC_URL}/cuban-flag.svg`} />
                  </IconButton>
                </Link>
              </Typography>
            </Grid>
            <Grid item xs={8} sm={6}>
              <Paper
                component="form"
                sx={{
                  p: '2px 4px',
                  display: 'flex',
                  alignItems: 'center',
                  backgroundColor: "#f1f3f4",
                  borderRadius: 2
                }}
              >
                <IconButton type="submit" sx={{ p: '10px' }} aria-label="search">
                  <SearchIcon />
                </IconButton>
                <InputBase
                  sx={{ ml: 1, flex: 1 }}
                  placeholder="Escribe algo que desees buscar!"
                  onChange={handleChange}
                  onKeyPress={handleKeyPress}
                  value={queryString}
                />
              </Paper>
            </Grid>
            <Grid item xs={2} sm={3} sx={{ textAlign: "end" }}>
              <IconButton
                aria-label="account of current user"
                aria-controls="menu-appbar"
                aria-haspopup="true"
                color="primary"
                onClick={toggleDrawer("right", true)}>
                <MenuIcon />
              </IconButton>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>

      {/* Results grid */}
      <Container>
        <Grid item xs={12} sx={{ mt: 1 }}>
          {
            styles.girdLayout ?
              <ResultMasonry
                searchResults={searchResults}
                compact={styles.compact}
                images={styles.images}
              />
              :
              <ResultStack
                searchResults={searchResults}
                compact={styles.compact}
                images={styles.images} />
          }
        </Grid>
      </Container>

      {/* Tiler stamp */}
      <Divider sx={{ m: 4 }}>
        <Chip icon={<CopyrightRounded />} label="Cuba News" />
      </Divider>
    </Box>
  );
}
