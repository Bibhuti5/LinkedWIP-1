package com.devsocial.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient used for external API calls
 * 
 * This configuration sets up WebClient for making HTTP requests to external APIs,
 * particularly GitHub API for user data synchronization.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Configuration
public class WebClientConfig {
    
    /**
     * Creates a WebClient bean for making HTTP requests
     * 
     * @return Configured WebClient instance
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();
    }
}