// For more information, see https://crawlee.dev/

import { Actor } from "apify";
import CibercubaCrawler from "../node_modules/cubanews-crawler/dist/cibercubaCrawler.js";

try {
  await Actor.init();
  const dataset = await Actor.openDataset("cibercuba-dataset");
  await dataset.drop();
  const crawler = new CibercubaCrawler();
  await crawler.runX();
} catch (err) {
  console.log(err);
} finally {
  await Actor.exit();
}
