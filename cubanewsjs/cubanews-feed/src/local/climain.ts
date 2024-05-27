import { Database } from "@/app/api/dataschema";
import { createKysely } from "@vercel/postgres-kysely";
import * as dotenv from "dotenv";
import { refreshFeedFromLocalSources } from "./localFeedLib";

dotenv.config();
const db = createKysely<Database>();

(async () => {
  try {
    await refreshFeedFromLocalSources(db);
  } catch (error) {
    console.error("Error refreshing sources ", error);
  }
})();
