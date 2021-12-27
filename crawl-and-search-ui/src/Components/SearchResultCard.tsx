import { Card, Chip, Typography } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";

export function SearchResultCard(props: { content: ResultCard }) {
  return (
    <Box>
      <Card variant="outlined">
        <Box sx={{ padding: 2 }}>
          <Typography sx={{ mb: 0 }} gutterBottom variant="h6" component="div">
            <a href={props.content.url}>{props.content.title}</a>
          </Typography>
          <Typography sx={{ mb: 1 }} color="text.secondary">
            <a>{props.content.url}</a>
            <a> - </a>
            <a>{`${props.content.lastUpdated} hour(s) ago`}</a>
          </Typography>
          <Typography >
            {props.content.summary}
          </Typography>
        </Box>
        {props.content.tags != undefined &&
          <Box sx={{ padding: 1, paddingTop: 0 }}>
            {props.content.tags.map((tag: string) => (
              <Chip key={tag} label={tag} size="small" sx={{ margin: 0.5 }} />
            ))}
          </Box>
        }
      </Card>
    </Box>
  )
}
