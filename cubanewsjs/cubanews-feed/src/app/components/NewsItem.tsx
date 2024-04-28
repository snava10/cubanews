import {
  Paper,
  Grid,
  Link,
  Typography,
  styled,
  Box,
  Card,
  Stack,
  Chip,
} from "@mui/material";
import { NewsItem } from "../interfaces";
import Image from "next/image";
import moment from "moment";

type NewsItemProps = {
  item: NewsItem;
};

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === "dark" ? "#1A2027" : "#fff",
  ...theme.typography.body2,
  padding: theme.spacing(2),
  textAlign: "left",
  color: theme.palette.text.secondary,
}));

export default function NewsItemComponent({ item }: NewsItemProps) {
  return (
    <>
      <Card variant="outlined" sx={{ padding: 2 }}>
        <Stack direction="row" spacing={2}>
          <Box>
            <Image
              height={30}
              width={30}
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
        {/* <Grid container>
          <Grid item xs={2} md={1}>
            <Box>
              <Image
                height={30}
                width={30}
                alt="Publication Logo"
                src={"/source_logos/adncuba1.webp"}
              />
            </Box>
          </Grid>
          <Grid item xs={10} md={11}>
            <Box>
              <Typography>
                <Link href={item.url}>{item.title}</Link>
              </Typography>
            </Box>
          </Grid>
        </Grid> */}
      </Card>
    </>
  );
}
