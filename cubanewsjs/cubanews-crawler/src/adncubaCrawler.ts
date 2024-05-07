/**
 * This is the ADN Cuba homepage crawler.
 * It is aiming to crawl the home page of ADN Cuba and extract all the direct links to articles
 * present in the page. Only one level.
 * The articles are encapsulated in an <article> tab and contain a link inside that fits the pattern
 * https://adncuba.com/asd/asdx
 */
import { PlaywrightCrawlingContext } from "crawlee";
import moment from "moment";
import { CubanewsCrawler, saveData } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";

function isValid(url: string): boolean {
  const sections = url.split("/");
  return !url.startsWith("https://adncuba.com/tags/") && sections.length >= 5;
}

function parseDate(rawDate: string): moment.Moment {
  return moment(rawDate.trim().split(": ")[1], "ddd, MM/DD/YYYY - HH:mm");
}

const newsSource = getNewsSourceByName(NewsSourceName.ADNCUBA);

export default class AdnCubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  override async requestHandler(
    context: PlaywrightCrawlingContext
  ): Promise<void> {
    const { page, request, enqueueLinks, log } = context;
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

        var content = await page.locator(".text-long").textContent();
        if (content) {
          content = content
            .trim()
            .replace(/\n/g, "")
            .split(" ")
            .slice(0, 50)
            .join(" ");
        }
        // Save results as JSON to ./storage/datasets/default
        if (newsSource) {
          await saveData(
            {
              title,
              url: request.loadedUrl,
              updated: momentDate.unix(),
              isoDate: momentDate.toISOString(),
              content: content,
              source: newsSource.name,
            },
            newsSource.datasetName,
            process.env.NODE_ENV !== "dev"
          );
        }
      }
    }
    // Extract links from the current page
    // and add them to the crawling queue.
    // Only enque links if we are in the start page. This is basically a depth=1 crawler.
    if (
      request.loadedUrl &&
      (newsSource.startUrls.has(request.loadedUrl) ||
        newsSource.startUrls.has(request.loadedUrl + "/"))
    ) {
      await enqueueLinks({
        globs: ["http?(s)://adncuba.com/*/*"],
        exclude: ["http?(s)://adncuba.com/tags/*"],
        selector: "a",
      });
    }
  }
}
