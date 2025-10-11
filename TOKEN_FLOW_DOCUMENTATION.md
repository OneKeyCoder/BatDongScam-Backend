# JWT Token Flow Documentation

## Overview
Your JWT authentication is now fully **STATELESS** with proper token refresh flow and exception handling.

---

## Token Flow Scenarios

### 1. **Login** (Get New Tokens)
```
Client → POST /auth/login
Body: { email, password }

Success Response (200):
{
  "token": "eyJhbGc...",           // Access Token (short-lived)
  "refreshToken": "eyJhbGc...",    // Refresh Token (long-lived)
  "userId": "uuid",
  "role": "USER"
}

Error Responses:
- 401 Unauthorized: Wrong email/password
- 422 Unprocessable Entity: Validation errors
```

---

### 2. **Access Protected Resource** (Using Access Token)
```
Client → GET /api/protected-endpoint
Header: Authorization: Bearer {access_token}

Success (200): Resource data returned

Error Responses:
- 401 Unauthorized: "Access token expired. Please refresh your token."
  → Client should call refresh endpoint
- 401 Unauthorized: "Invalid JWT token"
  → Token is malformed or tampered
```

---

### 3. **Refresh Tokens** (When Access Token Expires)
```
Client → GET /auth/refresh
Header: Authorization: Bearer {refresh_token}

Success Response (200):
{
  "token": "eyJhbGc...",           // NEW Access Token
  "refreshToken": "eyJhbGc...",    // NEW Refresh Token
  "userId": "uuid",
  "role": "USER"
}

Error Responses:
- 401 Unauthorized: "Refresh token expired. Please login again."
  → Both tokens expired, user must login
- 401 Unauthorized: "Invalid refresh token"
  → Refresh token is invalid or tampered
```

---

### 4. **Logout**
```
Client → GET /auth/logout
Header: Authorization: Bearer {access_token}

Success (200): Logout successful

Note: In stateless JWT, logout is handled CLIENT-SIDE
- Client must discard both access and refresh tokens
- Tokens will expire naturally based on their expiration time
```

---

## Client-Side Implementation Guide

### Recommended Flow:

```javascript
// 1. Store tokens after login
localStorage.setItem('access_token', response.token);
localStorage.setItem('refresh_token', response.refreshToken);

// 2. Make API request with access token
async function apiRequest(url) {
  const accessToken = localStorage.getItem('access_token');
  
  let response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  
  // 3. If access token expired, use refresh token
  if (response.status === 401) {
    const errorData = await response.json();
    
    if (errorData.message.includes('expired')) {
      // Try to refresh tokens
      const refreshed = await refreshTokens();
      
      if (refreshed) {
        // Retry original request with new token
        const newAccessToken = localStorage.getItem('access_token');
        response = await fetch(url, {
          headers: {
            'Authorization': `Bearer ${newAccessToken}`
          }
        });
      } else {
        // Refresh failed, redirect to login
        window.location.href = '/login';
      }
    }
  }
  
  return response;
}

// 4. Refresh tokens function
async function refreshTokens() {
  const refreshToken = localStorage.getItem('refresh_token');
  
  try {
    const response = await fetch('/auth/refresh', {
      headers: {
        'Authorization': `Bearer ${refreshToken}`
      }
    });
    
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('access_token', data.data.token);
      localStorage.setItem('refresh_token', data.data.refreshToken);
      return true;
    } else {
      // Refresh token expired, clear storage
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      return false;
    }
  } catch (error) {
    console.error('Token refresh failed:', error);
    return false;
  }
}

// 5. Logout function
async function logout() {
  const accessToken = localStorage.getItem('access_token');
  
  await fetch('/auth/logout', {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  
  // Clear tokens from storage
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
  
  // Redirect to login
  window.location.href = '/login';
}
```

---

## Exception Handling

All authentication errors return proper HTTP status codes and messages:

### 400 Bad Request
- Malformed JSON
- Invalid request parameters
- Missing required headers

### 401 Unauthorized
- **"Access token expired. Please refresh your token."**
  - Access token has expired
  - Client should call `/auth/refresh` with refresh token

- **"Refresh token expired. Please login again."**
  - Refresh token has expired
  - Client must redirect to login page

- **"Invalid JWT token"** / **"Invalid JWT signature"**
  - Token has been tampered with
  - Token format is incorrect

- **Wrong email or password** (from login)
  - Authentication credentials are incorrect

### 403 Forbidden
- User doesn't have permission to access resource

### 422 Unprocessable Entity
- Validation errors (e.g., invalid email format, missing fields)

---

## Token Expiration Configuration

Configure in `application.yaml`:

```yaml
app:
  secret: "your-secret-key-must-be-at-least-256-bits"
  jwt:
    token:
      expires-in: 3600000        # 1 hour in milliseconds
    refresh-token:
      expires-in: 604800000      # 7 days in milliseconds
```

**Recommendations:**
- **Access Token**: 15-60 minutes (short-lived for security)
- **Refresh Token**: 7-30 days (long-lived for convenience)

---

## Security Features Implemented

✅ **Stateless Authentication**: No server-side session storage  
✅ **JWT Signature Verification**: Cryptographic validation  
✅ **Expiration Validation**: Tokens expire automatically  
✅ **Proper Error Messages**: Clear guidance for clients  
✅ **Token Refresh Flow**: Seamless token renewal  
✅ **Exception Handling**: Comprehensive error coverage  

---

## Testing the Flow

### 1. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### 2. Access Protected Resource
```bash
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer {access_token}"
```

### 3. Refresh Token
```bash
curl -X GET http://localhost:8080/auth/refresh \
  -H "Authorization: Bearer {refresh_token}"
```

### 4. Logout
```bash
curl -X GET http://localhost:8080/auth/logout \
  -H "Authorization: Bearer {access_token}"
```

---

## Migration Notes

### Changes from Previous Implementation:

1. **Redis Dependency Removed**
   - Tokens are no longer stored in Redis
   - Validation is purely cryptographic
   - Better performance and scalability

2. **Logout Behavior Changed**
   - Logout no longer deletes tokens from database
   - Tokens remain valid until expiration
   - Client must discard tokens

3. **New Exception Handling**
   - Specific handlers for token expiration
   - Clear error messages for different scenarios
   - Proper HTTP status codes

---

## Optional: Token Blacklist Implementation

If you need immediate token invalidation on logout, implement a blacklist:

```java
// Store only invalidated tokens in Redis
public void logout(User user, String bearer) {
    String token = jwtTokenProvider.extractJwtFromBearerString(bearer);
    String userId = jwtTokenProvider.getUserIdFromToken(token);
    
    // Add to blacklist
    redisTemplate.opsForValue().set(
        "blacklist:" + token, 
        userId, 
        getRemainingExpiration(token), 
        TimeUnit.MILLISECONDS
    );
}

// Check blacklist in validateToken
public boolean validateToken(String token) {
    if (redisTemplate.hasKey("blacklist:" + token)) {
        return false;
    }
    // ... existing validation
}
```

This way, you only store invalidated tokens, not all tokens.

---

## Support

For questions or issues, check:
- JWT.io for token debugging
- Application logs for detailed error traces
- This documentation for implementation guidance

