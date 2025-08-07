package com.devsocial.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application class for User Service
 * 
 * This class bootstraps the User Service with all necessary configurations:
 * - JPA repositories for data access
 * - Entity scanning for domain models
 * - Transaction management for data consistency
 * - Caching for performance optimization
 * - Async processing for non-blocking operations
 * 
 * The service handles user profile management, social connections,
 * and GitHub integration functionality.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.user",     // User service components
    "com.devsocial.common"    // Common shared components
})
@EnableJpaRepositories(basePackages = "com.devsocial.user.repository")
@EntityScan(basePackages = "com.devsocial.user.model")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class UserServiceApplication {

    /**
     * Main method to start the User Service application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}