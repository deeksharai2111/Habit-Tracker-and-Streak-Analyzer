# Troubleshooting Guide

## Common Issues and Solutions

### 1. Port Already in Use Error

**Error:** `Web server failed to start. Port 8080 was already in use.`

**Solutions:**
- **Option 1:** Use the provided start scripts
  - Windows: `start.bat`
  - Linux/Mac: `./start.sh`
  
- **Option 2:** Manually change port
  - Edit `application.properties` and change `server.port=8080` to `server.port=8081`
  
- **Option 3:** Kill the process using the port
  ```bash
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  
  # Linux/Mac
  lsof -i :8080
  kill -9 <PID>
  ```

### 2. Maven Compilation Issues

**Error:** `BUILD FAILURE`

**Solutions:**
- Clean and rebuild:
  ```bash
  mvn clean
  mvn compile
  ```
  
- Update dependencies:
  ```bash
  mvn dependency:resolve
  ```
  
- Check Java version:
  ```bash
  java -version
  # Should be Java 17 or higher
  ```

### 3. Database Issues

**Error:** `SQLite database locked`

**Solutions:**
- Delete the database file and restart:
  ```bash
  rm habits.db
  mvn spring-boot:run
  ```
  
- Check file permissions:
  ```bash
  # Linux/Mac
  chmod 644 habits.db
  ```

### 4. Email Configuration Issues

**Error:** `Mail server connection failed`

**Solutions:**
- Update email settings in `application.properties`
- For Gmail, use App Password instead of regular password
- Set environment variables:
  ```bash
  export MAIL_USERNAME=your-email@gmail.com
  export MAIL_PASSWORD=your-app-password
  ```

### 5. Memory Issues

**Error:** `OutOfMemoryError`

**Solutions:**
- Increase JVM memory:
  ```bash
  mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1g -Xms512m"
  ```
  
- Or set environment variable:
  ```bash
  export JAVA_OPTS="-Xmx1g -Xms512m"
  ```

### 6. Security Configuration Issues

**Error:** `Access denied` or authentication problems

**Solutions:**
- Check user credentials in `application.properties`
- Default credentials: admin/admin
- For production, set environment variables:
  ```bash
  export ADMIN_USERNAME=your-admin-username
  export ADMIN_PASSWORD=your-secure-password
  ```

## Quick Start Commands

### Development Mode
```bash
# Windows
start.bat

# Linux/Mac
./start.sh

# Manual
mvn spring-boot:run
```

### Production Mode
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/habit-tracker-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment
```bash
# Build and run with Docker
docker-compose up --build

# Run with production profile
docker-compose -f docker-compose.yml --profile production up
```

## Environment Variables

Set these for production deployment:

```bash
# Database
export DATABASE_URL=jdbc:sqlite:/app/data/habits.db

# Email
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Security
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=secure-password

# Server
export PORT=8080
export CONTEXT_PATH=/
```

## Log Files

Check these locations for detailed error logs:
- Application logs: `app.log`
- Spring Boot logs: Console output
- Database logs: Check SQLite file permissions

## API Endpoints

Test these endpoints to verify the application is working:

- **Health Check:** `http://localhost:8081/`
- **Dashboard:** `http://localhost:8081/dashboard`
- **Analytics:** `http://localhost:8081/analytics`
- **API:** `http://localhost:8081/api/v1/habits`

## Common Ports

- **Development:** 8081 (changed from 8080)
- **Production:** 8080 (configurable via PORT env var)
- **Docker:** 8080 (mapped to host) 