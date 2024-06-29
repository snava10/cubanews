import { Database } from "@/app/api/dataschema";
import { Kysely, PostgresDialect } from "kysely";
import { Pool } from "pg";
import { createKysely } from "@vercel/postgres-kysely";
import * as dotenv from "dotenv";

if (process.env.ENV_FILE !== ".env") {
  dotenv.config({
    path: process.env.ENV_FILE ?? ".env",
  });
}

export class CubanewsApp {
  private database: Kysely<Database>;

  constructor() {
    console.log(
      "Initialising Cubanews App for environment, ",
      process.env.NODE_ENV
    );
    if (process.env.NODE_ENV === "development") {
      console.log("Using local database");
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
      this.database = new Kysely<Database>({
        dialect,
      });
    } else {
      this.database = createKysely<Database>();
    }
  }

  public get getDatabase() {
    return this.database;
  }
}

const cubanewsApp = new CubanewsApp();
export default cubanewsApp;
