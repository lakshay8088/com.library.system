# Quick API Testing Guide

## Prerequisites
- Application running on `http://localhost:8080`
- A REST client (Postman, Insomnia, Thunder Client, or cURL)

## Test Sequence

### Step 1: Register a New User
```
Method: POST
URL: http://localhost:8080/auth/register
Content-Type: application/json

Body:
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```
**Save the `accessToken` from the response**

### Step 2: Login (Alternative to Register)
```
Method: POST
URL: http://localhost:8080/auth/login
Content-Type: application/json

Body:
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

### Step 3: Add a Book
```
Method: POST
URL: http://localhost:8080/addBook
Authorization: Bearer <accessToken_from_step1>
Content-Type: application/json

Body:
{
  "title": "Java",
  "author": "James",
  "price": 150,
  "publishedYear": 2020
}
```

### Step 4: Get All Books by Title
```
Method: GET
URL: http://localhost:8080/getBooks?title=Java
Authorization: Bearer <accessToken_from_step1>
```

### Step 5: Get Book by ID
```
Method: GET
URL: http://localhost:8080/book/1
Authorization: Bearer <accessToken_from_step1>
```

### Step 6: Get Price by ID
```
Method: GET
URL: http://localhost:8080/Book/Repository?id=1
Authorization: Bearer <accessToken_from_step1>
```

### Step 7: Search Books by Title
```
Method: POST
URL: http://localhost:8080/book?title=Java
Authorization: Bearer <accessToken_from_step1>
```

### Step 8: Delete a Book
```
Method: DELETE
URL: http://localhost:8080/delete/1
Authorization: Bearer <accessToken_from_step1>
```

### Step 9: Refresh Token
```
Method: POST
URL: http://localhost:8080/auth/refresh
Authorization: Bearer <refreshToken_from_step1>
```

### Step 10: Get Paginated Books
```
Method: GET
URL: http://localhost:8080/pagable/0
Authorization: Bearer <accessToken_from_step1>
```

## Public Endpoints (No Authorization Required)
```
Method: GET
URL: http://localhost:8080/name

Method: GET
URL: http://localhost:8080/profile
Authorization: Bearer <accessToken>
```

## Common Error Responses

### 401 Unauthorized - Missing Token
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/book/1"
}
```

### 401 Unauthorized - Invalid Token
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid JWT token",
  "path": "/book/1"
}
```

### 400 Bad Request - User Already Exists
```json
{
  "status": "FAILED",
  "responseObject": "Username is already taken!"
}
```

### 401 Unauthorized - Wrong Credentials
```json
{
  "status": "FAILED",
  "responseObject": "Bad credentials"
}
```

## Sample Complete Workflow using cURL

```bash
# 1. Register User
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

echo "Registration Response:"
echo $REGISTER_RESPONSE | jq '.'

# 2. Extract Access Token (using jq)
ACCESS_TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.responseObject.accessToken')

echo "Access Token: $ACCESS_TOKEN"

# 3. Add a Book using the token
curl -X POST http://localhost:8080/addBook \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring",
    "author": "Guide",
    "price": 200,
    "publishedYear": 2023
  }' | jq '.'

# 4. Get All Books
curl -X GET "http://localhost:8080/getBooks?title=Spring" \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq '.'

# 5. Get Book by ID
curl -X GET http://localhost:8080/book/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq '.'
```

## Token Information

Each JWT token contains:
- **sub (subject)**: Username
- **iat (issued at)**: Token issue time
- **exp (expiration)**: Token expiration time
- **email**: User's email (in claims)

You can decode the token at [jwt.io](https://jwt.io) to see the payload.

## Tips for Testing

1. **Use Postman/Insomnia**: Make it easier to manage tokens and requests
2. **Save tokens to variables**: Reuse across multiple requests
3. **Set token expiration alerts**: Know when to refresh
4. **Check token validity**: Use jwt.io for debugging
5. **Monitor logs**: Check Spring Boot logs for detailed error information

---

**Happy Testing!** 🎉

