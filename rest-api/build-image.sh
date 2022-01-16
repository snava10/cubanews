#docker build -t cubanews-rest-api:1.0.0 \
#       --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .

docker build --tag "gcr.io/$PROJECT_ID/cubanews-rest-api:v1" --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .