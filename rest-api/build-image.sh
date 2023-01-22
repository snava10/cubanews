docker build -t cubanews-rest-api:local \
       --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .

# docker build --tag "gcr.io/$PROJECT_ID/cubanews-rest-api:v1" --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .
# docker build --tag "us-east1-docker.pkg.dev/$PROJECT_ID/cubanews/cubanews-rest-api:v1" --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .
