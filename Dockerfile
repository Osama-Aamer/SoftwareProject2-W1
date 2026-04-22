# Multi-stage build: compile with Maven, then run with JRE
FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="osamaaa1"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre
LABEL authors="osamaaa1"

# Install X11/GTK libraries needed for JavaFX GUI via Xming
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxxf86vm1 \
    libgl1 \
    libglib2.0-0 \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/shopping-cart-gui.jar .

EXPOSE 8080

ENV DISPLAY=host.docker.internal:0.0

ENTRYPOINT ["java", "-jar", "shopping-cart-gui.jar"]
