#!/bin/bash

echo "Starting Habit Tracker Application..."

# Check if port 8081 is available
if lsof -Pi :8081 -sTCP:LISTEN -t >/dev/null ; then
    echo "Port 8081 is already in use. Trying port 8082..."
    SERVER_PORT=8082
else
    SERVER_PORT=8081
fi

echo "Starting application on port $SERVER_PORT..."

# Clean and compile
echo "Cleaning and compiling..."
mvn clean compile

# Start the application
echo "Starting Spring Boot application..."
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=$SERVER_PORT" 