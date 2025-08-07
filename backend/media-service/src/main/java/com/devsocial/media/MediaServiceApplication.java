package com.devsocial.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application class for Media Service
 * 
 * This service handles media processing including:
 * - File upload and storage (S3 integration)
 * - Image processing and optimization
 * - Video transcoding and compression
 * - Thumbnail generation
 * - CDN distribution
 * - Metadata extraction
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.media",    // Media service components
    "com.devsocial.common"    // Common shared components
})
@EnableJpaRepositories(basePackages = "com.devsocial.media.repository")
@EntityScan(basePackages = "com.devsocial.media.model")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class MediaServiceApplication {

    /**
     * Main method to start the Media Service application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MediaServiceApplication.class, args);
    }
}