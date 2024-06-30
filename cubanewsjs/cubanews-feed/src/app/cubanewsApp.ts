import { Database } from "@/app/api/dataschema";
import { Kysely, PostgresDialect } from "kysely";
import { Pool } from "pg";
import { createKysely } from "@vercel/postgres-kysely";
import * as dotenv from "dotenv";

dotenv.config({
  path: process.env.ENV_FILE ?? `.env`,
});
export class CubanewsApp {
  private database: Kysely<Database>;

  constructor() {
    console.log(
      "Initialising Cubanews App for environment, ",
      process.env.LOCAL_ENV
    );
    if (process.env.LOCAL_ENV === "development") {
      console.log("Using local database");
      const dialect = new PostgresDialect({
        pool: new Pool({
          database: process.env.POSTGRES_DATABASE,
          host: process.env.POSTGRES_HOST,
          user: process.env.POSTGRES_USER,
          password: process.env.POSTGRES_PASSWORD,
          port: 9000,
          max: 10,
        }),
      });
      this.database = new Kysely<Database>({
        dialect,
      });
    } else {
      dotenv.config({
        path: `.env`,
      });
      this.database = createKysely<Database>();
    }
  }

  public get getDatabase() {
    return this.database;
  }
}

const cubanewsApp = new CubanewsApp();
export default cubanewsApp;
