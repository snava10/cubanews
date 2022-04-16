import * as React from 'react';
import { Masonry } from "@mui/lab";
import { SearchResultCard } from "./SearchResultCard";

/**
 * Masonry problems with weird spacing behaviour:
 * PROBLEM: When the spacing property > 0, it uses negative margin to space elements. This creates weird spaces between the grid and the grid's parent component.
 * SOLUTION: in the sx properties, set margin to 0 and margin top to -1. It still adds some spaces on the sides but at least the entire grid is centred now and most importantly, at the expected height (no weird top margin). 
 */

export default function ResultMasonry(props: any) {
  return (
    <Masonry
      columns={{ xs: 1, sx: 1, sm: 2, md: 3, lg: 3, xl: 3 }}
      spacing={2}
      sx={{margin: 0, mt: -1}}>
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
