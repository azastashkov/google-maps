FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY load-client/ load-client/

RUN chmod +x gradlew && ./gradlew :load-client:jar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/load-client/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
