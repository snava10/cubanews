# source /home/sergio/.bashrc;

echo "Refreshing feed Locally"
cd /home/sergio/github/cubanews/cubanewsjs/adncuba-crawler && npm run start;
cd /home/sergio/github/cubanews/cubanewsjs/catorceYmedio-crawler && npm run start;
cd /home/sergio/github/cubanews/cubanewsjs/cibercuba-crawler && npm run start;
cd /home/sergio/github/cubanews/cubanewsjs/ddc-crawler && npm run start:local;
cd /home/sergio/github/cubanews/cubanewsjs/cubanews-feed && npm run start:local;

# # node /home/sergio/github/cubanews/cubanewsjs/adncuba-crawler/dist/main.js;
# cd /home/sergio/github/cubanews/cubanewsjs/catorceYmedio-crawler && npm run start;
# cd /home/sergio/github/cubanews/cubanewsjs/cibercuba-crawler && npm run start;
# cd /home/sergio/github/cubanews/cubanewsjs/cubanews-feed && npm run start:local;