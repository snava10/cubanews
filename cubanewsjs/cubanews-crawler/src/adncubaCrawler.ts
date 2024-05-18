/**
 * This is the ADN Cuba homepage crawler.
 * It is aiming to crawl the home page of ADN Cuba and extract all the direct links to articles
 * present in the page. Only one level.
 * The articles are encapsulated in an <article> tab and contain a link inside that fits the pattern
 * https://adncuba.com/asd/asdx
 */
import moment from "moment";
import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import { Page } from "playwright";

const newsSource = getNewsSourceByName(NewsSourceName.ADNCUBA);

export default class AdnCubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
    this.enqueueLinkOptions = {
      globs: ["http?(s)://adncuba.com/*/*"],
      exclude: ["http?(s)://adncuba.com/tags/*"],
      selector: "a",
    };
  }

  protected override isUrlValid(url: string): boolean {
    const sections = url.split("/");
    return !url.startsWith("https://adncuba.com/tags/") && sections.length >= 5;
  }

  protected override async extractDate(
    page: Page
  ): Promise<moment.Moment | null> {
    const rawDate = await page
      .locator("p.updated__paragraph")
      .first()
      .textContent();
    if (!rawDate) {
      return null;
    }
    moment.locale("es");
    const mDate = moment(
      rawDate.trim().split(": ")[1],
      "ddd, MM/DD/YYYY - HH:mm"
    );
    // TODO:: Sometimes the date is in the future. This is a patch to prevent that from happening.
    // It may be a parsing error.
    if (mDate.isSameOrAfter(moment.now())) {
      console.warn(`Date is in the future: ${rawDate}`);
      return moment(new Date());
    }
    return mDate;
  }

  protected override async extractContent(page: Page): Promise<string | null> {
    var content = await page.locator(".text-long").textContent();
    if (content) {
      content = content
        .trim()
        .replace(/\n/g, "")
        .split(" ")
        .slice(0, 50)
        .join(" ");
    }
    return content;
  }
}
