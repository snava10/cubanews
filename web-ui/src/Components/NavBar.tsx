import { AccountCircle } from "@material-ui/icons";
import { AppBar, Toolbar, Typography, IconButton, Tooltip, Avatar, Button } from "@mui/material";
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
  const handleMenu = (event: React.MouseEvent<HTMLElement>) => { };
  return (
    <div>
      <AppBar position="fixed" sx={{ boxShadow: 3 }}>
        <Toolbar className={classes.whiteBackground}>
          <Tooltip title="Open settings">
            <IconButton sx={{ p: 0 }}>
              <Avatar variant="square" src={`${process.env.PUBLIC_URL}/cuban-flag.svg`} />
            </IconButton>
          </Tooltip>

          <Typography variant="body1" fontWeight="bold" className={classes.title} sx={{ p: 1 }}>
            Cuba News
          </Typography>

          <Button
            variant="contained"
            color="success"
            sx={{ textTransform: 'none' }}
            component={Link} to="about"
          >
            Apóyanos
          </Button>
        </Toolbar>
      </AppBar>
    </div>
  );
}
