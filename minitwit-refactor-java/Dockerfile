# We use the Maven image as the primary base so the Dev Container has 'mvn'
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Install Git for the dev environment
RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

# Pre-copy pom for caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build the app (used for production)
#RUN mvn -DskipTests package

# --- PRODUCTION STAGE ---
FROM eclipse-temurin:21-jre AS production
WORKDIR /app
COPY --from=build /workspace/target/minitwit-refactor-*.jar /app/app.jar
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/app/app.jar"]