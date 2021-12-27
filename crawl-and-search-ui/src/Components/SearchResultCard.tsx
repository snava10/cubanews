import { Card, CardActions, CardContent, CardHeader, Chip, Typography } from "@mui/material";
import { Box } from "@mui/system";

// For sure!
// title
// source

// Maybe
// Summary (200 line)
// Tags

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


function randomIntFromInterval(min: number, max: number) { // min and max included 
  return Math.floor(Math.random() * (max - min + 1) + min)
}

const generateRandomString = (size: number) => {
  const characters = 'ABCD EFGH IJKL MNOP QRST UVWX YZab cdef ghij klmn opqr stuv wxyz 0123 4567 89';
  let res = ' ';
  const charactersLength = characters.length;
  for (let i = 0; i < size; i++) {
    res += characters.charAt(Math.floor(Math.random() * charactersLength));
  }

  return res;
}