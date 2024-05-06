import {
  Dataset,
  Dictionary,
  PlaywrightCrawler,
  PlaywrightCrawlingContext,
} from "crawlee";
import { NewsSource } from "./crawlerUtils.js";
import { Actor } from "apify";

export interface ICubanewsCrawler {
  run(): Promise<void>;
  createCrawler(): PlaywrightCrawler;
  requestHandler(context: PlaywrightCrawlingContext): Promise<void>;
}

export abstract class CubanewsCrawler implements ICubanewsCrawler {
  protected newsSource: NewsSource;
  protected crawler: PlaywrightCrawler;

  constructor(newsSource: NewsSource) {
    this.newsSource = newsSource;
    this.crawler = this.createCrawler();
  }

  get datasetName(): string {
    return this.newsSource.name + "-dataset";
  }

  get startUrls(): Set<string> {
    console.log("startUrls", this.newsSource.startUrls);
    return this.newsSource.startUrls;
  }

  createCrawler(): PlaywrightCrawler {
    return new PlaywrightCrawler({
      // Use the requestHandler to process each of the crawled pages.
      requestHandler: this.requestHandler,
      // Comment this option to scrape the full website.
      maxRequestsPerCrawl: 50,
      // Uncomment this option to see the browser window.
      // headless: false,
    });
  }

  abstract requestHandler(context: PlaywrightCrawlingContext): Promise<void>;

  async run(): Promise<void> {
    console.log(process.env.NODE_ENV);
    if (process.env.NODE_ENV === "dev") {
      await this.crawler.run([...this.startUrls]);
    } else {
      const dataset = await Actor.openDataset(this.datasetName);
      await dataset.drop();
      await this.crawler.run([...this.startUrls]);
    }
  }
}

export async function saveData(
  data: Dictionary,
  datasetName: string,
  useActor: boolean = true
): Promise<void> {
  if (useActor) {
    const dataset = await Actor.openDataset(datasetName);
    await dataset.pushData(data);
  } else {
    await Dataset.pushData(data);
  }
}
