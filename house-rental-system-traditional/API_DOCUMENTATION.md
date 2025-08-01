# RentEase API Documentation

This document provides comprehensive information about the RentEase house rental system's web endpoints and API structure.

## 📋 Overview

RentEase uses a traditional web application architecture with server-side rendering using Thymeleaf templates. The application provides both web pages for user interaction and RESTful endpoints for data operations.

### Base URL
- **Development**: `http://localhost:8080`
- **Production**: `https://your-domain.com`

### Authentication
The application uses Spring Security with session-based authentication and CSRF protection.

## 🔐 Authentication Endpoints

### Login
**GET** `/login`
- **Description**: Display login page
- **Response**: Login form HTML page
- **Parameters**: 
  - `error` (optional): Error message parameter
  - `logout` (optional): Logout confirmation parameter

**POST** `/perform_login`
- **Description**: Process user login
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**:
  ```
  username: string (required)
  password: string (required)
  remember-me: boolean (optional)
  ```
- **Success Response**: Redirect to dashboard or intended page
- **Error Response**: Redirect to login with error parameter

### Registration
**GET** `/register`
- **Description**: Display registration page
- **Response**: Registration form HTML page

**POST** `/register`
- **Description**: Process user registration
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**:
  ```
  firstName: string (required, min: 2, max: 50)
  lastName: string (required, min: 2, max: 50)
  username: string (required, min: 3, max: 20, unique)
  email: string (required, valid email format, unique)
  phoneNumber: string (optional, valid phone format)
  password: string (required, min: 6)
  role: enum (required, values: TENANT, LANDLORD)
  ```
- **Success Response**: Redirect to login with success message
- **Error Response**: Registration form with validation errors

### Logout
**POST** `/logout`
- **Description**: Process user logout
- **Response**: Redirect to home page

## 🏠 Public Pages

### Home Page
**GET** `/`
- **Description**: Display home page with featured properties
- **Response**: Home page HTML with property listings
- **Query Parameters**:
  - `page` (optional): Page number for pagination (default: 0)

### Property Listings
**GET** `/houses`
- **Description**: Display all available properties
- **Response**: Property listings page HTML
- **Query Parameters**:
  - `page` (optional): Page number for pagination (default: 0)
  - `size` (optional): Number of items per page (default: 12)

### Property Details
**GET** `/houses/{id}`
- **Description**: Display detailed information about a specific property
- **Path Parameters**:
  - `id`: Long (required) - Property ID
- **Response**: Property details page HTML
- **Error Response**: Redirect to `/houses` if property not found

### Search Properties
**GET** `/search`
- **Description**: Search and filter properties
- **Response**: Search results page HTML
- **Query Parameters**:
  ```
  keyword: string (optional) - Search in title and description
  city: string (optional) - Filter by city
  state: string (optional) - Filter by state
  minPrice: decimal (optional) - Minimum monthly price
  maxPrice: decimal (optional) - Maximum monthly price
  bedrooms: integer (optional) - Minimum number of bedrooms
  bathrooms: integer (optional) - Minimum number of bathrooms
  propertyType: enum (optional) - Property type (HOUSE, APARTMENT, CONDO, TOWNHOUSE)
  page: integer (optional) - Page number (default: 0)
  ```

## 🔒 Protected Pages (Authentication Required)

### Dashboard
**GET** `/dashboard`
- **Description**: Display user dashboard with statistics and quick actions
- **Authentication**: Required
- **Response**: Dashboard HTML page with user-specific data
- **Data Included**:
  - User information
  - Role-specific statistics (properties count for landlords, bookings count for tenants)
  - Quick action links

## 🏘️ Property Management (Landlord Only)

### My Properties
**GET** `/my-houses`
- **Description**: Display landlord's property listings
- **Authentication**: Required (LANDLORD role)
- **Response**: Property management page HTML

### Add Property Form
**GET** `/my-houses/add`
- **Description**: Display form to add new property
- **Authentication**: Required (LANDLORD role)
- **Response**: Add property form HTML

### Add Property
**POST** `/my-houses/add`
- **Description**: Create new property listing
- **Authentication**: Required (LANDLORD role)
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**:
  ```
  title: string (required, max: 100)
  description: string (required, max: 2000)
  pricePerMonth: decimal (required, min: 0)
  address: string (required, max: 200)
  city: string (required, max: 50)
  state: string (required, max: 50)
  zipCode: string (required, max: 10)
  bedrooms: integer (required, min: 0, max: 20)
  bathrooms: integer (required, min: 0, max: 20)
  squareFeet: integer (optional, min: 0)
  propertyType: enum (required, values: HOUSE, APARTMENT, CONDO, TOWNHOUSE)
  amenities: string[] (optional)
  images: string[] (optional, URLs)
  availabilityStatus: enum (optional, default: AVAILABLE)
  ```
- **Success Response**: Redirect to `/my-houses` with success message
- **Error Response**: Add property form with validation errors

### Edit Property Form
**GET** `/my-houses/edit/{id}`
- **Description**: Display form to edit existing property
- **Authentication**: Required (LANDLORD role, property owner)
- **Path Parameters**:
  - `id`: Long (required) - Property ID
- **Response**: Edit property form HTML
- **Error Response**: Redirect to `/my-houses` if not authorized

### Update Property
**POST** `/my-houses/edit/{id}`
- **Description**: Update existing property
- **Authentication**: Required (LANDLORD role, property owner)
- **Path Parameters**:
  - `id`: Long (required) - Property ID
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**: Same as add property
- **Success Response**: Redirect to `/my-houses` with success message
- **Error Response**: Edit property form with validation errors

### Delete Property
**POST** `/my-houses/delete/{id}`
- **Description**: Delete property listing
- **Authentication**: Required (LANDLORD role, property owner)
- **Path Parameters**:
  - `id`: Long (required) - Property ID
- **Success Response**: Redirect to `/my-houses` with success message
- **Error Response**: Redirect to `/my-houses` with error message

## 📅 Booking Management

### My Bookings
**GET** `/my-bookings`
- **Description**: Display user's bookings (tenant view) or booking requests (landlord view)
- **Authentication**: Required
- **Response**: Bookings page HTML with role-specific data

### Book Property Form
**GET** `/book/{houseId}`
- **Description**: Display booking form for a specific property
- **Authentication**: Required (TENANT role)
- **Path Parameters**:
  - `houseId`: Long (required) - Property ID
- **Response**: Booking form HTML
- **Error Response**: Redirect to `/houses` if property not found

### Create Booking
**POST** `/book/{houseId}`
- **Description**: Create new booking request
- **Authentication**: Required (TENANT role)
- **Path Parameters**:
  - `houseId`: Long (required) - Property ID
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**:
  ```
  startDate: date (required, format: YYYY-MM-DD, future date)
  endDate: date (required, format: YYYY-MM-DD, after start date)
  numberOfGuests: integer (required, min: 1)
  specialRequests: string (optional, max: 500)
  ```
- **Success Response**: Redirect to `/my-bookings` with success message
- **Error Response**: Booking form with validation errors

### Approve Booking
**POST** `/bookings/{id}/approve`
- **Description**: Approve booking request
- **Authentication**: Required (LANDLORD role, property owner)
- **Path Parameters**:
  - `id`: Long (required) - Booking ID
- **Success Response**: Redirect to `/my-bookings` with success message
- **Error Response**: Redirect to `/my-bookings` with error message

### Reject Booking
**POST** `/bookings/{id}/reject`
- **Description**: Reject booking request
- **Authentication**: Required (LANDLORD role, property owner)
- **Path Parameters**:
  - `id`: Long (required) - Booking ID
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parameters**:
  ```
  rejectionReason: string (required, max: 500)
  ```
- **Success Response**: Redirect to `/my-bookings` with success message
- **Error Response**: Redirect to `/my-bookings` with error message

### Cancel Booking
**POST** `/bookings/{id}/cancel`
- **Description**: Cancel booking (tenant or landlord)
- **Authentication**: Required (booking participant)
- **Path Parameters**:
  - `id`: Long (required) - Booking ID
- **Success Response**: Redirect to `/my-bookings` with success message
- **Error Response**: Redirect to `/my-bookings` with error message

## 📊 Data Models

### User Entity
```json
{
  "id": "Long",
  "firstName": "String",
  "lastName": "String",
  "username": "String (unique)",
  "email": "String (unique)",
  "phoneNumber": "String (optional)",
  "role": "Enum (TENANT, LANDLORD)",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### House Entity
```json
{
  "id": "Long",
  "title": "String",
  "description": "String",
  "pricePerMonth": "BigDecimal",
  "address": "String",
  "city": "String",
  "state": "String",
  "zipCode": "String",
  "bedrooms": "Integer",
  "bathrooms": "Integer",
  "squareFeet": "Integer (optional)",
  "propertyType": "Enum (HOUSE, APARTMENT, CONDO, TOWNHOUSE)",
  "amenities": "List<String>",
  "images": "List<String>",
  "availabilityStatus": "Enum (AVAILABLE, RENTED, MAINTENANCE)",
  "owner": "User",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Booking Entity
```json
{
  "id": "Long",
  "house": "House",
  "tenant": "User",
  "startDate": "LocalDate",
  "endDate": "LocalDate",
  "numberOfGuests": "Integer",
  "totalAmount": "BigDecimal",
  "status": "Enum (PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED)",
  "specialRequests": "String (optional)",
  "rejectionReason": "String (optional)",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Review Entity
```json
{
  "id": "Long",
  "house": "House",
  "reviewer": "User",
  "rating": "Integer (1-5)",
  "comment": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## 🔒 Security Features

### CSRF Protection
All POST requests require CSRF tokens. Thymeleaf automatically includes CSRF tokens in forms.

### Session Management
- Session timeout: 30 minutes of inactivity
- Remember-me functionality: 2 weeks
- Concurrent session control: Maximum 1 session per user

### Input Validation
- Server-side validation using Bean Validation (JSR-303)
- Client-side validation using HTML5 and JavaScript
- XSS protection through Thymeleaf escaping

### Authorization
- Role-based access control (RBAC)
- Method-level security annotations
- URL-based security configuration

## 📝 Response Formats

### Success Responses
- **Web Pages**: HTML content with appropriate HTTP status codes
- **Redirects**: HTTP 302 with Location header
- **Flash Messages**: Success messages displayed on subsequent page load

### Error Responses
- **Validation Errors**: Form redisplay with error messages
- **Authentication Errors**: Redirect to login page
- **Authorization Errors**: HTTP 403 Forbidden
- **Not Found Errors**: HTTP 404 or redirect to appropriate page

## 🔧 Configuration

### Application Properties
Key configuration properties for API behavior:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Security Configuration
app.jwtSecret=mySecretKey
app.jwtExpirationMs=86400000

# Session Configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

## 🧪 Testing Endpoints

### Manual Testing
Use browser developer tools or tools like Postman to test endpoints:

1. **Authentication Flow**
   ```
   GET /login → POST /perform_login → GET /dashboard
   ```

2. **Property Management Flow**
   ```
   GET /my-houses → GET /my-houses/add → POST /my-houses/add
   ```

3. **Booking Flow**
   ```
   GET /houses/{id} → GET /book/{id} → POST /book/{id}
   ```

### Automated Testing
```bash
# Run all tests
mvn test

# Run integration tests
mvn verify

# Run specific test class
mvn test -Dtest=HomeControllerTest
```

## 📞 Support

For API-related questions:
- Check endpoint documentation above
- Review Spring Security configuration
- Consult Spring Boot documentation
- Create an issue in the repository

## 🔄 API Versioning

Currently, the application uses a single version. Future versions will be handled through:
- URL versioning: `/api/v1/`, `/api/v2/`
- Header versioning: `Accept: application/vnd.rentease.v1+json`

## 📈 Rate Limiting

For production deployment, consider implementing rate limiting:
- Per-user limits: 100 requests per minute
- Per-IP limits: 1000 requests per hour
- Search endpoint limits: 10 requests per minute

---

This API documentation covers all available endpoints in the RentEase house rental system. The application follows RESTful principles and Spring Boot conventions for consistent and predictable behavior.

