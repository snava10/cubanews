import { PlaywrightCrawlingContext, Dictionary } from "crawlee";
import { CubanewsCrawler } from "./cubanewsCrawler.js";
import { NewsSourceName, getNewsSourceByName } from "./crawlerUtils.js";

const newsSource = getNewsSourceByName(NewsSourceName.DIARIO_DE_CUBA);

export default class DiarioDeCubaCrawler extends CubanewsCrawler {
  constructor() {
    super(newsSource);
  }

  override requestHandler(
    _context: PlaywrightCrawlingContext<Dictionary>
  ): Promise<void> {
    throw new Error("Method not implemented.");
  }
}
