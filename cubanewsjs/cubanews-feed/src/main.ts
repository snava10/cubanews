import { Actor } from "apify";
import express from "express";

await Actor.init();
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const {
  APIFY_CONTAINER_PORT,
  APIFY_CONTAINER_URL,
  APIFY_DEFAULT_KEY_VALUE_STORE_ID,
} = process.env;

app.get("/feed", (_req, res) => {
  res.send(
    JSON.stringify({
      banter: "Greetings from the cubanews feed service :)",
      data: [],
    })
  );
});

if (APIFY_CONTAINER_PORT) {
  app.listen(APIFY_CONTAINER_PORT, () => {
    console.log(
      `Cubanews news feed is listening at URL ${APIFY_CONTAINER_URL}.`
    );
  });
} else {
  app.listen(3001, () => {
    console.log(`Cubanews news feed is listening at URL localhost.`);
  });
}
