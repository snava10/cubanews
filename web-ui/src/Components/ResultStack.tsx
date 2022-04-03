import { Stack } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";

// Legacy old fucky style

export function ResultStack(props: any) {
  // console.log("[Result stack] called")

  return (
    <Stack spacing={1}>
      {props.searchResults.length === 1 && props.searchResults[0].title === 'No results' ?
        // TODO: Make this a better UI.
        <p>No se encontraron resultados. Por favor modifique su criterio de busqueda e intente nuevamente.</p> :
        props.searchResults.map((res: any) => (
          <SearchResultCard
            key={res.url}
            content={res}
          />
        ))
      }
    </Stack>
  )
}
