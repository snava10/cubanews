import { refreshFeedFromLocalSources } from "./localFeedLib";
import { CubanewsApp } from "@/app/cubanewsApp";

(async () => {
  try {
    const cubanewsApp = new CubanewsApp();
    const db = cubanewsApp.getDatabase;
    await refreshFeedFromLocalSources(db);
  } catch (error) {
    console.error("Error refreshing sources ", error);
  }
})();
