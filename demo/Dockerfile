# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache deps first
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package


FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# For container healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 9090

# Default Spring Boot port for this project
ENV SERVER_PORT=9090

HEALTHCHECK --interval=10s --timeout=3s --retries=12 \
  CMD curl -sS http://localhost:${SERVER_PORT}/ >/dev/null || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]
