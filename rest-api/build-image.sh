docker build --no-cache -t cubanews-api:local \
       --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar \
       --build-arg AWS_ACCESS_KEY_ID_ARG=$AWS_ACCESS_KEY_ID \
       --build-arg AWS_SECRET_ACCESS_KEY_ARG=$AWS_SECRET_ACCESS_KEY \
       --build-arg SPRING_PROFILE_ARG=dev .

# docker build --tag "gcr.io/$PROJECT_ID/cubanews-rest-api:v1" --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .
# docker build --tag "us-east1-docker.pkg.dev/$PROJECT_ID/cubanews/cubanews-rest-api:v1" --build-arg JAR_FILE=build/libs/rest-api-1.0.0-SNAPSHOT.jar .
