package com.devsocial.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application class for Post Service
 * 
 * This service handles content management including:
 * - Video/project showcases and posts
 * - Media file handling and processing
 * - Comments and social interactions
 * - Content discovery and search
 * - Tagging and categorization
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.post",     // Post service components
    "com.devsocial.common"    // Common shared components
})
@EnableJpaRepositories(basePackages = "com.devsocial.post.repository")
@EntityScan(basePackages = "com.devsocial.post.model")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class PostServiceApplication {

    /**
     * Main method to start the Post Service application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }
}