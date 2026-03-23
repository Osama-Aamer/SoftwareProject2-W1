# Multi-stage build
FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="osamaaa1"

WORKDIR /app

COPY pom.xml .
COPY . /app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/laskin.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "laskin.jar"]
