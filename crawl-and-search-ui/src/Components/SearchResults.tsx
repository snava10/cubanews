const SearchResults = (searchResults: any) => {
    // console.log(searchResults);
    // console.log(searchResults.searchResults.length);
  if (searchResults.length > 0) {
    return (
      <ul>          
        {searchResults.searchResults.map((res: any) => (
          <li>
            <h5>
              <a href={res.url}>{res.title}</a>
            </h5>
          </li>
        ))}
      </ul>
    );
  }
  return (<p>The results will appear here</p>);
};

export default SearchResults;
