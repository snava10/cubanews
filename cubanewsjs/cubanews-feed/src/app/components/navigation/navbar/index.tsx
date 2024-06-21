import React from "react";
import Link from "next/link";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/joy/Typography";
import { Avatar, IconButton } from "@mui/material";
import { usePathname } from "next/navigation";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { Box, Stack } from "@mui/joy";

const styles = {
  root: {
    flexGrow: 1,
  },
  title: {
    flexGrow: 1,
    color: "#202124",
  },
  whiteBackground: {
    backgroundColor: "white",
  },
  bareLink: {
    textDecoration: "none",
    color: "#202124",
  },
};

const Navbar = () => {
  var leftComponent =
    usePathname() === "/home" ? (
      <Link href="/about">
        <Stack direction="row">
          <IconButton>
            <Avatar variant="square" src="./cuban-flag.svg" />
          </IconButton>
          <Stack direction="row" style={{ alignItems: "center" }}>
            <Typography level="h3" style={{ color: "#CC0D0D" }}>
              Cuba
            </Typography>
            <Typography level="h3" style={{ color: "#002590" }}>
              News
            </Typography>
          </Stack>
        </Stack>
      </Link>
    ) : (
      <Typography level="body-md" fontWeight="bold">
        <Link href="/">
          <IconButton>
            <ArrowBackIcon></ArrowBackIcon>
          </IconButton>
        </Link>
      </Typography>
    );
  return (
    <>
      <div>
        <AppBar
          position="static"
          style={{
            backgroundColor: "white",
          }}
        >
          <Toolbar>{leftComponent}</Toolbar>
        </AppBar>
      </div>
    </>
  );
};

export default Navbar;
