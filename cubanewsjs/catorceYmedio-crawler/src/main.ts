// For more information, see https://crawlee.dev/

import { Actor } from "apify";
import CatorceYMedioCrawler from "../node_modules/cubanews-crawler/dist/catorceYMedioCrawler.js";

try {
  await Actor.init();
  const dataset = await Actor.openDataset("catorceymedio-dataset");
  await dataset.drop();
  const crawler = new CatorceYMedioCrawler();
  await crawler.runX();
} catch (err) {
  console.log(err);
} finally {
  await Actor.exit();
}
