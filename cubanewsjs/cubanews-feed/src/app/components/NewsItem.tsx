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
} from "@mui/joy";
import { NewsItem, NewsSourceName } from "../interfaces";
import moment from "moment";
import Image from "next/image";

type NewsItemProps = {
  item: NewsItem;
};

function getPublicationLogo(item: NewsItem) {
  let imageLogoSrc = "";
  switch (item.source) {
    case NewsSourceName.ADNCUBA:
      imageLogoSrc = "/source_logos/adncuba1.webp";
      break;
    case NewsSourceName.CATORCEYMEDIO:
      imageLogoSrc = "/source_logos/14ymedio1.jpg";
      break;
    case NewsSourceName.CIBERCUBA:
      imageLogoSrc = "/source_logos/cibercuba1.png";
      break;
    case NewsSourceName.DIARIODECUBA:
      imageLogoSrc = "/source_logos/ddc1.webp";
      break;
  }
  return (
    <Image height={30} width={30} alt="Publication Logo" src={imageLogoSrc} />
  );
}

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
                {getPublicationLogo(item)}
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
