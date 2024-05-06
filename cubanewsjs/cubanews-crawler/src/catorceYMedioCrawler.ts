import { PlaywrightCrawlingContext, Dictionary } from "crawlee";
import { CubanewsCrawler, saveData } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";

const newsSource = getNewsSourceByName(NewsSourceName.CATORCEYMEDIO);

function parseDate(rawDate: string): moment.Moment {
  moment.locale("es");
  return moment(rawDate, "DD [de] MMMM YYYY - HH:mm");
}

function isValid(url: string): boolean {
  const sections = url.split("/");
  return sections.length >= 5;
}

export default class CatorceYMedioCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  override async requestHandler(
    context: PlaywrightCrawlingContext<Dictionary>
  ): Promise<void> {
    const { page, request, enqueueLinks, log } = context;
    const title = await page.title();

    if (request.loadedUrl && isValid(request.loadedUrl)) {
      log.info(`Title of ${request.loadedUrl} is '${title}'`);
      const rawDate = await page
        .locator(".timestamp-atom")
        .first()
        .textContent();
      console.log(rawDate);
      if (rawDate) {
        const momentDate = parseDate(rawDate);
        if (newsSource) {
          await saveData(
            {
              title,
              url: request.loadedUrl,
              updated: momentDate.unix(),
              isoDate: momentDate.toISOString(),
              // content: content,
            },
            newsSource.datasetName,
            process.env.NODE_ENV !== "dev"
          );
        }
      }
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
