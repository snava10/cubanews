import { Card, Chip, Typography, Link, Stack, CardMedia, CardContent } from "@mui/material";
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

export function GNewsWithImageCard(props: { content: ResultCard, stackLayout: boolean, compact: boolean, images: boolean }) {
  const date = new Date(0);

  // Solution for toggling the position of the image in a result card.
  // This is a hacky solution that works for determining screen size ONCE. If we want it to work for a resizing screen without 
  // having to refresh the page, we need to make an on-resize listener.
  let smallScreen = window.innerWidth <= 600 // sm breakpoint 
  let imageOnTopOfCard = props.stackLayout && smallScreen || !props.stackLayout;

  date.setUTCSeconds(props.content.lastUpdated);
  return (
    <Box>
      <Card variant="outlined" sx={{ borderRadius: 3 }}>
        {props.images && imageOnTopOfCard &&
          <CardMedia
            component="img"
            image={`${process.env.PUBLIC_URL}/image_placeholder.jpg`}
            alt="image"
            sx={{
              height: 150 // only restrict height
            }}
          />
        }

        <Stack
          direction={{xs:"row"}}
          spacing={2}
          sx={{
            padding: 2,
            pb: 1
          }}
        >
          <Box>
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
          {props.images && !imageOnTopOfCard &&
            <Box
              component="img"
              src={`${process.env.PUBLIC_URL}/image_placeholder.jpg`}
              alt="image"
              sx={{
                borderRadius: 3,
                height: 100 // only restrict height
              }}
            />
          }
        </Stack>
      </Card>
    </Box>
  )
}
