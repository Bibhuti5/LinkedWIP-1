# 🚀 DevSocial Swagger Documentation - Quick Setup

## ⚡ **Quick Start - Access API Documentation**

### **Option 1: Docker Compose (Recommended)**
```bash
# Start all services with Swagger enabled
docker-compose up -d

# Wait 30 seconds for services to start, then access:
# 🚪 API Gateway: http://localhost:8080/swagger-ui.html
# 🔐 Auth Service: http://localhost:8081/swagger-ui.html
# 👤 User Service: http://localhost:8082/swagger-ui.html
# 📝 Post Service: http://localhost:8083/swagger-ui.html
# 💬 Message Service: http://localhost:8084/swagger-ui.html
# 🎥 Media Service: http://localhost:8085/swagger-ui.html
```

### **Option 2: Individual Services**
```bash
# Start each service in separate terminals
./mvnw spring-boot:run -pl backend/auth-service
./mvnw spring-boot:run -pl backend/user-service
./mvnw spring-boot:run -pl backend/post-service
./mvnw spring-boot:run -pl backend/message-service
./mvnw spring-boot:run -pl backend/media-service
./mvnw spring-boot:run -pl backend/gateway
```

## 🔐 **Test Authentication Flow**

1. **Open Auth Service Swagger**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

2. **Register a test user** using `/api/auth/register`:
   ```json
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "SecurePass123!",
     "firstName": "Test",
     "lastName": "User"
   }
   ```

3. **Login** using `/api/auth/login`:
   ```json
   {
     "usernameOrEmail": "test@example.com",
     "password": "SecurePass123!",
     "rememberMe": true
   }
   ```

4. **Copy the access token** from the response

5. **Authorize in any Swagger UI**:
   - Click the 🔒 **"Authorize"** button
   - Enter: `Bearer <your-access-token>`
   - Click **"Authorize"**

6. **Test protected endpoints** in any service!

## 📋 **Service-Specific Features**

### 🔐 **Auth Service** - [localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- User registration and login
- JWT token management
- OAuth2 integration (GitHub, Google)
- Password reset functionality

### 👤 **User Service** - [localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- User profile management
- Social features (follow/unfollow)
- GitHub integration and sync
- User search and discovery

### 📝 **Post Service** - [localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- Content creation and management
- Social interactions (likes, comments)
- Media attachments
- Trending and discovery

### 💬 **Message Service** - [localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- Real-time messaging
- Conversation management
- WebSocket integration
- Message history

### 🎥 **Media Service** - [localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- File upload and processing
- Image and video handling
- Thumbnail generation
- Media metadata

### 🚪 **API Gateway** - [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Unified API access
- All services in one place
- Load balancing and routing
- Rate limiting

## 🔧 **Development Tips**

### **Postman Integration**
```bash
# Import OpenAPI specs directly into Postman
# File → Import → Link → http://localhost:8081/api-docs
```

### **VS Code REST Client**
Create `.http` files:
```http
### Login
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "test@example.com",
  "password": "SecurePass123!"
}

### Get Profile (use token from login)
GET http://localhost:8082/api/users/profile
Authorization: Bearer {{auth_token}}
```

### **cURL Examples**
```bash
# Register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"SecurePass123!","firstName":"Test","lastName":"User"}'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@example.com","password":"SecurePass123!"}'

# Use token for authenticated requests
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🎯 **Quick Testing Checklist**

- [ ] Start services (Docker or individual)
- [ ] Access Auth Service Swagger UI
- [ ] Register a test user
- [ ] Login to get JWT token
- [ ] Authorize in Swagger UI with Bearer token
- [ ] Test user profile endpoints
- [ ] Try creating a post
- [ ] Test social features (follow/unfollow)
- [ ] Upload media files
- [ ] Send messages between users

## 🐛 **Troubleshooting**

### **Service Not Starting**
```bash
# Check if port is already in use
netstat -tulpn | grep :8081

# Kill process if needed
sudo kill -9 $(lsof -t -i:8081)

# Check logs
docker-compose logs auth-service
```

### **Swagger UI Not Loading**
```bash
# Verify service is running
curl http://localhost:8081/actuator/health

# Check if Swagger endpoint is available
curl http://localhost:8081/api-docs
```

### **Authentication Issues**
```bash
# Verify JWT token format
echo "YOUR_TOKEN" | base64 -d

# Check token expiration
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

**🎉 Your DevSocial API documentation is now ready for development and testing!**

For detailed API reference, visit: [docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)