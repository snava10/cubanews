import React from "react";
import Link from "next/link";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import { Avatar, IconButton } from "@mui/material";
import { usePathname } from "next/navigation";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

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
    usePathname() === "/" ? (
      <Typography
        variant="body1"
        fontWeight="bold"
        style={{
          color: "#202124",
        }}
      >
        <Link href="/about">
          <IconButton>
            <Avatar variant="square" src="./cuban-flag.svg" />
          </IconButton>
          Cuba News
        </Link>
      </Typography>
    ) : (
      <Typography variant="body1" fontWeight="bold">
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
