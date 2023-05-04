import { Box, CircularProgress, Stack } from "@mui/material";
import { SearchResultCard } from "./SearchResultCard";
import { ResultCard } from "../Helpers/resultsGenerator";
import { SourceGroup } from "./SourceGroup";
import React from "react";

// We go with this while the masonry component is fucky.

export function ResultStack(props: any) {
  // console.log("[Result stack] called")

  function ConditionalResults() {
    if (props.searchResults.length === 0) {
      return (
        <Box sx={{ textAlign: "center" }}>
          <CircularProgress />
        </Box>
      );
    }
    const groups: { [source: string]: ResultCard[] } = {};
    (props.searchResults as Array<ResultCard>).forEach((card) => {
      if (groups[card.source]) {
        groups[card.source].push(card);
      } else {
        groups[card.source] = [card];
      }
    });
    console.log(groups);
    return (
      <Stack spacing={2}>
        {Object.keys(groups).map((source) => (
          <SourceGroup
            key={source}
            source={source}
            cards={groups[source]}
          ></SourceGroup>
        ))}
      </Stack>
    );
  }

  return <ConditionalResults />;
}
