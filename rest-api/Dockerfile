FROM adoptopenjdk/openjdk17:alpine
ARG JAR_FILE
RUN echo ${JAR_FILE}
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]