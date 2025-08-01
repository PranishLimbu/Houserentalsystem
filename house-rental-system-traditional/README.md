# RentEase - House Rental System

A comprehensive full-stack house rental management system built with **Spring Boot**, **PostgreSQL**, and traditional web technologies (HTML, CSS, JavaScript). This system provides a complete solution for property rental management with separate interfaces for tenants and landlords.

## 🏠 Project Overview

RentEase is a modern, responsive web application that facilitates house rental operations. It features user authentication, property management, booking systems, and review functionality, all wrapped in an attractive and user-friendly interface.

### Key Features

- **User Management**: Registration and authentication for tenants and landlords
- **Property Management**: Add, edit, search, and manage rental properties
- **Advanced Search**: Multi-criteria filtering (location, price, bedrooms, bathrooms, type)
- **Booking System**: Complete booking workflow with approval/rejection process
- **Review System**: Rate and review properties
- **Responsive Design**: Mobile-friendly interface that works on all devices
- **Role-based Access**: Different dashboards and permissions for tenants and landlords

## 🛠 Technology Stack

### Backend
- **Spring Boot 3.1.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Primary database
- **Thymeleaf** - Server-side templating engine
- **Maven** - Dependency management

### Frontend
- **HTML5** - Markup
- **CSS3** - Styling with custom design system
- **JavaScript (ES6+)** - Interactive functionality
- **Bootstrap 5.3** - UI framework
- **Font Awesome 6.4** - Icons

### Database
- **PostgreSQL 12+** - Relational database
- **JPA/Hibernate** - ORM mapping

## 📁 Project Structure

```
house-rental-system-traditional/
├── src/
│   ├── main/
│   │   ├── java/com/houserental/
│   │   │   ├── HouseRentalApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   └── HouseController.java
│   │   │   ├── entity/
│   │   │   │   ├── Booking.java
│   │   │   │   ├── House.java
│   │   │   │   ├── Review.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── BookingRepository.java
│   │   │   │   ├── HouseRepository.java
│   │   │   │   ├── ReviewRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── BookingService.java
│   │   │       ├── HouseService.java
│   │   │       ├── ReviewService.java
│   │   │       ├── UserDetailsServiceImpl.java
│   │   │       └── UserService.java
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   │   ├── css/style.css
│   │   │   │   └── js/main.js
│   │   │   ├── templates/
│   │   │   │   ├── layout.html
│   │   │   │   ├── index.html
│   │   │   │   ├── login.html
│   │   │   │   ├── register.html
│   │   │   │   ├── dashboard.html
│   │   │   │   └── search.html
│   │   │   └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd house-rental-system-traditional
   ```

2. **Database Setup**
   ```sql
   -- Create database
   CREATE DATABASE house_rental_db;
   
   -- Create user (optional)
   CREATE USER rental_user WITH PASSWORD 'rental_password';
   GRANT ALL PRIVILEGES ON DATABASE house_rental_db TO rental_user;
   ```

3. **Configure Database Connection**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # Update these values according to your PostgreSQL setup
   spring.datasource.url=jdbc:postgresql://localhost:5432/house_rental_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and Run**
   ```bash
   # Build the project
   mvn clean install
   
   # Run the application
   mvn spring-boot:run
   ```

5. **Access the Application**
   
   Open your browser and navigate to: `http://localhost:8080`

## 🔧 Configuration

### Database Configuration

The application uses PostgreSQL as the primary database. Configure the connection in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/house_rental_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
server.servlet.context-path=/

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Security Configuration
app.jwtSecret=mySecretKey
app.jwtExpirationMs=86400000

# Logging Configuration
logging.level.com.houserental=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Environment Variables

For production deployment, consider using environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/house_rental_db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
```

## 👥 User Roles and Permissions

### Tenant Role
- Search and browse properties
- Book properties
- View booking history
- Leave reviews for properties
- Manage personal profile

### Landlord Role
- Add and manage properties
- View and manage booking requests
- Approve/reject booking requests
- View property analytics
- Manage personal profile

## 🎨 UI/UX Features

### Design System
- **Modern gradient backgrounds** with primary and secondary colors
- **Responsive grid layout** that adapts to all screen sizes
- **Interactive hover effects** and smooth transitions
- **Professional typography** with clear hierarchy
- **Consistent spacing** and visual rhythm

### Key UI Components
- **Navigation bar** with role-based menu items
- **Hero section** with search functionality
- **Property cards** with image galleries and key information
- **Advanced search filters** with real-time updates
- **Dashboard widgets** with statistics and quick actions
- **Form validation** with user-friendly error messages

## 📱 Responsive Design

The application is fully responsive and optimized for:
- **Desktop** (1200px+)
- **Tablet** (768px - 1199px)
- **Mobile** (320px - 767px)

## 🔐 Security Features

- **Password encryption** using BCrypt
- **Session management** with Spring Security
- **CSRF protection** enabled
- **Role-based access control** (RBAC)
- **Input validation** and sanitization
- **SQL injection prevention** through JPA

## 🧪 Testing

### Manual Testing Checklist

1. **User Registration and Login**
   - [ ] Register new tenant account
   - [ ] Register new landlord account
   - [ ] Login with valid credentials
   - [ ] Login with invalid credentials

2. **Property Management (Landlord)**
   - [ ] Add new property
   - [ ] Edit existing property
   - [ ] Delete property
   - [ ] View property list

3. **Property Search and Booking (Tenant)**
   - [ ] Search properties by location
   - [ ] Filter by price range
   - [ ] Filter by bedrooms/bathrooms
   - [ ] Book a property
   - [ ] View booking history

4. **Booking Management**
   - [ ] Landlord approves booking
   - [ ] Landlord rejects booking
   - [ ] Tenant cancels booking

### Running Tests

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

## 🚀 Deployment

### Local Development

```bash
mvn spring-boot:run
```

### Production Deployment

1. **Build JAR file**
   ```bash
   mvn clean package
   ```

2. **Run with production profile**
   ```bash
   java -jar target/house-rental-system-1.0.0.jar --spring.profiles.active=prod
   ```

### Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/house-rental-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t house-rental-system .
docker run -p 8080:8080 house-rental-system
```

## 📊 Database Schema

### Main Entities

1. **Users** - Store user information and authentication data
2. **Houses** - Property listings with details and amenities
3. **Bookings** - Rental bookings with dates and status
4. **Reviews** - Property reviews and ratings

### Entity Relationships

- User (1) → Houses (N) - One landlord can own multiple properties
- User (1) → Bookings (N) - One tenant can have multiple bookings
- House (1) → Bookings (N) - One property can have multiple bookings
- House (1) → Reviews (N) - One property can have multiple reviews
- User (1) → Reviews (N) - One user can write multiple reviews

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify PostgreSQL is running
   - Check database credentials in `application.properties`
   - Ensure database exists

2. **Port Already in Use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

3. **Maven Build Fails**
   ```bash
   # Clean and rebuild
   mvn clean install -U
   ```

4. **Thymeleaf Template Not Found**
   - Check template file names and locations
   - Verify Thymeleaf configuration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Email: support@rentease.com
- Documentation: [Wiki](wiki-url)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the UI components
- Font Awesome for the icon library
- PostgreSQL community for the robust database

---

**RentEase** - Making house rental simple, secure, and efficient! 🏠✨

