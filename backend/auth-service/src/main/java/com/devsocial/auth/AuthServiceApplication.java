package com.devsocial.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for the Authentication Service
 * 
 * This class bootstraps the authentication service with the following features:
 * - JWT-based authentication and authorization
 * - OAuth2 integration (GitHub, Google, etc.)
 * - User registration and login
 * - Password reset and email verification
 * - RESTful API endpoints
 * - PostgreSQL database integration
 * - Spring Security configuration
 * 
 * The service is designed to be:
 * - Stateless (using JWT tokens)
 * - Scalable (can run multiple instances)
 * - Secure (BCrypt password hashing, JWT signing)
 * - Well-documented (comprehensive API documentation)
 * 
 * Configuration:
 * - Database: PostgreSQL (configurable via application.yml)
 * - Security: Spring Security with JWT
 * - OAuth2: GitHub and Google providers
 * - Logging: SLF4J with Logback
 * - Validation: Bean Validation (JSR-303)
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.auth",      // Auth service packages
    "com.devsocial.common"     // Common shared packages
})
@EnableJpaRepositories(basePackages = "com.devsocial.auth.repository")
@EntityScan(basePackages = "com.devsocial.auth.model")
@EnableTransactionManagement
public class AuthServiceApplication {
    
    /**
     * Main method to start the Authentication Service
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}