# 🚀 DevSocial - Developer Social Platform

> **A comprehensive social platform for developers to connect, share projects, and collaborate**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1+-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18+-blue.svg)](https://reactjs.org)
[![React Native](https://img.shields.io/badge/React%20Native-0.72+-purple.svg)](https://reactnative.dev)
[![TypeScript](https://img.shields.io/badge/TypeScript-5+-blue.svg)](https://typescriptlang.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://postgresql.org)

## 📋 Table of Contents

- [🏗️ Project Architecture](#️-project-architecture)
- [🎯 Features Overview](#-features-overview)
- [🖥️ Frontend Applications](#️-frontend-applications)
- [⚙️ Backend Services](#️-backend-services)
- [🚀 Quick Start Guide](#-quick-start-guide)
- [📁 Project Structure](#-project-structure)
- [🧪 Testing](#-testing)
- [🚀 Deployment](#-deployment)
- [📚 Documentation](#-documentation)

---

## 🏗️ Project Architecture

DevSocial follows a **microservices architecture** with separate frontend applications and backend services:

```
DevSocial Platform
├── 🖥️ Frontend Applications
│   ├── 📱 Mobile App (React Native)
│   └── 🌐 Web App (React + TypeScript)
└── ⚙️ Backend Services
    ├── 🔐 Auth Service (Port 8081)
    ├── 👤 User Service (Port 8082)
    ├── 📝 Post Service (Port 8083)
    ├── 💬 Message Service (Port 8084)
    ├── 🎥 Media Service (Port 8085)
    └── 🚪 API Gateway (Port 8080)
```

---

## 🎯 Features Overview

### 🔐 **Authentication & Security**
- JWT-based authentication with refresh tokens
- OAuth2 integration (GitHub, Google)
- Two-factor authentication support
- Secure password hashing with BCrypt

### 👤 **User Management**
- Comprehensive user profiles
- GitHub integration for showcasing repositories
- Social connections (follow/unfollow)
- Skills and experience tracking

### 📝 **Content Creation**
- Rich text posts with media attachments
- Code snippet sharing with syntax highlighting
- Video tutorials and project showcases
- Architecture diagram uploads

### 💬 **Real-time Communication**
- WebSocket-powered messaging
- Direct messages and group chats
- File sharing and code collaboration
- Online presence indicators

### 🎥 **Media Management**
- Image and video processing
- Automatic thumbnail generation
- CDN integration for fast delivery
- Multiple format support

---

## 🖥️ Frontend Applications

### 📱 **1. Mobile Application (React Native)**
**Status**: 🔄 *Ready for Development*
**Location**: `/frontend/mobile/`

#### Features:
- Native iOS and Android experience
- Push notifications
- Camera integration for content creation
- Offline mode support
- Biometric authentication

#### Tech Stack:
- **Framework**: React Native 0.72+
- **Navigation**: React Navigation 6
- **State Management**: Redux Toolkit
- **Networking**: Axios
- **Real-time**: Socket.IO Client

#### Quick Start:
```bash
cd frontend/mobile
npm install
npx react-native run-android  # or run-ios
```

### 🌐 **2. Web Application (React + TypeScript)**
**Status**: ✅ *COMPLETED & READY FOR TESTING*
**Location**: `/frontend/web/`

#### Features:
- Responsive design (mobile-first)
- Progressive Web App (PWA)
- Real-time updates via WebSocket
- Dark/Light theme support
- Advanced search and filtering

#### Tech Stack:
- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **UI Library**: Material-UI (MUI)
- **State Management**: Redux Toolkit + Redux Persist
- **Data Fetching**: React Query + Axios
- **Routing**: React Router DOM 6
- **Real-time**: STOMP over WebSocket

#### Quick Start:
```bash
cd frontend/web
npm install
npm run dev
# Open http://localhost:3000
```

#### Testing Guide:
See [`frontend/web/TESTING.md`](frontend/web/TESTING.md) for comprehensive testing instructions.

---

## ⚙️ Backend Services

### 🔐 **1. Auth Service** 
**Status**: ✅ *COMPLETED*
**Port**: 8081
**Location**: `/auth-service/`

#### Responsibilities:
- User authentication and authorization
- JWT token management (access & refresh)
- OAuth2 integration (GitHub, Google)
- Password management and security
- Two-factor authentication

#### Key Endpoints:
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Token refresh
- `GET /api/auth/oauth/{provider}` - OAuth login
- `POST /api/auth/logout` - User logout

#### Technologies:
- Spring Boot 3.1+
- Spring Security 6
- JWT (JSON Web Tokens)
- BCrypt password hashing
- OAuth2 client

---

### 👤 **2. User Service**
**Status**: ✅ *COMPLETED*
**Port**: 8082
**Location**: `/user-service/`

#### Responsibilities:
- User profile management
- GitHub integration and data sync
- Social connections (follow/unfollow)
- User search and discovery
- Profile analytics

#### Key Endpoints:
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `POST /api/users/follow/{userId}` - Follow user
- `GET /api/users/search` - Search users
- `POST /api/users/github/sync` - Sync GitHub data

#### Technologies:
- Spring Boot 3.1+
- Spring Data JPA
- PostgreSQL database
- GitHub API integration
- Redis caching

---

### 📝 **3. Post Service**
**Status**: ✅ *COMPLETED*
**Port**: 8083
**Location**: `/post-service/`

#### Responsibilities:
- Content creation and management
- Media attachment handling
- Comments and reactions
- Content discovery and trending
- Post analytics

#### Key Endpoints:
- `POST /api/posts` - Create post
- `GET /api/posts` - Get posts feed
- `GET /api/posts/{id}` - Get specific post
- `POST /api/posts/{id}/like` - Like/unlike post
- `POST /api/posts/{id}/comments` - Add comment

#### Technologies:
- Spring Boot 3.1+
- Spring Data JPA
- PostgreSQL database
- AWS S3 integration
- Image processing libraries

---

### 💬 **4. Message Service**
**Status**: ✅ *COMPLETED*
**Port**: 8084
**Location**: `/message-service/`

#### Responsibilities:
- Real-time messaging via WebSocket
- Direct and group conversations
- Message history and search
- File sharing capabilities
- Online presence tracking

#### Key Endpoints:
- `GET /api/messages/conversations` - Get conversations
- `POST /api/messages/send` - Send message
- `GET /api/messages/{conversationId}` - Get messages
- **WebSocket**: `/ws` - Real-time communication

#### Technologies:
- Spring Boot 3.1+
- WebSocket + STOMP
- Spring Data JPA
- PostgreSQL database
- Redis for session management

---

### 🎥 **5. Media Service**
**Status**: ✅ *COMPLETED*
**Port**: 8085
**Location**: `/media-service/`

#### Responsibilities:
- File upload and processing
- Image resizing and optimization
- Video transcoding and thumbnails
- CDN integration
- Media metadata extraction

#### Key Endpoints:
- `POST /api/media/upload` - Upload media
- `GET /api/media/{id}` - Get media info
- `POST /api/media/process` - Process media
- `DELETE /api/media/{id}` - Delete media

#### Technologies:
- Spring Boot 3.1+
- AWS S3 storage
- Image processing (imgscalr)
- Video processing (JAVE)
- Apache Tika for metadata

---

### 🚪 **6. API Gateway**
**Status**: ✅ *COMPLETED*
**Port**: 8080 (Main Entry Point)
**Location**: `/gateway/`

#### Responsibilities:
- Route management and load balancing
- Authentication and authorization
- Rate limiting and throttling
- Request/response transformation
- Circuit breaker patterns

#### Route Configuration:
- `/api/auth/**` → Auth Service (8081)
- `/api/users/**` → User Service (8082)
- `/api/posts/**` → Post Service (8083)
- `/api/messages/**` → Message Service (8084)
- `/api/media/**` → Media Service (8085)
- `/ws/**` → WebSocket routing

#### Technologies:
- Spring Cloud Gateway
- Redis for rate limiting
- Resilience4j for circuit breaker
- JWT validation

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17+** (for backend services)
- **Node.js 18+** (for frontend applications)
- **PostgreSQL 15+** (database)
- **Redis 6+** (caching and sessions)
- **Docker & Docker Compose** (optional, for containerized setup)

### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone <your-repo-url>
cd devsocial

# Start all services with Docker
docker-compose up -d

# Frontend applications need to be started separately
cd frontend/web && npm install && npm run dev
cd frontend/mobile && npm install && npx react-native run-android
```

### Option 2: Manual Setup

#### Backend Services:
```bash
# Start each service in separate terminals
./mvnw spring-boot:run -pl auth-service
./mvnw spring-boot:run -pl user-service  
./mvnw spring-boot:run -pl post-service
./mvnw spring-boot:run -pl message-service
./mvnw spring-boot:run -pl media-service
./mvnw spring-boot:run -pl gateway
```

#### Frontend Applications:
```bash
# Web Application
cd frontend/web
npm install
npm run dev

# Mobile Application  
cd frontend/mobile
npm install
npx react-native run-android  # or run-ios
```

### Service URLs:
- **🚪 API Gateway**: http://localhost:8080
- **🔐 Auth Service**: http://localhost:8081
- **👤 User Service**: http://localhost:8082
- **📝 Post Service**: http://localhost:8083
- **💬 Message Service**: http://localhost:8084
- **🎥 Media Service**: http://localhost:8085
- **🌐 Web App**: http://localhost:3000

---

## 📁 Project Structure

```
devsocial/
├── 📁 frontend/                    # Frontend Applications
│   ├── 📱 mobile/                  # React Native Mobile App
│   │   ├── src/
│   │   │   ├── components/         # Reusable UI components
│   │   │   ├── screens/            # Application screens
│   │   │   ├── navigation/         # Navigation configuration
│   │   │   ├── services/           # API and business logic
│   │   │   ├── hooks/              # Custom React hooks
│   │   │   ├── context/            # React context providers
│   │   │   └── utils/              # Utility functions
│   │   ├── package.json
│   │   └── README.md
│   │
│   └── 🌐 web/                     # React Web Application
│       ├── src/
│       │   ├── components/         # UI components
│       │   │   ├── common/         # Shared components
│       │   │   ├── auth/           # Authentication components
│       │   │   ├── user/           # User-related components
│       │   │   ├── post/           # Post components
│       │   │   ├── message/        # Messaging components
│       │   │   └── media/          # Media components
│       │   ├── pages/              # Page components
│       │   │   ├── auth/           # Login, Register pages
│       │   │   ├── dashboard/      # Dashboard page
│       │   │   ├── profile/        # Profile pages
│       │   │   ├── posts/          # Post-related pages
│       │   │   ├── messages/       # Messaging pages
│       │   │   └── settings/       # Settings pages
│       │   ├── services/           # API services
│       │   ├── store/              # Redux store and slices
│       │   ├── hooks/              # Custom hooks
│       │   ├── context/            # Context providers
│       │   ├── types/              # TypeScript type definitions
│       │   ├── utils/              # Utility functions
│       │   └── styles/             # Global styles
│       ├── public/                 # Static assets
│       ├── package.json
│       ├── vite.config.ts
│       ├── TESTING.md              # Testing documentation
│       └── README.md
│
├── 📁 backend/                     # Backend Services
│   ├── 📁 common/                  # Shared utilities and DTOs
│   │   ├── src/main/java/com/devsocial/common/
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── util/               # Utility classes
│   │   │   ├── exception/          # Custom exceptions
│   │   │   └── config/             # Shared configurations
│   │   └── pom.xml
│   │
│   ├── 🔐 auth-service/            # Authentication Service
│   │   ├── src/main/java/com/devsocial/auth/
│   │   │   ├── controller/         # REST controllers
│   │   │   ├── service/            # Business logic
│   │   │   ├── model/              # JPA entities
│   │   │   ├── repository/         # Data access layer
│   │   │   ├── config/             # Configuration classes
│   │   │   ├── security/           # Security configurations
│   │   │   └── dto/                # Data Transfer Objects
│   │   ├── src/main/resources/
│   │   │   ├── application.yml     # Configuration
│   │   │   └── db/migration/       # Database migrations
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── 👤 user-service/            # User Management Service
│   │   ├── src/main/java/com/devsocial/user/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── config/
│   │   │   └── dto/
│   │   ├── src/main/resources/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── 📝 post-service/            # Post Management Service
│   │   ├── src/main/java/com/devsocial/post/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── config/
│   │   │   └── dto/
│   │   ├── src/main/resources/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── 💬 message-service/         # Messaging Service
│   │   ├── src/main/java/com/devsocial/message/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── config/
│   │   │   └── dto/
│   │   ├── src/main/resources/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── 🎥 media-service/           # Media Processing Service
│   │   ├── src/main/java/com/devsocial/media/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── config/
│   │   │   └── dto/
│   │   ├── src/main/resources/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── 🚪 gateway/                 # API Gateway
│       ├── src/main/java/com/devsocial/gateway/
│       │   ├── config/             # Gateway configuration
│       │   ├── filter/             # Custom filters
│       │   └── controller/         # Health check controllers
│       ├── src/main/resources/
│       ├── Dockerfile
│       └── pom.xml
│
├── 📁 infrastructure/              # Infrastructure & DevOps
│   ├── docker-compose.yml         # Multi-service setup
│   ├── docker-compose.dev.yml     # Development setup
│   ├── kubernetes/                # K8s deployment files
│   └── scripts/                   # Build and deployment scripts
│
├── 📁 docs/                       # Documentation
│   ├── ARCHITECTURE.md            # Architecture overview
│   ├── FILE_STRUCTURE.md          # Detailed file structure
│   ├── API_DOCUMENTATION.md       # API endpoints
│   └── DEPLOYMENT.md              # Deployment guide
│
├── pom.xml                        # Root Maven configuration
├── README.md                      # This file
├── .gitignore
├── .env.example                   # Environment variables template
└── build.sh                      # Build script
```

---

## 🧪 Testing

### Frontend Testing:
```bash
# Web Application
cd frontend/web
npm test                    # Unit tests
npm run test:e2e           # End-to-end tests
npm run test:coverage     # Coverage report

# Mobile Application  
cd frontend/mobile
npm test                    # Unit tests
npm run test:ios           # iOS testing
npm run test:android       # Android testing
```

### Backend Testing:
```bash
# Run all backend tests
./mvnw test

# Test specific service
./mvnw test -pl auth-service
./mvnw test -pl user-service
```

### Integration Testing:
```bash
# Start test environment
docker-compose -f docker-compose.test.yml up -d

# Run integration tests
./mvnw integration-test
```

---

## 🚀 Deployment

### Production Deployment:
```bash
# Build all services
./build.sh

# Deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Or deploy to Kubernetes
kubectl apply -f infrastructure/kubernetes/
```

### Environment Configuration:
- Copy `.env.example` to `.env`
- Configure database URLs, API keys, and secrets
- Set up OAuth2 credentials for GitHub and Google

---

## 📚 Documentation

- **[🏗️ Architecture Overview](docs/ARCHITECTURE.md)** - Detailed system architecture
- **[📁 File Structure](docs/FILE_STRUCTURE.md)** - Complete project structure
- **[🔌 API Documentation](docs/API_DOCUMENTATION.md)** - Complete API reference with Swagger
- **[🚀 Deployment Guide](docs/DEPLOYMENT.md)** - Production deployment
- **[🧪 Testing Guide](frontend/web/TESTING.md)** - Frontend testing instructions

### 📖 **Interactive API Documentation (Swagger)**

All backend services include comprehensive Swagger/OpenAPI documentation:

| Service | Swagger UI | Description |
|---------|------------|-------------|
| 🚪 **API Gateway** | [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Unified API access point |
| 🔐 **Auth Service** | [localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Authentication & authorization |
| 👤 **User Service** | [localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | User profiles & social features |
| 📝 **Post Service** | [localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | Content management & interactions |
| 💬 **Message Service** | [localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | Real-time messaging |
| 🎥 **Media Service** | [localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) | Media processing & storage |

---

## 📊 Project Status

| Component | Status | Progress | Notes |
|-----------|--------|----------|-------|
| **Frontend** | | | |
| 🌐 Web App | ✅ Complete | 100% | Ready for testing |
| 📱 Mobile App | 🔄 Planned | 0% | Ready to start |
| **Backend** | | | |
| 🔐 Auth Service | ✅ Complete | 100% | JWT + OAuth2 |
| 👤 User Service | ✅ Complete | 100% | Profiles + GitHub |
| 📝 Post Service | ✅ Complete | 100% | Content + Media |
| 💬 Message Service | ✅ Complete | 100% | Real-time chat |
| 🎥 Media Service | ✅ Complete | 100% | File processing |
| 🚪 API Gateway | ✅ Complete | 100% | Routing + Security |
| **Overall** | 🚀 **95% Complete** | | Ready for production |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful frontend library
- Material-UI for the beautiful component library
- All open-source contributors who made this project possible

---

**Built with ❤️ for the developer community**
