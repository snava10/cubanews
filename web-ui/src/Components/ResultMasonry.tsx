import * as React from 'react';
import { Masonry } from "@mui/lab";
import { SearchResultCard } from "./SearchResultCard";

// It would be great if you could fix this component, thanks.

export default function ResultMasonry(props: any) {
  return (
    <Masonry columns={{ sx: 1, sm: 2, md: 3 }} spacing={2}>
      {props.searchResults.length === 1 && props.searchResults[0].title === 'No results' ?
        <p>No se encontraron resultados. Por favor modifique su criterio de busqueda e intente nuevamente.</p> :
        props.searchResults.map((res: any) => (
          <SearchResultCard
            key={res.url}
            content={res}
          />
        ))
      }
    </Masonry>
  );
}
