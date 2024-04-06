#!/usr/bin/env node

// Importing necessary modules
import yargs from "yargs";
import AdnCubaCrawler from "../adncuba_crawler.ts";

// Define the version of your application
const VERSION = "1.0.0";

// Create a command-line interface using yargs
const cli = yargs(process.argv.slice(2))
  .usage("$0 [args]", "My Simple Application")
  .version("version", "Show version number", VERSION)
  .alias("v", "version")
  .help();

// Define a command to accept a string argument
cli.command(
  "start [crawler]",
  "Start the crawler",
  (yargs) => {
    yargs.positional("crawler", {
      describe: "The crawler to use",
      type: "string",
    });
  },
  (argv) => {
    const crawler = argv.crawler;
    if (crawler === "adncuba") {
      const c = new AdnCubaCrawler();
      c.run();
    } else {
      console.log("Invalid crawler name");
    }
  }
);

// Parse the command-line arguments
cli.parse();
