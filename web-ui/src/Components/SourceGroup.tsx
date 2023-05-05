import Stack from "@mui/material/Stack";
import { ResultCard } from "../Helpers/resultsGenerator";
import { SearchResultCard } from "./SearchResultCard";
import { Box } from "@mui/material";
import { GNewsInspiredCard } from "./GNewsInspiredCard";

export function SourceGroup(props: { source: string; cards: ResultCard[] }) {
  return (
    <Stack direction="row" spacing={1}>
      {props.cards.map((card: ResultCard) => (
        <GNewsInspiredCard content={card} />
      ))}
    </Stack>
  );
}
