import { Moment } from "moment";
import { Page } from "playwright";
import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";

export default class CubanetCrawler extends CubanewsCrawler {
  constructor() {
    super(getNewsSourceByName(NewsSourceName.CUBANET));
    this.enqueueLinkOptions = {
      globs: ["http?(s)://www.cubanet.org/*/*"],
      selector: "a",
    };
  }

  protected override isUrlValid(url: string): boolean {
    const sections = url.split("/");
    return (
      sections.length >= 6 &&
      !url.startsWith("https://www.cubanet.org/categoria/") &&
      !url.startsWith("https://www.cubanet.org/author/") &&
      !url.startsWith("https://www.cubanet.org/tags/") &&
      url !== "https://www.cubanet.org/tag/reportajes-investigativos/" &&
      url !== "https://www.cubanet.org/htdocs/oldies.html"
    );
  }

  protected override async extractDate(page: Page): Promise<Moment | null> {
    if (page.url().includes("/deportes/")) {
      return this.extractSportsPageDate(page);
    }
    const rawDate = await page
      .locator("div.jeg_meta_date > a")
      .first()
      .textContent();

    if (!rawDate) {
      return null;
    }
    moment.locale("es");
    const mDate = moment(rawDate, "dddd, D [de] MMMM, YYYY h:mm a");
    // TODO:: Sometimes the date is in the future. This is a patch to prevent that from happening.
    // It may be a parsing error.
    if (mDate.isSameOrAfter(moment.now())) {
      console.warn(`Date is in the future: ${rawDate}`);
      return moment(new Date());
    }
    return mDate;
  }

  protected override async extractContent(page: Page): Promise<string | null> {
    if (page.url().includes("/deportes/")) {
      return await page
        .locator("div.elementor-widget-container")
        .first()
        .textContent();
    }
    const content = await page.locator("div.content-inner").textContent();
    return content;
  }

  private async extractSportsPageDate(page: Page): Promise<Moment | null> {
    const date = await page
      .locator("span.elementor-post-info__item--type-date")
      .first()
      .textContent();
    const time = await page
      .locator("span.elementor-post-info__item--type-time")
      .first()
      .textContent();
    if (!date || !time) {
      return null;
    }

    moment.locale("es");
    const parsedDate = moment(`${date} ${time}`, "MMMM D, YYYY h:mm A");
    return parsedDate;
  }
}
