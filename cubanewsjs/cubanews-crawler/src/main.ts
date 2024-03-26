// For more information, see https://crawlee.dev/
import { PlaywrightCrawler } from "crawlee";
import moment from "moment";
import { Actor } from "apify";

await Actor.init();

const domainSubdomainTileRegex = RegExp(
  `http[s]?:\/\/[\w.-]+\/[\w.-]+\/[\w.-]+`
);

function isValid(url: string): boolean {
  const sections = url.split("/");
  return !url.startsWith("https://adncuba.com/tags/") && sections.length >= 5;
}

function parseDate(rawDate: string): moment.Moment {
  return moment(rawDate.trim().split(": ")[1], "ddd, MM/DD/YYYY - HH:mm");
}

// PlaywrightCrawler crawls the web using a headless
// browser controlled by the Playwright library.
const crawler = new PlaywrightCrawler({
  // Use the requestHandler to process each of the crawled pages.
  async requestHandler({ request, page, enqueueLinks, log, pushData }) {
    const title = await page.title();
    log.info(`Title of ${request.loadedUrl} is '${title}'`);
    if (request.loadedUrl && isValid(request.loadedUrl)) {
      const rawDate = await page
        .locator("p.updated__paragraph")
        .first()
        .textContent();
      if (rawDate) {
        const momentDate = parseDate(rawDate);
        log.info(`Last updated: ${momentDate.toISOString()}`);
        // Save results as JSON to ./storage/datasets/default
        await pushData({
          title,
          url: request.loadedUrl,
          updated: momentDate.unix(),
          isoDate: momentDate.toISOString(),
        });
      }
    }
    // Extract links from the current page
    // and add them to the crawling queue.
    await enqueueLinks({
      strategy: "same-hostname",
    });
  },
  // Comment this option to scrape the full website.
  maxRequestsPerCrawl: 50,
  // Uncomment this option to see the browser window.
  // headless: false,
});

// Add first URL to the queue and start the crawl.
await crawler.run(["https://adncuba.com/noticias-de-cuba"]);

await Actor.exit();
