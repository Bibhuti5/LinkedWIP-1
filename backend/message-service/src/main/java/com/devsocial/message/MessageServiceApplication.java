package com.devsocial.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application class for Message Service
 * 
 * This service handles real-time messaging including:
 * - WebSocket-based real-time communication
 * - Direct messages between users
 * - Group conversations and channels
 * - Message delivery status tracking
 * - Typing indicators and presence
 * - Message reactions and threading
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.message",  // Message service components
    "com.devsocial.common"    // Common shared components
})
@EnableJpaRepositories(basePackages = "com.devsocial.message.repository")
@EntityScan(basePackages = "com.devsocial.message.model")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class MessageServiceApplication {

    /**
     * Main method to start the Message Service application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }
}