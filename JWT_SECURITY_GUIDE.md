# JWT Security Implementation Guide

## Overview
Your application is now secured with JWT (JSON Web Token) authentication. All existing API endpoints require authentication.

## Changes Made

### 1. Dependencies Added
- `spring-boot-starter-security`: Spring Security framework
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT token handling

### 2. New Components Created

#### Models
- **User.java**: User entity with username, email, password, and roles

#### Repository
- **userRepository.java**: User data access layer

#### Security Utilities
- **JwtProvider.java**: Generates, validates, and parses JWT tokens
- **JwtAuthenticationFilter.java**: Intercepts requests and validates tokens
- **CustomUserDetailsService.java**: Loads user details for authentication
- **SecurityConfiguration.java**: Spring Security configuration
- **JwtAuthenticationEntryPoint.java**: Handles authentication errors

#### Services
- **AuthenticationService.java**: Handles login, register, and token refresh

#### Controllers
- **AuthenticationController.java**: Public endpoints for auth operations

#### DTOs
- **LoginRequest.java**: Login credentials
- **RegisterRequest.java**: Registration credentials
- **JwtResponse.java**: JWT token response

### 3. Existing Endpoints Protected
All book-related endpoints now require authentication:
- GET /Book/Repository
- GET /getBooks
- GET /book/{id}
- POST /addBook
- POST /book
- DELETE /delete/{id}
- GET /profile
- GET /pagable/{id}
- POST /sampleHttp

### 4. Configuration
JWT settings in `application.properties`:
```properties
jwt.secret=MyVerySecureSecretKeyFor256BitEncryptionJWTForTheApplicationTokenAuthentication
jwt.expiration=86400000  # 24 hours
jwt.refresh-expiration=604800000  # 7 days
```

## How to Use

### 1. Register a New User
**Endpoint**: `POST /auth/register`
**Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```
**Response**:
```json
{
  "status": "SUCCESS",
  "responseObject": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

### 2. Login User
**Endpoint**: `POST /auth/login`
**Request Body**:
```json
{
  "username": "john_doe",
  "password": "password123"
}
```
**Response**: Same as registration

### 3. Access Protected Endpoints
Include the JWT token in the Authorization header:
```
Authorization: Bearer <your_access_token>
```

**Example**: Get a book by ID
```bash
curl -X GET "http://localhost:8080/book/1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. Refresh Token
**Endpoint**: `POST /auth/refresh`
**Headers**:
```
Authorization: Bearer <your_refresh_token>
```
**Response**: Returns new access and refresh tokens

## Security Features

1. **Password Encryption**: Passwords are encrypted using BCrypt
2. **Role-Based Access**: Users have roles (e.g., USER, ADMIN)
3. **Token Validation**: Tokens are validated on every request
4. **Token Expiration**: Access tokens expire after 24 hours
5. **Refresh Token**: Allows users to get new access tokens without re-login
6. **CORS Support**: Configured to allow cross-origin requests

## Public Endpoints
- `POST /auth/register`: User registration
- `POST /auth/login`: User login
- `POST /auth/refresh`: Token refresh

All other endpoints require a valid JWT token.

## Testing with cURL

### Register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/getBooks?title=BookTitle \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Customization

### Change JWT Secret
Update in `application.properties`:
```properties
jwt.secret=your_very_secure_secret_key_here_min_32_chars
```

### Change Token Expiration
```properties
jwt.expiration=3600000  # 1 hour
jwt.refresh-expiration=86400000  # 1 day
```

### Add/Change Roles
Edit in `AuthenticationService.registerUser()`:
```java
Set<String> roles = new HashSet<>();
roles.add("USER");
roles.add("ADMIN");  // Add more roles as needed
user.setRoles(roles);
```

## Error Responses

### Unauthorized (401)
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/book/1"
}
```

### Invalid Credentials (401)
```json
{
  "status": "FAILED",
  "responseObject": "Bad credentials"
}
```

### User Already Exists (400)
```json
{
  "status": "FAILED",
  "responseObject": "Username is already taken!"
}
```

## Next Steps

1. **Test the application**: Start the app and test the auth endpoints
2. **Customize roles**: Add more roles as needed
3. **Add admin endpoints**: Configure endpoints that require ADMIN role
4. **Setup HTTPS**: For production, use HTTPS for secure token transmission
5. **Database persistence**: Configure a production database (MySQL, PostgreSQL) instead of H2

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Security Recommendations

1. **Change the JWT secret** to a strong, random value before deploying to production
2. **Use HTTPS** for all requests in production
3. **Store tokens securely** on the client side (HttpOnly cookies recommended)
4. **Implement logout** by maintaining a token blacklist
5. **Set appropriate token expiration times** based on your security requirements
6. **Enable CSRF protection** if needed
7. **Add rate limiting** to prevent brute force attacks
8. **Validate all user inputs** to prevent injection attacks

---

For more information on Spring Security and JWT, refer to:
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JJWT GitHub](https://github.com/jpadilla/pyjwt)

