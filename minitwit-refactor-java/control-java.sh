#!/bin/bash

JAR_FILE="target/minitwit-refactor-0.0.1.jar"
DB_FILE="minitwit-java.db"

if [ "$1" = "init" ]; then
    if [ -f "$DB_FILE" ]; then 
        echo "Database already exists."
        exit 1
    fi
    echo "Putting a database to $DB_FILE"
    mvn -X compile exec:java -Dexec.mainClass="zerodowntime.App" -Dexec.args="init"

elif [ "$1" = "build" ]; then
    echo "Building Java application..."
    mvn clean package

elif [ "$1" = "start" ]; then
    echo "Starting minitwit-java..."
    if [ ! -f "$JAR_FILE" ]; then
        echo "JAR not found. Building..."
        mvn clean package
    fi
    exec java -jar "$JAR_FILE"

    # EDITED THIS OUT TO RUN THE SEVER WITH DOCKER
    #nohup java -jar "$JAR_FILE" > out.log 2>&1 &
    #echo "Started with PID $!"

elif [ "$1" = "stop" ]; then
    echo "Stopping minitwit..."
    pkill -f "minitwit-refactor"
    
else
    echo "Usage: $0 {init|build|start|stop}"
fi
