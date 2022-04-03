import { Chip, Container, Divider, Grid, Typography } from "@mui/material";
import { Box } from "@mui/system";
import { UaEventOptions } from "react-ga4/types/ga4";
import ReactGA from "react-ga4";
import { CopyrightRounded } from "@material-ui/icons";

export function About() {
  const env = process.env.REACT_APP_ENV;

  if (env === 'PROD') {
    ReactGA.event({
      category: 'AboutUs',
      action: 'AboutUs'
    } as UaEventOptions);
  }

  return (
    <Box>
      <Container>
        <Grid item xs={12}>
          <Box
            sx={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              padding: 3,
            }}
          >
            <Box sx={{ margin: 0, paddingLeft: 1, paddingRight: 1 }}>
              <img
                src={`${process.env.PUBLIC_URL}/cuban-flag.svg`}
                height={70}
                width={100}
                alt="Cuban FLag"
              />
            </Box>
            <h2>Cuba News</h2>
          </Box>
        </Grid>

        {/* Spanish */}
        <Typography variant="h3" gutterBottom>
          Nuestra mission
        </Typography>

        <Typography variant="body1" paragraph>
          El objetivo principal de esta web es amplificar el mensaje de la
          prensa independiente cubana, brindar un único punto de acceso a las
          últimas noticias acerca de Cuba y combatir la propaganda y
          desinformación del régimen dictatorial de La Habana. Está orientado
          principalmente a cubanos dentro de Cuba con conexiones a internet
          lentas y limitadas, pero igualmente a aquellas personas en el resto
          del mundo que no sepan de la variedad de fuentes de información
          disponibles.
        </Typography>

        <Typography variant="body1" paragraph>
          Este trabajo ha sido realizado completamente por voluntarios en su
          tiempo libre. Los costos de mantener el sitio son cubiertos
          completamente por fondos personales. Nuestra motivación es el amor a
          la libertad, la democracia y los derechos humanos. Como cubano
          quisiera ver a mi país libre de la dictadura comunista que lo oprime.
        </Typography>

        <Typography variant="body1" paragraph>
          Este es un proyecto de código abierto, si tienes conocimientos de
          programación y quieres contribuir visita{" "}
          <a href="https://github.com/snava10/cubanews">
            https://github.com/snava10/cubanews
          </a>{" "}
          o envianos un email a{" "}
          <a href="mailto:cubanews.icu@gmail.com">cubanews.icu@gmail.com</a>
        </Typography>

        <Typography variant="subtitle2" paragraph>
          Firmado, El equipo de Cuba News
        </Typography>

        {/* English */}
        <Typography variant="h3" gutterBottom>
          Our mission
        </Typography>

        <Typography variant="body1" paragraph>
          The main purpose of this website is to amplify the message of the independent Cuban media, provide a single entry point to find the latest news about Cuba and combat the propaganda and misinformation spread by the dictatorial regime in Havana. Cuba News is primarily aimed at those inside Cuba with limited and slow internet access, but also at those around the world that may not be aware of the variety of independent Cuban newspapers.
        </Typography>

        <Typography variant="body1" paragraph>
          This work has been done by volunteers in their free time. The running of the website is fully financed with personal funds. Our drive is our belief in freedom, democracy and humans rights. As a Cuban myself, I want to see my country free of the communist dictatorship that currently oppresses it.
        </Typography>

        <Typography variant="body1" paragraph>
          This is an open-source project, so if you have programming skills and want to contribute, please head over to{" "}
          <a href="https://github.com/snava10/cubanews">
            https://github.com/snava10/cubanews
          </a>{" "}
          or email us at{" "}
          <a href="mailto:cubanews.icu@gmail.com">cubanews.icu@gmail.com</a>
        </Typography>

        <Typography variant="subtitle2">
          Signed, The Cuba News team
        </Typography>
      </Container>
      <Divider sx={{ m: 4 }}>
        <Chip icon={<CopyrightRounded />} label="Cuba News" />
      </Divider>
    </Box>
  );
}
