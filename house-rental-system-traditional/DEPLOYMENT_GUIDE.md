# RentEase Deployment Guide

This guide provides comprehensive instructions for deploying the RentEase house rental system in various environments.

## 📋 Prerequisites

### System Requirements

- **Java**: OpenJDK 17 or higher
- **Maven**: 3.6.0 or higher
- **PostgreSQL**: 12.0 or higher
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: Minimum 1GB free space

### Development Tools (Optional)

- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Git**: For version control
- **Docker**: For containerized deployment
- **Nginx**: For reverse proxy (production)

## 🏠 Local Development Setup

### 1. Environment Preparation

```bash
# Verify Java installation
java -version

# Verify Maven installation
mvn -version

# Verify PostgreSQL installation
psql --version
```

### 2. Database Setup

```sql
-- Connect to PostgreSQL as superuser
sudo -u postgres psql

-- Create database
CREATE DATABASE house_rental_db;

-- Create application user
CREATE USER rental_user WITH PASSWORD 'rental_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE house_rental_db TO rental_user;

-- Grant schema privileges
\c house_rental_db
GRANT ALL ON SCHEMA public TO rental_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO rental_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO rental_user;

-- Exit PostgreSQL
\q
```

### 3. Application Configuration

Create or update `src/main/resources/application-dev.properties`:

```properties
# Development Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/house_rental_db
spring.datasource.username=rental_user
spring.datasource.password=rental_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration for Development
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
server.servlet.context-path=/

# Thymeleaf Development Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true

# Security Configuration
app.jwtSecret=devSecretKey123456789
app.jwtExpirationMs=86400000

# Logging Configuration
logging.level.com.houserental=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### 4. Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd house-rental-system-traditional

# Build the project
mvn clean install

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Alternative: Run the JAR file
java -jar target/house-rental-system-1.0.0.jar --spring.profiles.active=dev
```

### 5. Verify Installation

- Open browser: `http://localhost:8080`
- Check application logs for any errors
- Test user registration and login

## 🌐 Production Deployment

### 1. Server Preparation

#### Ubuntu/Debian Server Setup

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Install Nginx (optional, for reverse proxy)
sudo apt install nginx -y

# Create application user
sudo useradd -m -s /bin/bash rentease
sudo usermod -aG sudo rentease
```

#### CentOS/RHEL Server Setup

```bash
# Update system packages
sudo yum update -y

# Install Java 17
sudo yum install java-17-openjdk java-17-openjdk-devel -y

# Install PostgreSQL
sudo yum install postgresql-server postgresql-contrib -y
sudo postgresql-setup initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Install Nginx
sudo yum install nginx -y
```

### 2. Production Database Setup

```bash
# Switch to postgres user
sudo -u postgres psql

# Create production database
CREATE DATABASE house_rental_prod;

# Create production user with strong password
CREATE USER rental_prod_user WITH PASSWORD 'StrongProductionPassword123!';

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE house_rental_prod TO rental_prod_user;

# Configure PostgreSQL for production
sudo nano /etc/postgresql/12/main/postgresql.conf
```

Update PostgreSQL configuration:
```conf
# postgresql.conf
listen_addresses = 'localhost'
max_connections = 100
shared_buffers = 256MB
effective_cache_size = 1GB
```

Update authentication:
```bash
sudo nano /etc/postgresql/12/main/pg_hba.conf
```

```conf
# pg_hba.conf
local   house_rental_prod   rental_prod_user   md5
host    house_rental_prod   rental_prod_user   127.0.0.1/32   md5
```

Restart PostgreSQL:
```bash
sudo systemctl restart postgresql
```

### 3. Application Deployment

#### Create Production Configuration

Create `src/main/resources/application-prod.properties`:

```properties
# Production Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/house_rental_prod
spring.datasource.username=rental_prod_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration for Production
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080
server.servlet.context-path=/

# Thymeleaf Production Configuration
spring.thymeleaf.cache=true
spring.thymeleaf.enabled=true

# Security Configuration
app.jwtSecret=${JWT_SECRET}
app.jwtExpirationMs=86400000

# Logging Configuration
logging.level.com.houserental=INFO
logging.level.org.springframework.security=WARN
logging.file.name=/var/log/rentease/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# Performance Configuration
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

#### Build Production Package

```bash
# Build the application
mvn clean package -Pprod

# Copy JAR to server
scp target/house-rental-system-1.0.0.jar user@server:/opt/rentease/
```

#### Create Systemd Service

Create `/etc/systemd/system/rentease.service`:

```ini
[Unit]
Description=RentEase House Rental System
After=network.target postgresql.service

[Service]
Type=simple
User=rentease
Group=rentease
WorkingDirectory=/opt/rentease
ExecStart=/usr/bin/java -jar -Xmx1024m -Xms512m /opt/rentease/house-rental-system-1.0.0.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
Environment=DB_PASSWORD=StrongProductionPassword123!
Environment=JWT_SECRET=SuperSecretJWTKeyForProduction123456789
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

#### Start the Service

```bash
# Create application directory
sudo mkdir -p /opt/rentease
sudo mkdir -p /var/log/rentease
sudo chown -R rentease:rentease /opt/rentease
sudo chown -R rentease:rentease /var/log/rentease

# Copy application JAR
sudo cp house-rental-system-1.0.0.jar /opt/rentease/

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable rentease
sudo systemctl start rentease

# Check service status
sudo systemctl status rentease

# View logs
sudo journalctl -u rentease -f
```

### 4. Nginx Reverse Proxy Setup

Create `/etc/nginx/sites-available/rentease`:

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/ssl/certs/your-domain.crt;
    ssl_certificate_key /etc/ssl/private/your-domain.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Gzip Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied expired no-cache no-store private must-revalidate auth;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss;

    # Proxy Configuration
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Static Files Caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Security
    location ~ /\. {
        deny all;
    }
}
```

Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/rentease /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 🐳 Docker Deployment

### 1. Create Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Create runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Create non-root user
RUN groupadd -r rentease && useradd -r -g rentease rentease

# Copy JAR file
COPY --from=0 /app/target/house-rental-system-1.0.0.jar app.jar

# Change ownership
RUN chown rentease:rentease app.jar

# Switch to non-root user
USER rentease

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Create Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  database:
    image: postgres:15-alpine
    container_name: rentease-db
    environment:
      POSTGRES_DB: house_rental_db
      POSTGRES_USER: rental_user
      POSTGRES_PASSWORD: rental_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    networks:
      - rentease-network
    restart: unless-stopped

  application:
    build: .
    container_name: rentease-app
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/house_rental_db
      SPRING_DATASOURCE_USERNAME: rental_user
      SPRING_DATASOURCE_PASSWORD: rental_password
      JWT_SECRET: DockerSecretKey123456789
    ports:
      - "8080:8080"
    depends_on:
      - database
    networks:
      - rentease-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  nginx:
    image: nginx:alpine
    container_name: rentease-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/ssl
    depends_on:
      - application
    networks:
      - rentease-network
    restart: unless-stopped

volumes:
  postgres_data:

networks:
  rentease-network:
    driver: bridge
```

### 3. Deploy with Docker

```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f

# Scale application (if needed)
docker-compose up -d --scale application=3

# Stop services
docker-compose down

# Update application
docker-compose build application
docker-compose up -d application
```

## ☁️ Cloud Deployment

### AWS Deployment

#### Using AWS Elastic Beanstalk

1. **Prepare Application**
   ```bash
   mvn clean package
   ```

2. **Create Elastic Beanstalk Application**
   - Upload JAR file
   - Configure environment variables
   - Set up RDS PostgreSQL instance

3. **Environment Variables**
   ```
   SPRING_PROFILES_ACTIVE=aws
   SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds-endpoint:5432/house_rental_db
   SPRING_DATASOURCE_USERNAME=your-username
   SPRING_DATASOURCE_PASSWORD=your-password
   JWT_SECRET=your-jwt-secret
   ```

#### Using AWS ECS

1. **Create ECR Repository**
   ```bash
   aws ecr create-repository --repository-name rentease
   ```

2. **Build and Push Docker Image**
   ```bash
   docker build -t rentease .
   docker tag rentease:latest your-account.dkr.ecr.region.amazonaws.com/rentease:latest
   docker push your-account.dkr.ecr.region.amazonaws.com/rentease:latest
   ```

3. **Create ECS Task Definition and Service**

### Google Cloud Platform

#### Using Google App Engine

Create `app.yaml`:
```yaml
runtime: java17
service: default

env_variables:
  SPRING_PROFILES_ACTIVE: gcp
  SPRING_DATASOURCE_URL: jdbc:postgresql://google/house_rental_db?cloudSqlInstance=your-instance&socketFactory=com.google.cloud.sql.postgres.SocketFactory
  SPRING_DATASOURCE_USERNAME: your-username
  SPRING_DATASOURCE_PASSWORD: your-password
  JWT_SECRET: your-jwt-secret

automatic_scaling:
  min_instances: 1
  max_instances: 10
  target_cpu_utilization: 0.6
```

Deploy:
```bash
gcloud app deploy
```

### Heroku Deployment

1. **Create Heroku App**
   ```bash
   heroku create your-app-name
   ```

2. **Add PostgreSQL Add-on**
   ```bash
   heroku addons:create heroku-postgresql:hobby-dev
   ```

3. **Set Environment Variables**
   ```bash
   heroku config:set SPRING_PROFILES_ACTIVE=heroku
   heroku config:set JWT_SECRET=your-jwt-secret
   ```

4. **Deploy**
   ```bash
   git push heroku main
   ```

## 🔧 Monitoring and Maintenance

### Application Monitoring

#### Health Checks

Add to `application.properties`:
```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.db.enabled=true
```

#### Log Monitoring

```bash
# View application logs
sudo journalctl -u rentease -f

# View Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# Application log rotation
sudo nano /etc/logrotate.d/rentease
```

### Database Maintenance

#### Backup Strategy

```bash
# Create backup script
#!/bin/bash
BACKUP_DIR="/backup/postgresql"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U rental_prod_user house_rental_prod > $BACKUP_DIR/backup_$DATE.sql
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete
```

#### Performance Monitoring

```sql
-- Monitor database performance
SELECT * FROM pg_stat_activity;
SELECT * FROM pg_stat_database;

-- Check slow queries
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

### Security Updates

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Update application
mvn clean package
sudo systemctl stop rentease
sudo cp target/house-rental-system-1.0.0.jar /opt/rentease/
sudo systemctl start rentease
```

## 🚨 Troubleshooting

### Common Issues

1. **Application Won't Start**
   ```bash
   # Check logs
   sudo journalctl -u rentease -n 50
   
   # Check Java version
   java -version
   
   # Check database connection
   psql -h localhost -U rental_prod_user house_rental_prod
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL status
   sudo systemctl status postgresql
   
   # Check database exists
   sudo -u postgres psql -l
   
   # Test connection
   telnet localhost 5432
   ```

3. **Memory Issues**
   ```bash
   # Check memory usage
   free -h
   
   # Adjust JVM memory settings
   sudo nano /etc/systemd/system/rentease.service
   # Add: -Xmx2048m -Xms1024m
   ```

4. **SSL Certificate Issues**
   ```bash
   # Check certificate validity
   openssl x509 -in /etc/ssl/certs/your-domain.crt -text -noout
   
   # Test SSL configuration
   openssl s_client -connect your-domain.com:443
   ```

### Performance Optimization

1. **Database Optimization**
   ```sql
   -- Create indexes for frequently queried columns
   CREATE INDEX idx_house_city ON houses(city);
   CREATE INDEX idx_house_price ON houses(price_per_month);
   CREATE INDEX idx_booking_dates ON bookings(start_date, end_date);
   
   -- Analyze query performance
   EXPLAIN ANALYZE SELECT * FROM houses WHERE city = 'New York';
   ```

2. **Application Optimization**
   ```properties
   # Connection pooling
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=5
   spring.datasource.hikari.connection-timeout=20000
   
   # JPA optimization
   spring.jpa.properties.hibernate.jdbc.batch_size=25
   spring.jpa.properties.hibernate.order_inserts=true
   ```

## 📞 Support

For deployment issues:
- Check the troubleshooting section
- Review application logs
- Consult the main README.md
- Create an issue in the repository

---

This deployment guide covers various scenarios from local development to production cloud deployment. Choose the approach that best fits your infrastructure and requirements.

