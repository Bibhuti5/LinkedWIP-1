# DevSocial Platform

A developer-focused social platform with curated features from LinkedIn, GitHub, and YouTube-style project showcases.

## 🏗️ Architecture Overview

This platform is built using a microservices architecture with the following components:

### Backend Services (Java Spring Boot)
- **Auth Service** (Port 8081) - Authentication, JWT, OAuth2
- **User Service** (Port 8082) - Profile management, GitHub integration
- **Post Service** (Port 8083) - Video/project posts with architecture diagrams
- **Message Service** (Port 8084) - Direct messaging between users
- **Media Service** (Port 8085) - File uploads, video processing, S3 integration
- **Gateway Service** (Port 8080) - API Gateway for routing requests

### Frontend Applications
- **React Web App** (Port 3000) - Desktop web application
- **React Native App** - Mobile application for iOS/Android

### Database & Storage
- **PostgreSQL** - Primary database for structured data
- **Redis** - Caching and real-time messaging
- **AWS S3** - Media storage (videos, images, documents)

## 🚀 Getting Started

### Prerequisites

- **Java 17+** - Required for Spring Boot services
- **Node.js 18+** - Required for React applications
- **PostgreSQL 13+** - Database server
- **Redis 6+** - Caching server (optional)
- **Docker & Docker Compose** - For containerized development
- **Maven 3.8+** - Java build tool

### Development Setup

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd dev-social-platform
```

#### 2. Database Setup
```bash
# Create PostgreSQL databases
createdb devsocial_auth
createdb devsocial_user
createdb devsocial_post
createdb devsocial_message

# Or use Docker
docker run -d \
  --name devsocial-postgres \
  -e POSTGRES_DB=devsocial \
  -e POSTGRES_USER=devsocial \
  -e POSTGRES_PASSWORD=devsocial123 \
  -p 5432:5432 \
  postgres:13
```

#### 3. Build and Run Services

##### Using Maven (Individual Services)
```bash
# Build all services
mvn clean install

# Run Authentication Service
cd auth-service
mvn spring-boot:run

# Run in different terminals for other services
cd user-service && mvn spring-boot:run
cd post-service && mvn spring-boot:run
cd message-service && mvn spring-boot:run
cd media-service && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```

##### Using Docker Compose (Recommended)
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### 4. Frontend Setup
```bash
# React Web Application
cd frontend/web
npm install
npm start

# React Native Application
cd frontend/mobile
npm install
npx react-native run-android  # or run-ios
```

## 🔧 Configuration

### Environment Variables

Create `.env` files for each service or set environment variables:

```bash
# Database Configuration
DB_USERNAME=devsocial
DB_PASSWORD=devsocial123
DATABASE_URL=jdbc:postgresql://localhost:5432/devsocial

# JWT Configuration
JWT_SECRET=your-very-long-and-secure-secret-key-here

# OAuth2 Configuration
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Frontend URLs
FRONTEND_URL=http://localhost:3000
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001

# AWS Configuration (for media service)
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
AWS_S3_BUCKET=your-s3-bucket-name
AWS_REGION=us-east-1
```

### OAuth2 Setup

#### GitHub OAuth App
1. Go to GitHub Settings > Developer settings > OAuth Apps
2. Create a new OAuth App with:
   - Homepage URL: `http://localhost:3000`
   - Authorization callback URL: `http://localhost:8081/oauth2/callback/github`
3. Copy Client ID and Client Secret to your environment variables

#### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth2 credentials with:
   - Authorized JavaScript origins: `http://localhost:3000`
   - Authorized redirect URIs: `http://localhost:8081/oauth2/callback/google`
5. Copy Client ID and Client Secret to your environment variables

## 📚 API Documentation

### Authentication Service (Port 8081)

#### Public Endpoints
```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
GET  /api/auth/check-username?username=test
GET  /api/auth/check-email?email=test@example.com
```

#### Protected Endpoints
```http
GET  /api/auth/me
POST /api/auth/logout
```

#### OAuth2 Endpoints
```http
GET  /oauth2/authorize/github
GET  /oauth2/authorize/google
GET  /oauth2/callback/{provider}
```

### Example API Requests

#### User Registration
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepassword",
    "confirmPassword": "securepassword",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### User Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "securepassword"
  }'
```

#### Authenticated Request
```bash
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd auth-service
mvn test

# Run integration tests
mvn verify
```

### Test Coverage
```bash
# Generate test coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## 🔒 Security Features

### Authentication & Authorization
- **JWT Tokens** - Stateless authentication
- **BCrypt Password Hashing** - Secure password storage
- **OAuth2 Integration** - GitHub and Google login
- **Role-Based Access Control** - User, Admin, Moderator roles
- **Token Refresh** - Automatic token renewal

### Security Best Practices
- **CORS Configuration** - Proper cross-origin settings
- **Input Validation** - Bean Validation (JSR-303)
- **SQL Injection Prevention** - JPA/Hibernate parameterized queries
- **XSS Protection** - JSON serialization security
- **Rate Limiting** - API throttling (to be implemented)

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker images
docker-compose build

# Deploy to production
docker-compose -f docker-compose.prod.yml up -d
```

### Kubernetes Deployment
```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services
```

### Environment-Specific Configurations
- **Development** - H2 in-memory database, debug logging
- **Staging** - PostgreSQL, info logging, OAuth2 testing
- **Production** - PostgreSQL cluster, warn logging, SSL, monitoring

## 📊 Monitoring & Observability

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- External service health monitoring

### Metrics & Logging
- **Micrometer** - Application metrics
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization
- **ELK Stack** - Log aggregation and analysis

### Monitoring Endpoints
```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
GET /actuator/prometheus
```

## 🛠️ Development Guidelines

### Code Style
- **Java** - Google Java Style Guide
- **JavaScript/TypeScript** - ESLint + Prettier
- **Documentation** - Comprehensive JavaDoc and JSDoc
- **Git** - Conventional Commits

### Database Migrations
- Use Flyway for database schema migrations
- Version control all schema changes
- Test migrations on staging before production

### API Design
- RESTful principles
- Consistent response formats (ApiResponse wrapper)
- Proper HTTP status codes
- Comprehensive error handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Workflow
1. Create issue for feature/bug
2. Create branch from `develop`
3. Implement changes with tests
4. Create pull request to `develop`
5. Code review and merge
6. Deploy to staging for testing
7. Merge to `main` for production

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Backend Development** - Spring Boot microservices
- **Frontend Development** - React web and mobile apps
- **DevOps** - Docker, Kubernetes, CI/CD
- **Database** - PostgreSQL, Redis optimization
- **Security** - Authentication, authorization, OAuth2

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/)
- [React Native Documentation](https://reactnative.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [GitHub OAuth Documentation](https://docs.github.com/en/developers/apps/building-oauth-apps)

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team
- Check the documentation and FAQ

---

**Built with ❤️ by the DevSocial Team**
