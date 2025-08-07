package com.devsocial.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application class for API Gateway
 * 
 * This gateway provides:
 * - Request routing to microservices
 * - Load balancing and failover
 * - Rate limiting and throttling
 * - Centralized authentication
 * - Request/response transformation
 * - Circuit breaker patterns
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.devsocial.gateway",  // Gateway components
    "com.devsocial.common"    // Common shared components
})
public class GatewayApplication {

    /**
     * Main method to start the API Gateway application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * Define routes for microservices
     * 
     * @param builder Route locator builder
     * @return Route locator with all service routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Auth Service Routes
            .route("auth-service", r -> r.path("/api/auth/**")
                .uri("http://localhost:8081"))
            
            // User Service Routes
            .route("user-service", r -> r.path("/api/users/**")
                .uri("http://localhost:8082"))
            
            // Post Service Routes
            .route("post-service", r -> r.path("/api/posts/**")
                .uri("http://localhost:8083"))
            
            // Message Service Routes
            .route("message-service", r -> r.path("/api/messages/**")
                .uri("http://localhost:8084"))
            
            // Media Service Routes
            .route("media-service", r -> r.path("/api/media/**")
                .uri("http://localhost:8085"))
            
            // WebSocket Routes (Message Service)
            .route("websocket-service", r -> r.path("/ws/**")
                .uri("http://localhost:8084"))
            
            .build();
    }
}