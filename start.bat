@echo off
echo Starting Habit Tracker Application...

REM Check if port 8081 is available
netstat -ano | findstr :8081 > nul
if %errorlevel% equ 0 (
    echo Port 8081 is already in use. Trying port 8082...
    set SERVER_PORT=8082
) else (
    set SERVER_PORT=8081
)

echo Starting application on port %SERVER_PORT%...

REM Clean and compile
echo Cleaning and compiling...
call mvn clean compile

REM Start the application
echo Starting Spring Boot application...
call mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=%SERVER_PORT%"

pause 