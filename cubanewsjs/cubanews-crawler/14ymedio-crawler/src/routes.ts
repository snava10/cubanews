import { createPlaywrightRouter } from "crawlee";

export const router = createPlaywrightRouter();

router.addDefaultHandler(async ({ enqueueLinks, log }) => {
  log.info(`enqueueing new URLs`);
  await enqueueLinks({
    globs: ["https://www.14ymedio.com/cuba/**"],
    label: "article",
  });
});

router.addHandler("article", async ({ request, page, log, pushData }) => {
  const title = await page.title();
  log.info(`${title}`, { url: request.loadedUrl });

  await pushData({
    url: request.loadedUrl,
    title,
  });
});
