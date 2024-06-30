echo "$(date) Refreshing feed Locally";
cd $HOME/github/cubanews/cubanewsjs/ddc-crawler && npm run start;
cd $HOME/github/cubanews/cubanewsjs/adncuba-crawler && npm run start;
cd $HOME/github/cubanews/cubanewsjs/catorceYmedio-crawler && npm run start;
cd $HOME/github/cubanews/cubanewsjs/cibercuba-crawler && npm run start;
cd $HOME/github/cubanews/cubanewsjs/cubanet-crawler && npm run start;
cd $HOME/github/cubanews/cubanewsjs/cubanews-feed && npm run start:local:dev;
npm run start:mail:dev;

echo "$(date) Completed Refreshing feed Locally";