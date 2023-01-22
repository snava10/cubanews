FROM openjdk:17-alpine
ARG JAR_FILE
RUN echo ${JAR_FILE}
COPY ${JAR_FILE} app.jar
RUN mkdir /root/db_migrations
COPY src/main/resources/db_migrations /root/db_migrations
RUN ls /root
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]