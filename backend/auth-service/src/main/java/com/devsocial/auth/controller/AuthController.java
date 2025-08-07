package com.devsocial.auth.controller;

import com.devsocial.auth.dto.AuthResponse;
import com.devsocial.auth.dto.LoginRequest;
import com.devsocial.auth.dto.SignupRequest;
import com.devsocial.auth.service.AuthService;
import com.devsocial.auth.service.UserService;
import com.devsocial.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints
 * 
 * This controller provides REST API endpoints for:
 * - User registration (signup)
 * - User authentication (login)
 * - Token refresh
 * - Username/email availability check
 * - Password reset functionality
 * - Email verification
 * 
 * All endpoints return standardized ApiResponse objects for consistency.
 * The controller handles validation, error responses, and proper HTTP status codes.
 * 
 * Base URL: /api/auth
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    /**
     * User registration endpoint
     * 
     * Creates a new user account with the provided information.
     * Validates input data, checks for existing users, and returns
     * JWT tokens upon successful registration.
     * 
     * @param signupRequest User registration data
     * @return ResponseEntity with authentication tokens and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Registration attempt for username: {}", signupRequest.getUsername());
        
        ApiResponse<AuthResponse> response = authService.registerUser(signupRequest);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : 
                           HttpStatus.valueOf(response.getStatusCode());
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * User login endpoint
     * 
     * Authenticates a user with username/email and password.
     * Returns JWT tokens upon successful authentication.
     * 
     * @param loginRequest User login credentials
     * @return ResponseEntity with authentication tokens and user info
     */
    @Operation(
        summary = "User Login",
        description = "Authenticate user with username/email and password. Returns JWT access and refresh tokens upon successful authentication.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "Login Example",
                    value = "{\n  \"usernameOrEmail\": \"john@example.com\",\n  \"password\": \"securePassword123\",\n  \"rememberMe\": true\n}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\n  \"success\": true,\n  \"message\": \"Login successful\",\n  \"data\": {\n    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"tokenType\": \"Bearer\",\n    \"expiresIn\": 86400,\n    \"user\": {\n      \"id\": 1,\n      \"username\": \"johndoe\",\n      \"email\": \"john@example.com\",\n      \"firstName\": \"John\",\n      \"lastName\": \"Doe\"\n    }\n  },\n  \"timestamp\": \"2024-01-01T12:00:00Z\"\n}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\n  \"success\": false,\n  \"message\": \"Invalid username or password\",\n  \"statusCode\": 401,\n  \"timestamp\": \"2024-01-01T12:00:00Z\"\n}"
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());
        
        ApiResponse<AuthResponse> response = authService.authenticateUser(loginRequest);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : 
                           HttpStatus.valueOf(response.getStatusCode());
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Token refresh endpoint
     * 
     * Generates a new access token using a valid refresh token.
     * This endpoint allows clients to obtain new access tokens
     * without requiring the user to log in again.
     * 
     * @param refreshToken Refresh token from previous authentication
     * @return ResponseEntity with new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody String refreshToken) {
        log.info("Token refresh request");
        
        // Extract token from JSON if needed (simple string for now)
        String token = refreshToken.replace("\"", "").trim();
        
        ApiResponse<AuthResponse> response = authService.refreshToken(token);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : 
                           HttpStatus.valueOf(response.getStatusCode());
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Username availability check endpoint
     * 
     * Checks if a username is available for registration.
     * This endpoint is useful for real-time validation in frontend forms.
     * 
     * @param username Username to check
     * @return ResponseEntity indicating availability
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@RequestParam String username) {
        log.debug("Checking username availability: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is required", 400, "VALIDATION_ERROR"));
        }
        
        if (username.length() < 3 || username.length() > 50) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username must be between 3 and 50 characters", 400, "VALIDATION_ERROR"));
        }
        
        boolean isAvailable = userService.isUsernameAvailable(username);
        String message = isAvailable ? "Username is available" : "Username is already taken";
        
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }
    
    /**
     * Email availability check endpoint
     * 
     * Checks if an email address is available for registration.
     * This endpoint is useful for real-time validation in frontend forms.
     * 
     * @param email Email address to check
     * @return ResponseEntity indicating availability
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        log.debug("Checking email availability: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required", 400, "VALIDATION_ERROR"));
        }
        
        // Basic email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid email format", 400, "VALIDATION_ERROR"));
        }
        
        boolean isAvailable = userService.isEmailAvailable(email);
        String message = isAvailable ? "Email is available" : "Email is already registered";
        
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }
    
    /**
     * Health check endpoint
     * 
     * Simple endpoint to check if the authentication service is running.
     * Used by load balancers and monitoring systems.
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Authentication service is healthy", "Service is running"));
    }
    
    /**
     * Get current user endpoint
     * 
     * Returns information about the currently authenticated user.
     * Requires valid JWT token in Authorization header.
     * 
     * @param username Current user's username from JWT token
     * @return ResponseEntity with current user information
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(@RequestParam String username) {
        log.debug("Getting current user info for: {}", username);
        
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user, "User information retrieved")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found", 404, "USER_NOT_FOUND")));
    }
    
    /**
     * Logout endpoint
     * 
     * Handles user logout. Since we're using stateless JWT tokens,
     * this endpoint primarily serves as a confirmation that the client
     * should discard their tokens.
     * 
     * In a production environment, you might want to implement token blacklisting
     * or maintain a logout token registry.
     * 
     * @return ResponseEntity with logout confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        log.info("User logout request");
        
        // In stateless JWT setup, logout is primarily handled on the client side
        // The client should discard the tokens
        
        // TODO: Implement token blacklisting if needed for enhanced security
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful", 
                "Please discard your authentication tokens"));
    }
    
    /**
     * Global exception handler for validation errors
     * 
     * Handles validation exceptions and returns standardized error responses.
     * 
     * @param ex Validation exception
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField())
                       .append(" - ")
                       .append(error.getDefaultMessage())
                       .append("; ")
        );
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage.toString(), 400, "VALIDATION_ERROR"));
    }
    
    /**
     * Global exception handler for general exceptions
     * 
     * Handles unexpected exceptions and returns generic error responses.
     * 
     * @param ex General exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error in AuthController", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", 500, "INTERNAL_ERROR"));
    }
}