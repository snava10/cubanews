import { AppBar, Toolbar, Typography, IconButton, Tooltip, Avatar } from "@mui/material";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import { Link } from "react-router-dom";
import { AccountCircle } from "@material-ui/icons";

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
  const handleMenu = (event: React.MouseEvent<HTMLElement>) => { };
  return (
    <div>
      <AppBar position="fixed" sx={{ boxShadow: 3 }}>
        <Toolbar disableGutters className={classes.whiteBackground} sx={{ pl: 1, pr: 1 }}>
          <Typography variant="body1" fontWeight="bold" className={classes.title}>
            <Link to="about" className={classes.bareLink}>
              <IconButton>
                <Avatar variant="square" src={`${process.env.PUBLIC_URL}/cuban-flag.svg`} />
              </IconButton>
              Cuba News
            </Link>
          </Typography>

          {/* <Button
            variant="contained"
            color="success"
            sx={{ textTransform: 'none' }}
            component={Link} to="about"
          >
            Apóyanos
          </Button> */}

          <IconButton
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="primary">
            <AccountCircle />
          </IconButton>
        </Toolbar>
      </AppBar>
    </div>
  );
}
