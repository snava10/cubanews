import type { NextApiRequest, NextApiResponse } from "next";

type ResponseData = {
  banter: string;
};

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<ResponseData>
) {
  res.status(200).json({
    banter: "Cubanews feed being updated!",
  });
}
