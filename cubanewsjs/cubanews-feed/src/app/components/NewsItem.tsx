import {
  Grid,
  Link,
  Typography,
  styled,
  Box,
  Card,
  Stack,
  Chip,
  Divider,
  CardOverflow,
  CardContent,
  AspectRatio,
  Avatar
} from "@mui/joy";
import { NewsItem } from "../interfaces";
import Image from "next/image";
import moment from "moment";

type NewsItemProps = {
  item: NewsItem;
};

// const Item = styled(Paper)(({ theme }) => ({
//   backgroundColor: theme.palette.mode === "dark" ? "#1A2027" : "#fff",
//   ...theme.typography.body2,
//   padding: theme.spacing(2),
//   textAlign: "left",
//   color: theme.palette.text.secondary,
// }));

export default function NewsItemComponent({ item }: NewsItemProps) {
  item.tags = ["tag 1", "tag 2", "tag 4", "tag 3"]
  return (
    <Stack spacing={4}>
      {/* <Card variant="outlined" sx={{ padding: 2 }}>
        <Stack direction="row" spacing={2}>
          <Box>
            <Image
              height={50}
              width={50}
              alt="Publication Logo"
              src={"/source_logos/adncuba1.webp"}
            />
          </Box>
          <Stack direction="column">
            <Box>
              <Typography
                gutterBottom
                variant="h6"
                component="div"
                color="text.secondary"
              >
                <Link href={item.url}>{item.title}</Link>
              </Typography>
              <Typography variant="body2">
                {moment(item.isoDate).fromNow()}
              </Typography>
              {item.content && item.content?.length > 0 ? (
                <Typography>{item.content}</Typography>
              ) : (
                <></>
              )}
            </Box>
            {item.tags !== undefined && item.tags.length > 0 && (
              <Box sx={{ paddingTop: 0 }}>
                {item.tags.map((tag: string) => (
                  <Chip
                    key={tag}
                    label={tag}
                    size="small"
                    sx={{ marginRight: 0.5 }}
                    variant="outlined"
                    color="primary"
                  />
                ))}
              </Box>
            )}
          </Stack>
        </Stack>
      </Card> */}
      <Card variant="outlined" sx={{ padding: 2 }}>
        <CardContent>
          <Link href={item.url}>
            <Typography level="h2" fontSize="xl">{item.title}</Typography>
          </Link>
          <Typography level="body-sm">{item.content} ...</Typography>
        </CardContent>
        <CardOverflow variant="soft" sx={{ bgcolor: 'background.level1'}}>
          <Divider inset="context" />
          <CardContent orientation="horizontal">
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Typography level="body-sm" fontWeight="md" textColor="text.secondary">
              {moment(item.isoDate).fromNow()}
            </Typography>
            <Divider orientation="vertical" sx={{ml:1, mr:1}}/>
            <Box alignItems="center">
              <Avatar 
                size="sm"
                alt="Publication Logo" src="/source_logos/adncuba1.webp" 
                sx ={{"--Avatar-size": "23px"}}
              />
            </Box>
            <Typography level="body-sm" fontWeight="md" textColor="text.secondary">
              {item.source}
            </Typography>
            <Divider orientation="vertical" sx={{ml:1, mr:1}}/>
              {item.tags.map((tagName: string) => (
                <Chip variant="outlined" key={tagName}>
                  <Typography level="body-xs" fontWeight="lg" textColor="text.secondary">
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
