import { Card, CardActions, CardContent, CardHeader, Chip, Typography } from "@mui/material";
import { Box } from "@mui/system";

interface ResultCardFields {
  url: string;
  title: string;
  summary: string;
  tags: Array<string>;
}

export function SearchResultCard(props: { content: ResultCardFields }) {
  return (
    <Box>
      <Card variant="outlined">
        <CardContent>
          <Typography gutterBottom variant="h6" component="div">
            <a href={props.content.url}>{props.content.title}</a>
            <a> ({props.content.url})</a>
          </Typography>
          <Typography >
            {props.content.summary}
          </Typography>
        </CardContent>
        <CardActions>
          {props.content.tags.map((tag: string) => (
            <Chip key={tag} label={tag} />
          ))}
        </CardActions>
      </Card>
    </Box>
  )
}
