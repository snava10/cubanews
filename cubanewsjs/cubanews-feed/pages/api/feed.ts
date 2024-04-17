import type { NextApiRequest, NextApiResponse } from "next";
import { FeedResponseData, NewsFeed } from "../../interfaces";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<FeedResponseData>
) {
  res.status(200).json({
    banter: "Cubanews feed!",
    content: {
      timestamp: Date.now(),
      feed: [
        {
          title: "Fake news 1",
          url: "https://fn1.com",
        },
        {
          title: "Fake news 2",
          url: "https://fn2.com",
        },
      ],
    },
  });
}
