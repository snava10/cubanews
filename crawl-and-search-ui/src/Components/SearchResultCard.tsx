import { Card, CardActions, CardContent, CardHeader, Chip, styled, Typography } from "@mui/material";
import { Box } from "@mui/system";

interface ResultCardFields {
  url: string;
  title: string;
  lastUpdated: string,
  summary: string;
  tags: Array<string>;
}

export function SearchResultCard(props: { content: ResultCardFields }) {
  return (
    <Box>
      <Card variant="outlined">
        <Box sx={{padding: 2}}>
          <Typography sx={{ mb: 0 }} gutterBottom variant="h6" component="div">
            <a href={props.content.url}>{props.content.title}</a>
          </Typography>
          <Typography sx={{ mb: 1 }} color="text.secondary">
            <a>{props.content.url}</a>
            <a> - </a>
            <a>{props.content.lastUpdated}</a>
          </Typography>
          <Typography >
            {props.content.summary}
          </Typography>
        </Box>
        <Box sx={{padding: 1, paddingTop: 0}}>
          {props.content.tags.map((tag: string) => (
            <Chip key={tag} label={tag} size="small" sx={{marginRight: 0.5, marginLeft: 0.5}}/>
          ))}
        </Box>
      </Card>
    </Box>
  )
}
