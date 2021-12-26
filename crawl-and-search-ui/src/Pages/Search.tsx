import SearchResults from "../Components/SearchResults";
import React, { useState } from "react";

export function Search() {
  const [searchResults, setSearchResults] = useState([]);
  const [queryString, setQueryString] = useState("");

  function search() {
    fetch(`http://localhost:8080/api/search/1234?query=${queryString}`)
      .then((res) => res.json())
      .then((data) => {
        setSearchResults(data);
        console.log(data);
      })
      .catch(console.log);
  }

  function handleChange(event: any) {
    setQueryString(event.target.value);
  }

  return (
    <div>
      <input
        placeholder="Type something you want to search!"
        type="text"
        value={queryString}
        onChange={handleChange}
      />
      <button onClick={search}>Search</button>
      <ul>
        {searchResults.map((res: any) => (
          <li>
            <h5>
              <a href={res.url}>{res.title}</a>
            </h5>
          </li>
        ))}
      </ul>
    </div>
  );
}
