import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";
import { Page } from "playwright";

const newsSource = getNewsSourceByName(NewsSourceName.CIBERCUBA);

export default class CibercubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  protected override isUrlValid(url: string): boolean {
    const sections = url.split("/");
    return sections.length >= 5;
  }

  protected override async extractDate(
    page: Page
  ): Promise<moment.Moment | null> {
    const rawDate = await page
      .locator("meta[name=article:published_time]")
      .first()
      .getAttribute("content");
    if (!rawDate) {
      return null;
    }
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

  protected override async extractContent(_page: Page): Promise<string | null> {
    return null;
  }

  parseDate(rawDate: string): moment.Moment {
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
}
