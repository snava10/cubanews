import {
  Link,
  Typography,
  Box,
  Card,
  Stack,
  Chip,
  Divider,
  CardOverflow,
  CardContent,
  Avatar,
  AspectRatio,
} from "@mui/joy";
import { NewsItem } from "../interfaces";
import moment from "moment";
import Image from "next/image";

type NewsItemProps = {
  item: NewsItem;
};

export default function NewsItemComponent({ item }: NewsItemProps) {
  return (
    <Stack spacing={4}>
      <Card variant="outlined" sx={{ padding: 2 }}>
        <CardContent>
          <Link href={item.url}>
            <Typography level="h2" fontSize="xl">
              {item.title}
            </Typography>
          </Link>
          <Typography level="body-sm">{item.content} ...</Typography>
        </CardContent>
        <CardOverflow variant="soft" sx={{ bgcolor: "background.level1" }}>
          <Divider inset="context" />
          <CardContent orientation="horizontal">
            <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
              <Typography
                level="body-sm"
                fontWeight="md"
                textColor="text.secondary"
              >
                {moment(item.isoDate).fromNow()}
              </Typography>
              <Divider orientation="vertical" sx={{ ml: 1, mr: 1 }} />
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                }}
              >
                <Image
                  src={`cuban-flag.svg`}
                  width={20}
                  height={100}
                  alt="Cuban FLag"
                />

                {/* <AspectRatio objectFit="contain">
                <img
                  src="/source_logos/adncuba1.webp"
                />
              </AspectRatio> */}

                {/* <Avatar 
                size="sm"
                alt="Publication Logo" src="/source_logos/adncuba1.webp" 
                sx ={{"--Avatar-size": "23px"}}
              /> */}
              </Box>
              <Typography
                level="body-sm"
                fontWeight="md"
                textColor="text.secondary"
              >
                {item.source}
              </Typography>
              <Divider orientation="vertical" sx={{ ml: 1, mr: 1 }} />
              {item.tags.map((tagName: string) => (
                <Chip variant="outlined" key={tagName}>
                  <Typography
                    level="body-xs"
                    fontWeight="lg"
                    textColor="text.secondary"
                  >
                    {tagName}
                  </Typography>
                </Chip>
              ))}
            </Stack>
          </CardContent>
        </CardOverflow>
      </Card>
    </Stack>
  );
}
