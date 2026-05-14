# JWT Security - Troubleshooting Guide

## Common Issues & Solutions

### 1. 401 Unauthorized Error on Protected Endpoints

**Problem**: Getting `401 Unauthorized` when accessing protected endpoints

**Causes & Solutions**:

#### a) Missing Authorization Header
❌ **Wrong**:
```bash
curl http://localhost:8080/getBooks?title=Java
```

✅ **Correct**:
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/getBooks?title=Java
```

#### b) Incorrect Token Format
❌ **Wrong**: `Authorization: YOUR_TOKEN`
❌ **Wrong**: `Authorization: JWT YOUR_TOKEN`

✅ **Correct**: `Authorization: Bearer YOUR_TOKEN`

#### c) Expired Token
- Access tokens expire after 24 hours
- Use the refresh token to get a new access token:
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Authorization: Bearer REFRESH_TOKEN"
```

#### d) Invalid Token
- Check if token is not tampered with
- Verify token using [jwt.io](https://jwt.io)
- Re-login if token is invalid

### 2. 400 Bad Request on /auth/register

**Problem**: Registration fails with `400 Bad Request`

**Common Reasons**:

```json
// Missing required fields
{
  "username": "user123",
  // missing email and password
}
```

**Solution**: Ensure all required fields are present:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### 3. Username/Email Already Exists

**Error Response**:
```json
{
  "status": "FAILED",
  "responseObject": "Username is already taken!"
}
```

**Solution**: Use a different username or email:
```bash
# First, check what users exist by trying different usernames
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "unique_user_12345",
    "email": "new_email_12345@example.com",
    "password": "password123"
  }'
```

### 4. Invalid Email Format

**Error Response**:
```json
{
  "status": "FAILED",
  "responseObject": "Email should be valid"
}
```

**Solution**: Provide a valid email address:
```json
{
  "username": "john_doe",
  "email": "john@example.com",  // Must have @ and domain
  "password": "password123"
}
```

### 5. Password Too Short

**Error Response**:
```json
{
  "status": "FAILED",
  "responseObject": "Password must be at least 6 characters"
}
```

**Solution**: Use a password with at least 6 characters:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secure_password_123"  // Min 6 characters
}
```

### 6. Cannot Connect to localhost:8080

**Problem**: Connection refused error

**Solutions**:
1. Ensure the application is running:
```bash
cd /Users/lakshaychaudhary/Downloads/com.library.system
./mvnw spring-boot:run
```

2. Check if port 8080 is available:
```bash
# On macOS
lsof -i :8080

# If occupied, kill the process
kill -9 <PID>
```

3. Change port in `application.properties`:
```properties
server.port=8090
```

### 7. Token is Valid but Still Getting 401

**Problem**: Token appears valid but still returns 401

**Solutions**:

1. **Check token format**:
   - Must have expired yet: Check `exp` claim in token

2. **Verify token in code**:
   - Go to [jwt.io](https://jwt.io)
   - Paste your token
   - Check the payload
   - Verify `exp` timestamp (is it in the past?)

3. **Restart application**:
   - Security context might be stale
   - Restart the Spring Boot application

4. **Check logs**:
   - Look for JWT validation errors in console

### 8. CORS Errors in Browser

**Problem**: Cross-Origin Request Blocked

**Error in Console**:
```
Access to XMLHttpRequest at 'http://localhost:8080/getBooks'
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solution**: Already handled in SecurityConfiguration, but verify:

```java
// In AuthenticationController and other controllers
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthenticationController {
  // ...
}
```

### 9. NullPointerException on JWT Parsing

**Problem**: Application crashes when parsing token

**Causes & Solutions**:

```
// Check if token is null or empty
if (token == null || token.isEmpty()) {
    return null; // Handle gracefully
}
```

**Solution**: Ensure token is passed correctly:
```bash
# Verify Authorization header is present
curl -v http://localhost:8080/getBooks
# Look for "Authorization:" in the request headers
```

### 10. Database Connection Issues

**Problem**: User records not being saved

**Solutions**:

1. **H2 Console Access**:
   - URL: `http://localhost:8080/h2-console`
   - Check if `users` and `user_roles` tables exist

2. **Check H2 database configuration**:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3. **Enable detailed logging**:
```properties
logging.level.root=INFO
logging.level.com.library.manG.System=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Debugging Tips

### 1. Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.com.library=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.web.FilterChainProxy=DEBUG
```

### 2. Use Application Logs

Check Spring Boot console output for:
- Authentication attempts
- Token validation results
- Database operations

### 3. Test with Simpler Requests

Start with basic requests:
```bash
# 1. Test server is running
curl http://localhost:8080/name

# 2. Test registration
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"123456"}'

# 3. Test protected endpoint
curl http://localhost:8080/getBooks
# Should return 401 without token
```

### 4. Decode JWT Tokens

Visit [jwt.io](https://jwt.io) and paste your token:
- Verify claims
- Check expiration time
- Verify signature (should show verified if using correct secret)

### 5. Check Request/Response Headers

Using cURL with verbose flag:
```bash
curl -v -X GET http://localhost:8080/getBooks \
  -H "Authorization: Bearer TOKEN"
```

Look for:
- `Authorization:` header in request
- `HTTP/1.1` response code
- `Content-Type:` in response

## Performance Optimization

### 1. Cache User Details
Implement caching in `CustomUserDetailsService`:
```java
@Service
@EnableCaching
public class CustomUserDetailsService {
    
    @Cacheable(value = "userDetails", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Implementation
    }
}
```

### 2. Reduce Token Size
Remove unnecessary claims to reduce token size and improve performance.

### 3. Connection Pooling
Configure database connection pooling in `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## Security Best Practices Check

Verify your implementation has:
- ✅ Strong JWT secret (min 32 characters)
- ✅ Appropriate token expiration times
- ✅ Password encryption with BCrypt
- ✅ HTTPS in production
- ✅ Secure token storage (HttpOnly cookies recommended)
- ✅ CORS properly configured
- ✅ Input validation on all endpoints
- ✅ Error messages don't expose sensitive info
- ✅ Logging for security events
- ✅ CSRF protection if needed

## Getting Help

1. **Check Logs**: First place to look for errors
2. **Verify Input**: Ensure all data is in correct format
3. **Use JWT.io**: Decode tokens to verify content
4. **Check Network**: Verify Authorization header is sent
5. **Restart Application**: Clear any stale state
6. **Review Documentation**: See JWT_SECURITY_GUIDE.md

## Quick Checklist

Before reporting an issue:
- [ ] Application is running
- [ ] Port 8080 is accessible
- [ ] Database is initialized
- [ ] Token is properly formatted
- [ ] Token is not expired
- [ ] Authorization header is included with "Bearer " prefix
- [ ] User credentials are correct
- [ ] Required fields are provided
- [ ] Email format is valid
- [ ] Password meets minimum requirements

---

**Happy Debugging!** 🔍

If issue persists after checking these guidelines:
1. Enable debug logging
2. Check Spring Boot console output
3. Verify configuration in application.properties
4. Restart the application

