import { PlaywrightCrawlingContext, Dictionary } from "crawlee";
import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";

const newsSource = getNewsSourceByName(NewsSourceName.DIARIODECUBA);

function isValid(url: string): boolean {
  const sections = url.split("/");
  return sections.length >= 5;
}

export default class DiarioDeCubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  override async requestHandler(
    context: PlaywrightCrawlingContext<Dictionary>
  ): Promise<void> {
    const { page, request, enqueueLinks, log } = context;
    const title = await page.title();
    log.info(`Title of ${request.loadedUrl} is '${title}'`);

    if (
      request.loadedUrl &&
      (newsSource.startUrls.has(request.loadedUrl) ||
        newsSource.startUrls.has(request.loadedUrl + "/"))
    ) {
      await enqueueLinks({
        globs: ["http?(s)://diariodecuba.com/*/*"],
        exclude: [],
        selector: "a",
      });
    }
  }
}
