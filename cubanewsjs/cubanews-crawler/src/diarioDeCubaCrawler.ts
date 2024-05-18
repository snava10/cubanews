import { PlaywrightCrawlingContext, Dictionary } from "crawlee";
import { CubanewsCrawler, saveData } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";

const newsSource = getNewsSourceByName(NewsSourceName.DIARIODECUBA);

function isValid(url: string): boolean {
  const sections = url.split("/");
  return sections.length >= 5;
}

function parseDate(rawDate: string): moment.Moment {
  moment.locale("es");
  const mDate = moment(rawDate.trim(), "DD MMMM YYYY - HH:mm Z");
  // TODO:: Sometimes the date is in the future. This is a patch to prevent that from happening.
  // It may be a parsing error.
  if (mDate.isSameOrAfter(moment.now())) {
    console.warn(`Date is in the future: ${rawDate}`);
    return moment(new Date());
  }
  return mDate;
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

    if (request.loadedUrl && isValid(request.loadedUrl)) {
      const rawDate = await page.locator("time.date").first().textContent();

      if (rawDate) {
        const momentDate = parseDate(rawDate);
        var content = await page.locator("div.content").first().textContent();
        if (!content || content?.length < 100) {
          // This is to guarantee this is a real article.
          return;
        }
        content = content
          .trim()
          .replace(/\n/g, "")
          .split(" ")
          .slice(0, 50)
          .join(" ");

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

    if (
      request.loadedUrl &&
      (newsSource.startUrls.has(request.loadedUrl) ||
        newsSource.startUrls.has(request.loadedUrl + "/"))
    ) {
      await enqueueLinks({
        globs: ["http?(s)://diariodecuba.com/*/*"],
        exclude: [
          "https://diariodecuba.com/user/login",
          "https://diariodecuba.com/de-leer/**",
        ],
        selector: "a",
      });
    }
  }
}
