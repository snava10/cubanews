import { Card, Chip, Typography, Link } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";
import moment from 'moment';

/**
 * Design philosophy:
 * - Just a more relaxed results card. The paddings are wider and the components in the card more separated from each other.
 * - The title is in a heading 6 format and the underline only appears on hover.
 * - Slight touches of the primary contrasting colour, blue in this case.
 * - That little dot character between the news source and the update date. 
 */

export function GNewsInspiredCard(props: { content: ResultCard }) {
  const date = new Date(0);
  date.setUTCSeconds(props.content.lastUpdated);
  return (
    <Box>
      <Card variant="outlined" sx={{ borderRadius: 3}}>
        <Box sx={{ padding: 2, pb: 1 }}>
          {/* Title */}
          <Typography variant="h6">
            <Link
              color="text.primary"
              fontWeight="bold"
              underline="hover"
              href={props.content.url}>
              {props.content.title}
            </Link>
          </Typography>

          {/* URL and update date */}
          <Typography variant="body2" color="text.secondary" paragraph>
            <span>{props.content.url}</span>
            <span> • </span>
            <span>{moment(date).fromNow()}</span>
          </Typography>

          {/* Content summary */}
          <Typography variant="body1" paragraph>
            {props.content.summary}
          </Typography>

          {/* Tags */}
          {props.content.tags !== undefined && props.content.tags.length > 0 &&
            <Box sx={{ p: 0, m: 0 }}>
              {props.content.tags.map((tag: string) => (
                <Chip
                  variant="outlined"
                  key={tag}
                  label={tag}
                  size="small"
                  color="primary"
                  sx={{ mr: 1, mb: 1 }} />
              ))}
            </Box>
          }
        </Box>
      </Card>
    </Box>
  )
}
