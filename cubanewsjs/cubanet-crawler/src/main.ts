// For more information, see https://crawlee.dev/

import { Actor } from "apify";
import CubanetCrawler from "../../cubanews-crawler/dist/cubanetCrawler.js";

try {
  await Actor.init();
  const dataset = await Actor.openDataset("cubanet-dataset");
  await dataset.drop();
  const crawler = new CubanetCrawler();
  await crawler.runX();
} catch (err) {
  console.log(err);
} finally {
  await Actor.exit();
}
