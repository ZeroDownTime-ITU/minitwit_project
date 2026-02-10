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
COPY control-java.sh /app/
# If you want tests to run as well during the build, then delete the "DskipTests" flag
RUN mvn -DskipTests package
COPY minitwit-java.db /app/ 

# RUNTIME OF CONTAINER

FROM eclipse-temurin:21-jre
WORKDIR /app

# We use the reference to the build stage above and copy from the dir the compiled code  
COPY --from=build /app/target/*.jar /app/app.jar
COPY control-java.sh /app/control-java.sh

COPY minitwit-java.db /app/minitwit-java.db 

# LINE ABOVE IS THE ACTUAL COPY OF THE DATA BASE?!

# The flag +x here just makes it so we can execute the .sh file (I think it works like chmod 775 <insert name of sh-file>)
RUN chmod +x /app/control-java.sh

EXPOSE 7070

# NOTE: To start of remember to type "docker build -t <Name of your image> ."
# After it is built, you run: "docker run --rm -it --entrypoint <Bash or sh> <name of the image>"
# This will make it so you are running the container and you are able to execute commands in the terminal for that container
