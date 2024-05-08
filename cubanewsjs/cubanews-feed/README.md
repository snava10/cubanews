## Getting Started

1. Install node dependencies: `npm install`
2. Run the development server: `npm run dev`
3. Set the `APIFY_TOKEN` env var with one of the following methods:
    - set it manually in the console or github secrets
    - set it in the `.env` file. Running `vercel innit` prepares this

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `src/app/home/page.tsx`. The page auto-updates as you edit the file.

## Learn More about NextJS

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js/) - your feedback and contributions are welcome!

## Deploy on Vercel

Install vercel command line tool and use it to deploy.

```bash
npm i -g vercel;
cd cubanews-feed;
vercel
```

Run `vercel --help` for more information

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/deployment) for more details.
