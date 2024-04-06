// For more information, see https://crawlee.dev/
import { PlaywrightCrawler } from "crawlee";
import { Actor } from "apify";

import { router } from "./routes.js";

const startUrls = ["https://www.14ymedio.com/cuba"];

await Actor.init();

const crawler = new PlaywrightCrawler({
  // proxyConfiguration: new ProxyConfiguration({ proxyUrls: ['...'] }),
  requestHandler: router,
  // Comment this option to scrape the full website.
  maxRequestsPerCrawl: 20,
});

await crawler.run(startUrls);

await Actor.exit();
