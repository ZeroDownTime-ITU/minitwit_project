# Has to start with the latest build
FROM maven:4.0.0-rc-5-eclipse-temurin-21 AS build

# the "WORKDIR" will make a directory on the container (if it doesnt have the dir)
WORKDIR /app

# Here we copy the pom file into our container's dir ~/app/pom.xml 
# the "." refers to our "WORKDIR" we created earlier
# The reason why have the pom.xml file first is because of docker's cache. 
# I think writing more comments might be too much, so look at this link for a detailed description:
    # https://docs.docker.com/build/cache/#how-the-build-cache-works
COPY pom.xml .
COPY src ./src

# If you want tests to run as well during the build, then delete the "DskipTests" flag
RUN mvn -DskipTests package

# RUNTIME OF CONTAINER
FROM eclipse-temurin:21-jre

WORKDIR /app

# We use the reference to the build stage above and copy from the dir the compiled code  
COPY --from=build /app/target/minitwit-refactor-*.jar /app/app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

