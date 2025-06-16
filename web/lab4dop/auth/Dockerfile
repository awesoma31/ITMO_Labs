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

WORKDIR /app

COPY --from=build-image $APP_HOME/build/libs/auth-0.0.1-SNAPSHOT.jar /app/build/libs/auth-0.0.1-SNAPSHOT.jar

COPY gradle gradle
COPY gradlew .
COPY .gradle .gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
#COPY build/libs/auth-0.0.1-SNAPSHOT.jar build/libs/.

RUN #./gradlew dependencies --no-daemon

RUN #./gradlew bootJar --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/auth-0.0.1-SNAPSHOT.jar"]
