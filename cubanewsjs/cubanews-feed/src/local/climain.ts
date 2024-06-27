import { Database } from "@/app/api/dataschema";
import { createKysely } from "@vercel/postgres-kysely";
import * as dotenv from "dotenv";
import { refreshFeedFromLocalSources } from "./localFeedLib";
import { Kysely, PostgresDialect } from "kysely";
import { Pool } from "pg";

dotenv.config();
const dialect = new PostgresDialect({
  pool: new Pool({
    database: "cubanews",
    host: "localhost",
    user: "postgres",
    password: "example",
    port: 9000,
    max: 10,
  }),
});

// Database interface is passed to Kysely's constructor, and from now on, Kysely
// knows your database structure.
// Dialect is passed to Kysely's constructor, and from now on, Kysely knows how
// to communicate with your database.
export const db = new Kysely<Database>({
  dialect,
});
// const db = createKysely<Database>();

(async () => {
  try {
    await refreshFeedFromLocalSources(db);
  } catch (error) {
    console.error("Error refreshing sources ", error);
  }
})();
