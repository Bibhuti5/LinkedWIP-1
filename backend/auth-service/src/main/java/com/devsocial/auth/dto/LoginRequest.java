package com.devsocial.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login requests
 * 
 * This DTO captures the user credentials required for authentication.
 * It supports both username/email and password-based login.
 * 
 * Validation:
 * - Username/email is required and cannot be blank
 * - Password is required and cannot be blank
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    /**
     * User's username or email address
     * This field accepts both username and email for flexible login
     */
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;
    
    /**
     * User's password
     */
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * Optional field to remember the user's login session
     * If true, the JWT token will have extended expiration
     */
    @Builder.Default
    private boolean rememberMe = false;
}