package com.devsocial.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Common Swagger/OpenAPI Configuration for DevSocial Services
 * 
 * This configuration provides a base setup for all microservices
 * with common security schemes, contact information, and styling.
 * 
 * @author DevSocial Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:DevSocial Service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${swagger.api.version:1.0.0}")
    private String apiVersion;

    @Value("${swagger.api.description:DevSocial Platform API}")
    private String apiDescription;

    /**
     * Creates the base OpenAPI configuration for all services
     * 
     * @return OpenAPI configuration with common settings
     */
    @Bean
    public OpenAPI createOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .addSecurityItem(createSecurityRequirement())
                .schemaRequirement("Bearer Authentication", createSecurityScheme());
    }

    /**
     * Creates API information including title, description, version, and contact details
     * 
     * @return Info object with API metadata
     */
    private Info createApiInfo() {
        return new Info()
                .title(applicationName + " API")
                .description(apiDescription + "\n\n" +
                        "**DevSocial Platform** - A comprehensive social platform for developers\n\n" +
                        "### Features:\n" +
                        "- 🔐 **Authentication & Authorization** - JWT-based security\n" +
                        "- 👤 **User Management** - Profiles, social connections, GitHub integration\n" +
                        "- 📝 **Content Creation** - Posts, media, comments, reactions\n" +
                        "- 💬 **Real-time Messaging** - WebSocket-powered chat\n" +
                        "- 🎥 **Media Processing** - Image/video upload and processing\n" +
                        "- 🚪 **API Gateway** - Centralized routing and load balancing\n\n" +
                        "### Architecture:\n" +
                        "Built using **Spring Boot 3.1+** microservices architecture with:\n" +
                        "- Spring Security 6 for authentication\n" +
                        "- PostgreSQL for data persistence\n" +
                        "- Redis for caching and sessions\n" +
                        "- AWS S3 for media storage\n" +
                        "- Docker for containerization")
                .version(apiVersion)
                .contact(createContact())
                .license(createLicense());
    }

    /**
     * Creates contact information for the API
     * 
     * @return Contact object with team details
     */
    private Contact createContact() {
        return new Contact()
                .name("DevSocial Development Team")
                .email("dev@devsocial.com")
                .url("https://github.com/devsocial/platform");
    }

    /**
     * Creates license information for the API
     * 
     * @return License object with license details
     */
    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Creates server configurations for different environments
     * 
     * @return List of server configurations
     */
    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local Development Server"),
                new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway (Recommended)"),
                new Server()
                        .url("https://api.devsocial.com")
                        .description("Production Server")
        );
    }

    /**
     * Creates security requirement for JWT authentication
     * 
     * @return SecurityRequirement for Bearer token
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }

    /**
     * Creates security scheme for JWT Bearer token authentication
     * 
     * @return SecurityScheme configuration for JWT
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("**JWT Authentication**\n\n" +
                        "To authenticate:\n" +
                        "1. **Register/Login** via `/api/auth/register` or `/api/auth/login`\n" +
                        "2. **Copy the access token** from the response\n" +
                        "3. **Click 'Authorize'** button above\n" +
                        "4. **Enter**: `Bearer <your-token>`\n" +
                        "5. **Click 'Authorize'** to apply\n\n" +
                        "**Example:**\n" +
                        "```\n" +
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n" +
                        "```\n\n" +
                        "**Token expires in 24 hours**. Use `/api/auth/refresh` to get a new token.");
    }
}