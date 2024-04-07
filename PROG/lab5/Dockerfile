FROM maven:3.8.3-openjdk-11-slim AS build
WORKDIR /app


COPY pom.xml .
COPY lab-client .
COPY lab-common .
COPY init_model.json .
COPY s_checks.xml .
COPY .env .

RUN mvn clean install


FROM openjdk:17-oracle

WORKDIR /app

ENV LAB5=init_model.json

COPY lab-client/target/lab-client-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]