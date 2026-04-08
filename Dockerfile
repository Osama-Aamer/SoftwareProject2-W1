# Multi-stage build: compile with Maven, then run with JRE
FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="osamaaa1"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/shopping-cart-gui.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "shopping-cart-gui.jar"]
