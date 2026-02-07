#!/bin/bash

APP_JAR="/app/app.jar"
DB_FILE="minitwit.db"

case "$1" in 
    init)
        if [ -f "$DB_FILE"]; then
            echo "Database already exists at "$DB_FILE"
            exit 0
        fi
        echo "Creating database at "$DB_FILE"
        # Needs to work without having to call maven compile 
        # App.java also needs to have a different way of getting the database dir