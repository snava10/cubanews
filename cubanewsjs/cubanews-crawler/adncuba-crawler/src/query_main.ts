import * as apify from "apify";
import { getData } from "./adncuba_crawler.js";

async function getDatasetIdByAppName(appName: string): Promise<string | null> {
  // Initialize Apify client
  const client = new apify.ApifyClient({
    token: process.env.APIFY_TOKEN,
  });

  try {
    // Get list of datasets
    const datasetCollectionClient = client.datasets();
    // Gets (or creates, if it doesn't exist) a dataset with the name of my-dataset.
    const listDatasets = await datasetCollectionClient.list();

    // Find dataset associated with the provided app name
    const dataset = listDatasets.items.find((dataset) =>
      dataset.name?.startsWith(appName)
    );

    // If dataset found, return its ID
    if (dataset) {
      return dataset.id;
    } else {
      // If no dataset found, return null
      return null;
    }
  } catch (error) {
    // Handle errors
    console.error("Error fetching datasets:", error);
    throw error;
  }
}

async function readApifyDatasetByAppName(appName: string): Promise<any[]> {
  // Get dataset ID by app name
  const datasetId = await getDatasetIdByAppName(appName);

  if (!datasetId) {
    console.error(`No dataset found for app name: ${appName}`);
    return [];
  }

  // Read dataset using the obtained dataset ID
  return readApifyDataset(datasetId);
}

// Function to read dataset given dataset ID
async function readApifyDataset(datasetId: string): Promise<any[]> {
  // Initialize Apify client
  const client = new apify.ApifyClient({
    token: process.env.APIFY_TOKEN,
  });

  try {
    // Get dataset items
    const { items } = await client.dataset(datasetId).listItems();

    // Return dataset items
    return items;
  } catch (error) {
    // Handle errors
    console.error("Error reading dataset:", error);
    throw error;
  }
}

// Example usage
const appName = "adncuba";
// readApifyDatasetByAppName(appName)
//   .then((data) => {
//     console.log("Dataset items:", data);
//   })
//   .catch((error) => {
//     console.error("Failed to read dataset:", error);
//   });

getData()
  .then((data) => {
    console.log("Dataset items:", data);
  })
  .catch((error) => {
    console.error("Failed to read dataset:", error);
  });
