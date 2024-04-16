## Empty TypeScript template

Start a new [web scraping](https://apify.com/web-scraping) project quickly and easily in TypeScript (Node.js) with our empty project template. It provides a basic structure for the Actor with [Apify SDK](https://docs.apify.com/sdk/js/) and allows you to easily add your own functionality.

## Included features

- **[Apify SDK](https://docs.apify.com/sdk/js/)** - a toolkit for building [Actors](https://apify.com/actors)
- **[Crawlee](https://crawlee.dev/)** - web scraping and browser automation library

## How it works

Insert your own code between `await Actor.init()` and `await Actor.exit()`. If you would like to use the [Crawlee](https://crawlee.dev/) library simply uncomment its import `import { CheerioCrawler } from 'crawlee';`.

## Resources

- [TypeScript vs. JavaScript: which to use for web scraping?](https://blog.apify.com/typescript-vs-javascript-crawler/)
- [Node.js tutorials](https://docs.apify.com/academy/node-js) in Academy
- [Video guide on getting scraped data using Apify API](https://www.youtube.com/watch?v=ViYYDHSBAKM)
- [Integration with Airbyte](https://apify.com/integrations), Make, Zapier, Google Drive, and other apps
- A short guide on how to build web scrapers using code templates:

[web scraper template](https://www.youtube.com/watch?v=u-i-Korzf8w)


## Getting started

For complete information [see this article](https://docs.apify.com/platform/actors/development#build-actor-locally). To run the actor use the following command:

```bash
apify run
```

## Deploy to Apify

### Connect Git repository to Apify

If you've created a Git repository for the project, you can easily connect to Apify:

1. Go to [Actor creation page](https://console.apify.com/actors/new)
2. Click on **Link Git Repository** button

### Push project on your local machine to Apify

You can also deploy the project on your local machine to Apify without the need for the Git repository.

1. Log in to Apify. You will need to provide your [Apify API Token](https://console.apify.com/account/integrations) to complete this action.

    ```bash
    apify login
    ```

2. Deploy your Actor. This command will deploy and build the Actor on the Apify Platform. You can find your newly created Actor under [Actors -> My Actors](https://console.apify.com/actors?tab=my).

    ```bash
    apify push
    ```

## Documentation reference

To learn more about Apify and Actors, take a look at the following resources:

- [Apify SDK for JavaScript documentation](https://docs.apify.com/sdk/js)
- [Apify SDK for Python documentation](https://docs.apify.com/sdk/python)
- [Apify Platform documentation](https://docs.apify.com/platform)
- [Join our developer community on Discord](https://discord.com/invite/jyEM2PRvMU)
