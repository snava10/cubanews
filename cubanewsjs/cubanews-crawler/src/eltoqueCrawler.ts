import { Moment } from "moment";
import { Page } from "playwright";
import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";
import moment from "moment";

export default class ElToqueCrawler extends CubanewsCrawler {
  constructor() {
    super(getNewsSourceByName(NewsSourceName.ELTOQUE));
    this.enqueueLinkOptions = {
      globs: ["http?(s)://eltoque.com/*"],
      exclude: [
        "https://eltoque.com/search",
        "https://eltoque.com/sobre-nosotros",
        "https://eltoque.com/multimedia",
        "https://eltoque.com/coverturas",
        "https://eltoque.com/revistas",
        "https://eltoque.com/newsletters",
        "https://eltoque.com/tasas-de-cambio-de-moneda-en-cuba-hoy*",
        "https://eltoque.com/especiales",
      ],
      selector: "div.container-fluid a",
    };
  }

  protected override isUrlValid(url: string): boolean {
    return !this.newsSource.startUrls.has(url);
  }

  protected override async extractDate(page: Page): Promise<Moment | null> {
    const rawDate = await page
      .locator("p.sc-5ff5bade-4.ciNFMa")
      .first()
      .textContent();
    if (!rawDate) {
      return null;
    }
    moment.locale("es");
    const mDate = moment(rawDate.trim(), "D / MMMM / YYYY");
    if (mDate.isSameOrAfter(moment.now())) {
      console.warn(`Date is in the future: ${rawDate}`);
      return moment(new Date());
    }
    return mDate;
  }

  protected override async extractContent(page: Page): Promise<string | null> {
    var content = await page
      .locator("article > div.sc-5ff5bade-8.cDGUar")
      .textContent();
    if (content) {
      content = content = content
        .trim()
        .replace(/\n/g, "")
        .split(" ")
        .slice(0, 50)
        .join(" ");
    }
    return content;
  }
}
