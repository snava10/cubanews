import { Card, Chip, Stack, Typography } from "@mui/material";
import { Box } from "@mui/system";
import { ResultCard } from "../Helpers/resultsGenerator";
import moment from "moment";
import { useEffect, useState } from "react";

export function SearchResultCard(props: {
  content: ResultCard;
  bucketName: string;
}) {
  const base_url = process.env.REACT_APP_BASE_URL;
  const date = new Date(0);
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
      <Card variant="outlined">
        <Box sx={{ padding: 2 }}>
          <Stack direction="row">
            <Box
              component="img"
              sx={{
                height: 30,
                width: 30,
                maxHeight: { xs: 233, md: 167 },
                maxWidth: { xs: 350, md: 250 },
              }}
              alt="News Source"
              src={imageUrl}
            />
            <Typography sx={{ marginLeft: 2 }}>
              {props.content.source ?? "Name of the publication"}
            </Typography>
          </Stack>
          <Typography sx={{ mb: 0 }} gutterBottom variant="h6" component="div">
            <a href={props.content.url} target="_blank">
              {props.content.title}
            </a>
          </Typography>
          <Typography sx={{ mb: 1 }} color="text.secondary">
            <span>{props.content.url}</span>
            <span> - </span>
            <span>{moment(date).fromNow()}</span>
          </Typography>
          <Typography>{props.content.summary}</Typography>
        </Box>
        {props.content.tags !== undefined && props.content.tags.length > 0 && (
          <Box sx={{ padding: 1, paddingTop: 0 }}>
            {props.content.tags.map((tag: string) => (
              <Chip key={tag} label={tag} size="small" sx={{ margin: 0.5 }} />
            ))}
          </Box>
        )}
      </Card>
    </Box>
  );
}
