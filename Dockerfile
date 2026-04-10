FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
ARG SERVICE_NAME

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY ${SERVICE_NAME}/ ${SERVICE_NAME}/

RUN chmod +x gradlew && ./gradlew :${SERVICE_NAME}:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE_NAME

COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
