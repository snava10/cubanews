import { Card, Chip, Typography } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";
import moment from 'moment';

export function SearchResultCard(props: { content: ResultCard }) {
  const date = new Date(0);
  date.setUTCSeconds(props.content.lastUpdated);
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
            <a>{moment(date).fromNow()}</a>
          </Typography>
          <Typography >
            {props.content.summary}
          </Typography>
        </Box>
        {props.content.tags != undefined && props.content.tags.length != 0 &&
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
