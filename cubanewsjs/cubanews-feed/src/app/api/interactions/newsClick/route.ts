import { InteractionResponseData } from "@/app/interfaces";
import { createKysely } from "@vercel/postgres-kysely";
import { NextRequest, NextResponse } from "next/server";
import { Database } from "../../dataschema";

const db = createKysely<Database>();

export async function GET(
  request: NextRequest
): Promise<NextResponse<InteractionResponseData | null>> {
  const feedid = request.nextUrl.searchParams.get("feedid");
  if (!feedid) {
    return NextResponse.json(
      {
        banter: "feedid cannot be null",
      },
      { status: 400, statusText: "Bad Parameters" }
    );
  }
  db.insertInto("interactions")
    .values([
      {
        feedid: parseInt(feedid), // Replace with the actual feedid
        interaction: "view", // Replace with the actual interaction type
        timestamp: Date.now(), // Current timestamp
      },
    ])
    .execute();
  return NextResponse.json(
    {
      banter: "Refreshing cubanews feed",
    },
    { status: 200 }
  );
}
