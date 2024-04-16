import React, { useEffect } from "react";
import logo from "./logo.svg";
import "./App.css";

function App() {
  async function getDatasetIdByAppName(
    appName: string
  ): Promise<string | null> {
    try {
      const response = await fetch(
        `https://api.apify.com/v2/datasets?token=...`
      );
      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      const jsonData = await response.json();
      console.log(jsonData.data.items);
      const adncubaDataset = jsonData.data.items.filter(
        (item: any) => item.name === "adncuba-dataset"
      );

      console.log(adncubaDataset);
      return jsonData;

      // setData(jsonData);
    } catch (error: any) {
      // setError("Error fetching data: " + error.message);
      return error;
    }
  }

  useEffect(() => {
    getDatasetIdByAppName("adncuba")
      .then((value) => console.log(value))
      .catch((error) => console.error(error));
  });

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
