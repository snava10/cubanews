import * as nodemailer from "nodemailer";
import * as dotenv from "dotenv";
import { getFeedItems } from "@/app/api/feed/route";

dotenv.config();
const from = "cubanews.icu@gmail.com";

export const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "cubanews.icu@gmail.com",
    pass: process.env.MAIL_PWD,
  },
});

async function getRecipients(): Promise<string[]> {
  return ["sergio.nava89@gmail.com", "hello@acorndigitalsolutions.com"];
}

async function getEmailBody(): Promise<string> {
  const feed = await getFeedItems(1, 10);
  var body = "";
  for (const item of feed) {
    body += `<div><h1><a href='${item.url}'>${item.title}</a></h1>`;
    body += `<p>${item.content}</p></div>`;
  }
  return `<!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Email Template</title>
    </head>
    <body>
      ${body}
    </body>
  </html>`;
}

export async function sendEmails() {
  const mailOptions = {
    from: from,
    to: "",
    bcc: "hello@acorndigitalsolutions.com;sergio.nava89@gmail.com",
    subject: `CubaNews resumen ${new Date().toDateString()}`,
    html: await getEmailBody(),
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.error("Error occurred:", error);
    } else {
      console.log("Email sent:", info.response);
    }
  });
}

(async () => {
  try {
    await sendEmails();
  } catch (error) {
    console.error("Error sending emails ", error);
  }
})();
