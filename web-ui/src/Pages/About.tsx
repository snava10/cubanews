import { Container, Grid } from "@mui/material";
import { Box } from "@mui/system";
export function About() {
  return (
    <div>
      <Container maxWidth="lg">
        <Grid item xs={12} md={12}>
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
        <h1>Nuestra mission</h1>
        <p>
          El objetivo principal de esta web es amplificar el mensaje de la
          prensa independiente cubana, brindar un único punto de acceso a las
          últimas noticias acerca de Cuba y combatir la propaganda y
          desinformación del régimen dictatorial de La Habana. Está orientado
          principalmente a cubanos dentro de Cuba con conexiones a internet
          lentas y limitadas, pero igualmente a aquellas personas en el resto
          del mundo que no sepan de la variedad de fuentes de información
          disponibles.
        </p>
        <p>
          Este trabajo ha sido realizado completamente por voluntarios en su
          tiempo libre. Los costos de mantener el sitio son cubiertos
          completamente por fondos personales. Nuestra motivación es el amor a
          la libertad, la democracia y los derechos humanos. Como cubano
          quisiera ver a mi país libre de la dictadura comunista que lo oprime.
        </p>
        <p>
          Este es un proyecto de código abierto, si tienes conocimientos de
          programación y quieres contribuir visita{" "}
          <a href="https://github.com/snava10/cubanews">
            https://github.com/snava10/cubanews
          </a>{" "}
          o envianos un email a{" "}
          <a href="mailto:cubanews.icu@gmail.com">cubanews.icu@gmail.com</a>
        </p>
        <h3>Firmado, El equipo de Cubanews</h3>

        <h1>Our mission</h1>
        <p>
          The main purpose of this website is to amplify the message of the
          independent cuban media, provide a single entry point to find the
          latest news about Cuba and combat the propaganda and missinformation
          comming out of the dictatorial regime of Havana. Is primarly aimed to
          those inside Cuba, with limited and slow internet connections, but
          also to those around the world that may not be aware of the variaty of
          independent cuban newspappers.
        </p>
        <p>
          This work has been done by volunteers in their personal time and the
          running of the website is completely financed with personal funds. Our
          drive are our beleives of freedom, democracy and humans rights. As a
          Cuban myself I want to see my country free of the communist
          dictatorship that currently oppress it.
        </p>
        <p>
          This is an open source project so if you have programming skills and
          want to contribute please head over to{" "}
          <a href="https://github.com/snava10/cubanews">
            https://github.com/snava10/cubanews
          </a>{" "}
          or email us at{" "}
          <a href="mailto:cubanews.icu@gmail.com">cubanews.icu@gmail.com</a>
        </p>
        <h3>Signed, The Cubanews team</h3>
      </Container>
    </div>
  );
}
