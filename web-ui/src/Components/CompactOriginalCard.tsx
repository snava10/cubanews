import { Card, Chip, Typography } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";
import moment from 'moment';

export function CompactOriginalCard(props: { content: ResultCard }) {
  const date = new Date(0);
  date.setUTCSeconds(props.content.lastUpdated);
  return (
    <Box>
      <Card variant="outlined">
        <Box sx={{ padding: 2, pb: 1}}>
          {/* Title */}
          <Typography variant="h6">
            <a href={props.content.url} target="_blank">{props.content.title}</a>
          </Typography>
          
          {/* URL and update date */}
          <Typography variant="body2" color="text.secondary" gutterBottom >
            <span>{props.content.url}</span>
            <span> - </span>
            <span>{moment(date).fromNow()}</span>
          </Typography>

          {/* Content summary */}
          <Typography variant="body1" gutterBottom>
            {props.content.summary}
          </Typography>

          {/* Tags */}
          {props.content.tags !== undefined && props.content.tags.length > 0 &&
            <Box sx={{ p: 0, m: 0 }}>
              {props.content.tags.map((tag: string) => (
                <Chip key={tag} label={tag} size="small" sx={{ mr: 0.5 }} />
              ))}
            </Box>
          }
        </Box>
      </Card>
    </Box>
  )
}
