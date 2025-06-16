ARG BUILD_HOME=/gradle-docker-example

FROM gradle:jdk17 AS build-image

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

COPY build.gradle settings.gradle $APP_HOME/
COPY src $APP_HOME/src
#COPY --chown=gradle:gradle config $APP_HOME/config

RUN gradle --no-daemon build

FROM eclipse-temurin:17-jdk-alpine

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME

COPY --from=build-image $APP_HOME/build/libs/api-gateway-0.0.1-SNAPSHOT.jar /app/build/libs/api-gateway-0.0.1-SNAPSHOT.jar

WORKDIR /app

COPY gradle gradle
COPY .gradle .gradle
#COPY .gradle /home/gradle/.gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src
#COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar build/libs/.

RUN #./gradlew dependencies --no-daemon

RUN #./gradlew bootJar --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/api-gateway-0.0.1-SNAPSHOT.jar"]
