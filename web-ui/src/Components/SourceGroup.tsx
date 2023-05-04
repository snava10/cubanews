import Stack from "@mui/material/Stack";
import { ResultCard } from "../Helpers/resultsGenerator";
import { SearchResultCard } from "./SearchResultCard";
import { Box } from "@mui/material";

export function SourceGroup(props: { source: string; cards: ResultCard[] }) {
  return (
    <Stack direction="row" spacing={1}>
      {props.cards.map((card: ResultCard) => (
        <SearchResultCard
          key={card.url}
          content={card}
          bucketName={props.source}
        />
      ))}
    </Stack>
  );
}
