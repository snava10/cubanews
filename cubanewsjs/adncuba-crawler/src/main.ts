// For more information, see https://crawlee.dev/

import { Actor } from "apify";
import AdnCubaCrawler from "../../cubanews-crawler/dist/adncubaCrawler.js";

try {
  await Actor.init();
  const dataset = await Actor.openDataset("adncuba-dataset");
  await dataset.drop();
  const crawler = new AdnCubaCrawler();
  await crawler.runX();
} catch (err) {
  console.log(err);
} finally {
  await Actor.exit();
}
