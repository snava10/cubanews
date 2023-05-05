import { Card, Chip, Typography, Link } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";
import moment from "moment";
import { useEffect, useState } from "react";

/**
 * Design philosophy:
 * - Just a more relaxed results card. The paddings are wider and the components in the card more separated from each other.
 * - The title is in a heading 6 format and the underline only appears on hover.
 * - Slight touches of the primary contrasting colour, blue in this case.
 * - That little dot character between the news source and the update date.
 */

export function GNewsInspiredCard(props: { content: ResultCard }) {
  const date = new Date(0);
  const base_url = process.env.REACT_APP_BASE_URL;
  date.setUTCSeconds(props.content.lastUpdated);
  const [imageUrl, setImageUrl] = useState<string>();

  async function readImage(source: string): Promise<string> {
    if (!source) return "";
    const response = await fetch(`${base_url}/api/search/logo/${source}`, {
      method: "GET",
    });
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    return url;
  }

  useEffect(() => {
    async function fetchImage() {
      try {
        if (!imageUrl) {
          const imageData = await readImage(props.content.source);
          setImageUrl(imageData);
        }
      } catch (error) {
        console.error("Failed to load logo image:", error);
      }
    }
    fetchImage();
  }, [props.content.source, readImage]);

  return (
    <Box>
      <Card variant="outlined" sx={{ borderRadius: 3 }}>
        <Box sx={{ padding: 2, pb: 1 }}>
          <Box
            component="img"
            sx={{
              height: 40,
              width: 40,
              maxHeight: { xs: 233, md: 167 },
              maxWidth: { xs: 350, md: 250 },
            }}
            alt="News Source"
            src={imageUrl}
          />
          {/* Title */}
          <Typography variant="h6">
            <Link
              color="text.primary"
              fontWeight="bold"
              underline="hover"
              href={props.content.url}
            >
              {props.content.title}
            </Link>
          </Typography>

          {/* URL and update date */}
          <Typography variant="body2" color="text.secondary" paragraph>
            <span>{moment(date).fromNow()}</span>
          </Typography>

          {/* Content summary */}
          <Typography variant="body1" paragraph>
            {props.content.summary}
          </Typography>

          {/* Tags */}
          {props.content.tags !== undefined &&
            props.content.tags.length > 0 && (
              <Box sx={{ p: 0, m: 0 }}>
                {props.content.tags.map((tag: string) => (
                  <Chip
                    variant="outlined"
                    key={tag}
                    label={tag}
                    size="small"
                    color="primary"
                    sx={{ mr: 1, mb: 1 }}
                  />
                ))}
              </Box>
            )}
        </Box>
      </Card>
    </Box>
  );
}
