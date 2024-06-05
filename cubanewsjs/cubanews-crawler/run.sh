
echo "$(date) Refreshing feed Locally" > $HOME/cubanews-crawler.log 2>&1;
cd $HOME/github/cubanews/cubanewsjs/ddc-crawler && npm run start > $HOME/cubanews-crawler.log 2>&1;
cd $HOME/github/cubanews/cubanewsjs/adncuba-crawler && npm run start > $HOME/cubanews-crawler.log 2>&1;
cd $HOME/github/cubanews/cubanewsjs/catorceYmedio-crawler && npm run start > $HOME/cubanews-crawler.log 2>&1;
cd $HOME/github/cubanews/cubanewsjs/cibercuba-crawler && npm run start > $HOME/cubanews-crawler.log 2>&1;
cd $HOME/github/cubanews/cubanewsjs/cubanet-crawler && npm run start > $HOME/cubanews-crawler.log 2>&1;

cd $HOME/github/cubanews/cubanewsjs/cubanews-feed && npm run start:local > $HOME/cubanews-crawler.log 2>&

echo "$(date) Comleted Refreshing feed Locally" > ~/cubanews-crawler.log 2>&1;
