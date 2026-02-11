# --------------------
# BUILD STAGE
# --------------------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first (for dependency caching)
COPY minitwit-refactor-java/pom.xml ./pom.xml
RUN mvn dependency:go-offline

# Copy source code
COPY minitwit-refactor-java/src ./src

# Build application
RUN mvn package -DskipTests

# --------------------
# RUNTIME STAGE
# --------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for SQLite DB
RUN mkdir -p /data

ENV DB_FILE=/data/minitwit.db

EXPOSE 7070

CMD ["java", "-jar", "app.jar"]