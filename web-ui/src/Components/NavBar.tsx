import { AccountCircle } from "@material-ui/icons";
import { AppBar, Toolbar, Typography, IconButton } from "@mui/material";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import { Link } from "react-router-dom";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
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
  })
);

export function NavBar() {
  const classes = useStyles();
  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {};
  return (
    <div>
      <AppBar position="static" sx={{ boxShadow: 0 }}>
        <Toolbar className={classes.whiteBackground}>
          <Typography variant="body1" className={classes.title}>
            <Link to="about" className={classes.bareLink}>
              Cubanews
            </Link>            
          </Typography>          
          <IconButton
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="primary"
          >
            <AccountCircle />
          </IconButton>
        </Toolbar>
      </AppBar>
    </div>
  );
}
