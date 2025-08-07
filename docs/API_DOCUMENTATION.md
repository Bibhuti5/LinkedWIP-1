# 📚 DevSocial API Documentation

## 🚀 **Interactive API Documentation with Swagger**

DevSocial provides comprehensive, interactive API documentation using **Swagger/OpenAPI 3.0** for all backend services. Each service has its own dedicated Swagger UI where you can explore, test, and understand the APIs.

---

## 🔗 **Swagger UI Access**

### **Development Environment**

| Service | Swagger UI URL | API Docs JSON |
|---------|---------------|---------------|
| 🚪 **API Gateway** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | [/api-docs](http://localhost:8080/api-docs) |
| 🔐 **Auth Service** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [/api-docs](http://localhost:8081/api-docs) |
| 👤 **User Service** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [/api-docs](http://localhost:8082/api-docs) |
| 📝 **Post Service** | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [/api-docs](http://localhost:8083/api-docs) |
| 💬 **Message Service** | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | [/api-docs](http://localhost:8084/api-docs) |
| 🎥 **Media Service** | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) | [/api-docs](http://localhost:8085/api-docs) |

---

## 🔐 **Authentication for API Testing**

### **Step-by-Step Authentication:**

1. **🔓 Start with Auth Service**: Visit [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

2. **📝 Register a Test User**:
   - Use `/api/auth/register` endpoint
   - Example payload:
   ```json
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "SecurePass123!",
     "firstName": "Test",
     "lastName": "User"
   }
   ```

3. **🔑 Login to Get Token**:
   - Use `/api/auth/login` endpoint
   - Example payload:
   ```json
   {
     "usernameOrEmail": "test@example.com",
     "password": "SecurePass123!",
     "rememberMe": true
   }
   ```

4. **📋 Copy the Access Token** from the response:
   ```json
   {
     "success": true,
     "data": {
       "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "tokenType": "Bearer",
       "expiresIn": 86400
     }
   }
   ```

5. **🔒 Authorize in Swagger**:
   - Click the **"Authorize"** button (🔒) in any Swagger UI
   - Enter: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
   - Click **"Authorize"**

6. **✅ Test Protected Endpoints**: Now you can test all authenticated endpoints!

---

## 🔐 **Auth Service API**
**Base URL**: `http://localhost:8081`
**Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### **Key Endpoints**:

#### **Public Endpoints** (No Authentication Required)
```http
POST /api/auth/register       # User registration
POST /api/auth/login          # User login
POST /api/auth/refresh        # Token refresh
GET  /api/auth/check-username # Check username availability
GET  /api/auth/check-email    # Check email availability
```

#### **OAuth2 Endpoints**
```http
GET  /oauth2/authorize/github  # GitHub OAuth login
GET  /oauth2/authorize/google  # Google OAuth login
GET  /oauth2/callback/{provider} # OAuth callback
```

#### **Protected Endpoints** (Requires Authentication)
```http
GET  /api/auth/me             # Get current user info
POST /api/auth/logout         # User logout
POST /api/auth/change-password # Change password
```

---

## 👤 **User Service API**
**Base URL**: `http://localhost:8082`
**Swagger UI**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

### **Key Endpoints**:

#### **Profile Management**
```http
GET  /api/users/profile       # Get current user profile
PUT  /api/users/profile       # Update user profile
GET  /api/users/profile/{id}  # Get user profile by ID
```

#### **Social Features**
```http
POST /api/users/follow/{id}   # Follow a user
POST /api/users/unfollow/{id} # Unfollow a user
GET  /api/users/followers     # Get followers list
GET  /api/users/following     # Get following list
GET  /api/users/suggestions   # Get follow suggestions
```

#### **Discovery & Search**
```http
GET  /api/users/search        # Search users
GET  /api/users/trending      # Get trending profiles
GET  /api/users/location/{loc} # Find users by location
```

#### **GitHub Integration**
```http
POST /api/users/github/sync   # Sync GitHub data
GET  /api/users/github/repos  # Get GitHub repositories
```

---

## 📝 **Post Service API**
**Base URL**: `http://localhost:8083`
**Swagger UI**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

### **Key Endpoints**:

#### **Content Management**
```http
POST /api/posts               # Create new post
GET  /api/posts               # Get posts feed
GET  /api/posts/{id}          # Get specific post
PUT  /api/posts/{id}          # Update post
DELETE /api/posts/{id}        # Delete post
```

#### **Social Interactions**
```http
POST /api/posts/{id}/like     # Like/unlike post
POST /api/posts/{id}/comments # Add comment
GET  /api/posts/{id}/comments # Get comments
POST /api/posts/{id}/share    # Share post
```

#### **Discovery**
```http
GET  /api/posts/trending      # Get trending posts
GET  /api/posts/feed          # Get personalized feed
GET  /api/posts/search        # Search posts
GET  /api/posts/tags/{tag}    # Get posts by tag
```

---

## 💬 **Message Service API**
**Base URL**: `http://localhost:8084`
**Swagger UI**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)

### **Key Endpoints**:

#### **Conversations**
```http
GET  /api/messages/conversations    # Get all conversations
POST /api/messages/conversations    # Create conversation
GET  /api/messages/conversations/{id} # Get conversation details
```

#### **Messaging**
```http
POST /api/messages/send            # Send message
GET  /api/messages/{conversationId} # Get messages
PUT  /api/messages/{id}/read       # Mark message as read
DELETE /api/messages/{id}          # Delete message
```

#### **Real-time Features**
```http
WebSocket: /ws                     # WebSocket connection
STOMP: /app/chat                   # Send message via WebSocket
Subscribe: /topic/messages/{userId} # Receive messages
```

---

## 🎥 **Media Service API**
**Base URL**: `http://localhost:8085`
**Swagger UI**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

### **Key Endpoints**:

#### **File Upload**
```http
POST /api/media/upload        # Upload media file
POST /api/media/upload/batch  # Upload multiple files
```

#### **Media Management**
```http
GET  /api/media/{id}          # Get media info
PUT  /api/media/{id}          # Update media metadata
DELETE /api/media/{id}        # Delete media file
```

#### **Processing**
```http
POST /api/media/{id}/process  # Process media (thumbnails, etc.)
GET  /api/media/{id}/status   # Get processing status
```

---

## 🚪 **API Gateway**
**Base URL**: `http://localhost:8080` (Main Entry Point)
**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### **Unified API Access**:
The API Gateway provides a single entry point for all services:

```http
# All requests go through the gateway
http://localhost:8080/api/auth/**     → Auth Service
http://localhost:8080/api/users/**    → User Service  
http://localhost:8080/api/posts/**    → Post Service
http://localhost:8080/api/messages/** → Message Service
http://localhost:8080/api/media/**    → Media Service
```

### **Gateway Features**:
- **🔒 Authentication**: JWT validation for all requests
- **⚡ Rate Limiting**: API throttling and abuse prevention
- **🔄 Load Balancing**: Distributes requests across service instances
- **🛡️ Circuit Breaker**: Handles service failures gracefully
- **📊 Monitoring**: Request metrics and health checks

---

## 📊 **API Response Format**

All APIs follow a consistent response format:

### **Success Response**:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### **Error Response**:
```json
{
  "success": false,
  "message": "Error description",
  "statusCode": 400,
  "errors": [
    {
      "field": "fieldName",
      "message": "Validation error message"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 🧪 **Testing Workflows**

### **1. User Registration & Authentication**
```bash
# 1. Register user
POST /api/auth/register

# 2. Login to get token
POST /api/auth/login

# 3. Test protected endpoint
GET /api/auth/me (with Bearer token)
```

### **2. Social Features Testing**
```bash
# 1. Create user profile
PUT /api/users/profile

# 2. Search for users
GET /api/users/search?query=john

# 3. Follow a user
POST /api/users/follow/2

# 4. Get followers
GET /api/users/followers
```

### **3. Content Creation & Interaction**
```bash
# 1. Upload media
POST /api/media/upload

# 2. Create post with media
POST /api/posts

# 3. Like the post
POST /api/posts/1/like

# 4. Add comment
POST /api/posts/1/comments
```

### **4. Real-time Messaging**
```bash
# 1. Create conversation
POST /api/messages/conversations

# 2. Send message
POST /api/messages/send

# 3. Connect to WebSocket
WebSocket: ws://localhost:8084/ws
```

---

## 🔧 **Development Tools**

### **Postman Collection**
Import the OpenAPI specs into Postman:
- File → Import → Link → `http://localhost:8081/api-docs`

### **cURL Examples**
```bash
# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@example.com","password":"password"}'

# Get profile (with token)
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### **HTTP Client Files**
Create `.http` files for VS Code REST Client:
```http
### Login
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "test@example.com",
  "password": "password"
}

### Get Profile
GET http://localhost:8082/api/users/profile
Authorization: Bearer {{auth_token}}
```

---

## 📈 **API Monitoring**

### **Health Checks**
All services provide health check endpoints:
```http
GET /actuator/health          # Service health
GET /actuator/info           # Service information
GET /actuator/metrics        # Service metrics
```

### **Swagger Health Dashboard**
Monitor all services from the gateway:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🚀 **Production API Access**

### **Production URLs** (When deployed):
```
🌐 Production API: https://api.devsocial.com
📚 API Docs: https://api.devsocial.com/swagger-ui.html
🔐 Auth: https://api.devsocial.com/api/auth/
👤 Users: https://api.devsocial.com/api/users/
📝 Posts: https://api.devsocial.com/api/posts/
💬 Messages: https://api.devsocial.com/api/messages/
🎥 Media: https://api.devsocial.com/api/media/
```

---

## 🎯 **Quick Start Checklist**

- [ ] **Start Services**: `docker-compose up -d` or individual service startup
- [ ] **Access Swagger**: Visit [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- [ ] **Register User**: Use `/api/auth/register` endpoint
- [ ] **Get Token**: Use `/api/auth/login` endpoint
- [ ] **Authorize**: Click 🔒 and enter `Bearer <token>`
- [ ] **Test APIs**: Explore all protected endpoints
- [ ] **Try WebSocket**: Connect to message service WebSocket
- [ ] **Upload Media**: Test file upload functionality

---

**🎉 The DevSocial API is fully documented and ready for development, testing, and integration!**

For more detailed information, visit the individual Swagger UI pages for each service.