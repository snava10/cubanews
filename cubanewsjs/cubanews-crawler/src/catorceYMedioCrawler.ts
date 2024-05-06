import { PlaywrightCrawlingContext, Dictionary } from "crawlee";
import { CubanewsCrawler, saveData } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";

const newsSource = getNewsSourceByName(NewsSourceName.CATORCEYMEDIO);

export default class CatorceYMedioCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  override async requestHandler(
    context: PlaywrightCrawlingContext<Dictionary>
  ): Promise<void> {
    const { page, request, enqueueLinks, log } = context;
    const title = await page.title();
    log.info(`Title of ${request.loadedUrl} is '${title}'`);
    log.info(`${title}`, { url: request.loadedUrl });

    if (newsSource) {
      await saveData(
        {
          title,
          url: request.loadedUrl,
          // updated: momentDate.unix(),
          // isoDate: momentDate.toISOString(),
          // content: content,
        },
        newsSource.datasetName,
        process.env.NODE_ENV !== "dev"
      );
    }

    if (
      request.loadedUrl &&
      (newsSource.startUrls.has(request.loadedUrl) ||
        newsSource.startUrls.has(request.loadedUrl + "/"))
    ) {
      await enqueueLinks({
        globs: ["http?(s)://www.14ymedio.com/cuba/*"],
        exclude: [],
        selector: "a",
      });
    }
  }
}
