/**
 * This is the ADN Cuba homepage crawler.
 * It is aiming to crawl the home page of ADN Cuba and extract all the direct links to articles
 * present in the page. Only one level.
 * The articles are encapsulated in an <article> tab and contain a link inside that fits the pattern
 * https://adncuba.com/asd/asdx
 */
import { PlaywrightCrawler } from "crawlee";
import moment from "moment";
import { Actor } from "apify";

const startPages = new Set([
  "https://adncuba.com/",
  "https://adncuba.com/noticias-de-cuba/",
]);

function isValid(url: string): boolean {
  const sections = url.split("/");
  return !url.startsWith("https://adncuba.com/tags/") && sections.length >= 5;
}

function parseDate(rawDate: string): moment.Moment {
  return moment(rawDate.trim().split(": ")[1], "ddd, MM/DD/YYYY - HH:mm");
}

export default class AdnCubaCrawler {
  crawler = new PlaywrightCrawler({
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
      // Only enque links if we are in the start page. This is basically a depth=1 crawler.
      if (request.loadedUrl && startPages.has(request.loadedUrl)) {
        await enqueueLinks({
          globs: ["http?(s)://adncuba.com/*/*"],
          exclude: ["http?(s)://adncuba.com/tags/*"],
          selector: "a",
        });
      }
    },
    // Comment this option to scrape the full website.
    maxRequestsPerCrawl: 50,
    // Uncomment this option to see the browser window.
    // headless: false,
  });

  public async run(): Promise<void> {
    await Actor.init();
    await this.crawler.run([...startPages]);
    await Actor.exit();
  }
}
