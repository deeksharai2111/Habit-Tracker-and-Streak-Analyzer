# Daily Habit Tracker

A comprehensive Spring Boot web application for tracking daily habits, building streaks, and visualizing progress.

## Features

- **User Authentication**: Secure registration and login system using Spring Security
- **Habit Management**: Create, update, and delete personal habits
- **Daily Check-ins**: Track daily progress with one-click check-ins
- **Streak Tracking**: Automatic calculation and display of consecutive day streaks
- **Visual Charts**: Interactive bar charts showing habit streak data using Chart.js
- **Responsive Design**: Modern, mobile-friendly interface with Bootstrap
- **SQLite Database**: Lightweight, persistent data storage

## Tech Stack

- **Backend**: Java 17 + Spring Boot 3.2.5
- **Database**: SQLite with JPA/Hibernate
- **Security**: Spring Security with BCrypt password encoding
- **Frontend**: Thymeleaf + HTML/CSS + Bootstrap 5.3
- **Charts**: Chart.js for data visualization
- **Build Tool**: Maven

## Project Structure

```
habit-tracker/
├── src/main/java/com/example/habittracker/
│   ├── config/
│   │   └── SecurityConfig.java          # Spring Security configuration
│   ├── controller/
│   │   ├── AuthController.java          # Login/Registration endpoints
│   │   ├── DashboardController.java     # Main dashboard
│   │   └── HabitController.java         # Habit CRUD operations
│   ├── model/
│   │   ├── User.java                    # User entity
│   │   ├── Role.java                    # User roles
│   │   ├── Habit.java                   # Habit entity
│   │   └── CheckIn.java                 # Daily check-in records
│   ├── repository/
│   │   ├── UserRepository.java          # User data access
│   │   ├── RoleRepository.java          # Role data access
│   │   ├── HabitRepository.java         # Habit data access
│   │   └── CheckInRepository.java       # Check-in data access
│   ├── service/
│   │   ├── UserService.java             # User business logic
│   │   ├── CustomUserDetailsService.java # Spring Security integration
│   │   ├── HabitService.java            # Habit business logic
│   │   └── CheckInService.java          # Check-in and streak logic
│   └── HabitTrackerApplication.java     # Main application class
├── src/main/resources/
│   ├── static/
│   │   ├── css/style.css                # Custom styles
│   │   ├── js/app.js                    # Custom JavaScript
│   │   └── js/charts.js                 # Chart utilities
│   ├── templates/
│   │   ├── login.html                   # Login page
│   │   ├── register.html                # Registration page
│   │   ├── dashboard.html               # Main dashboard
│   │   └── habit-form.html              # Habit creation/editing
│   └── application.properties           # Application configuration
└── pom.xml                              # Maven dependencies
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation & Running

1. **Clone or extract the project**:
   ```bash
   cd habit-tracker
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - Open your browser and navigate to: `http://localhost:8080`
   - You'll be redirected to the login page

### First Time Setup

1. **Register a new account**:
   - Click "Sign up here" on the login page
   - Enter a username and password
   - Click "Create Account"

2. **Login**:
   - Use your credentials to log in
   - You'll be redirected to the dashboard

3. **Create your first habit**:
   - Click "New Habit" in the navigation or dashboard
   - Enter a habit name and description
   - Click "Create Habit"

4. **Start tracking**:
   - Use the "Check In" button to mark daily completion
   - Watch your streak grow!
   - View progress in the chart

## Usage Guide

### Dashboard Features

- **Stats Overview**: View total habits, today's completions, and best streak
- **Habit List**: See all your habits with current streaks
- **Quick Actions**: Check in, edit, or delete habits directly from the dashboard
- **Streak Chart**: Visual representation of all habit streaks

### Habit Management

- **Create**: Add new habits with names and descriptions
- **Edit**: Update habit details while preserving check-in history
- **Delete**: Remove habits (this will delete all associated data)
- **Check-in**: Mark habits as completed for the current day (once per day)

### Streak System

- Streaks automatically increment when you check in on consecutive days
- Missing a day resets the streak to 0
- Check-ins are limited to once per habit per day
- Streak data is displayed in real-time charts

## Database

The application uses SQLite for data storage with the following tables:

- **users**: User accounts and authentication
- **roles**: User roles (default: ROLE_USER)
- **users_roles**: Many-to-many relationship between users and roles
- **habits**: User habits with streak information
- **check_ins**: Daily check-in records with unique constraints

The database file (`habits.db`) is created automatically in the project root.

## Configuration

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:sqlite:habits.db
spring.jpa.hibernate.ddl-auto=update

# Server
server.port=8080

# Thymeleaf (development)
spring.thymeleaf.cache=false
```

## Security Features

- Password encryption using BCrypt
- Session-based authentication
- CSRF protection enabled
- Role-based access control
- Secure password handling

## Customization

### Styling
- Modify `src/main/resources/static/css/style.css` for custom styles
- The application uses Bootstrap 5.3 for responsive design
- CSS custom properties for easy color scheme changes

### Charts
- Chart.js configuration in `src/main/resources/static/js/charts.js`
- Customizable colors, animations, and chart types
- Responsive design for mobile devices

### Database
- Switch to other databases by updating dependencies and configuration
- Current SQLite setup requires no additional installation

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**:
   ```bash
   # Kill existing Java processes
   pkill -f java
   # Or change port in application.properties
   server.port=8081
   ```

2. **Database connection issues**:
   - Ensure write permissions in the project directory
   - Check if `habits.db` file is created

3. **Template not found errors**:
   - Verify all template files are in `src/main/resources/templates/`
   - Check Thymeleaf syntax in templates

4. **Build failures**:
   ```bash
   # Clean and rebuild
   mvn clean install
   ```

### Development Mode

For development with auto-reload:
```bash
# Enable Thymeleaf cache disable
spring.thymeleaf.cache=false

# Use Spring Boot DevTools (add to pom.xml)
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review application logs in the console
3. Verify all dependencies are correctly installed
4. Ensure Java 17+ and Maven are properly configured

---

**Happy habit tracking!** 🎯

