import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";
import { Page } from "playwright";

const newsSource = getNewsSourceByName(NewsSourceName.CIBERCUBA);

export default class CibercubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
    this.enqueueLinkOptions = {
      globs: ["http?(s)://www.cibercuba.com/noticias/*"],
      exclude: ["https://www.cibercuba.com/buscador*"],
      selector: "a",
    };
  }

  protected override isUrlValid(url: string): boolean {
    const sections = url.split("/");
    return sections.length >= 5;
  }

  protected override async extractDate(
    page: Page
  ): Promise<moment.Moment | null> {
    var rawDate = await page
      .locator("div.cibercuba-article-author > p > strong:nth-child(1)")
      .first()
      .textContent();
    console.log(rawDate);
    if (!rawDate) {
      return null;
    }
    rawDate = rawDate.replace("(GMT-5)", "-0500");
    moment.locale("es");
    const mDate = moment(rawDate.trim(), "DD/MM/YYYY - h:mma (ZZ)");
    // TODO:: Sometimes the date is in the future. This is a patch to prevent that from happening.
    // It may be a parsing error.
    if (mDate.isSameOrAfter(moment.now())) {
      console.warn(`Date is in the future: ${rawDate}`);
      return moment(new Date());
    }
    return mDate;
  }

  protected override async extractContent(page: Page): Promise<string | null> {
    return await page.locator("#textolimpio").innerText();
  }
}
