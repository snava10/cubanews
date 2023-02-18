docker compose stop;
docker system prune;
docker image rm -f ghcr.io/snava10/cubanews-rest-api:main;
docker image rm -f ghcr.io/snava10/cubanews-web:main;
docker compose up;