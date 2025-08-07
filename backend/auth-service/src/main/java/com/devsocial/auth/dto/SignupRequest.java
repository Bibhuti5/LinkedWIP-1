package com.devsocial.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests
 * 
 * This DTO captures all the required information for creating a new user account.
 * It includes comprehensive validation to ensure data integrity.
 * 
 * Validation rules:
 * - Username: 3-50 characters, required
 * - Email: Valid email format, required
 * - Password: 6-100 characters, required
 * - First/Last name: Optional, max 50 characters each
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    
    /**
     * Desired username for the new account
     * Must be unique across the platform
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * User's email address
     * Must be unique and will be used for account verification
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    /**
     * User's password
     * Will be encrypted before storage
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    /**
     * Password confirmation
     * Must match the password field
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    /**
     * User's first name (optional)
     */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    /**
     * User's last name (optional)
     */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    /**
     * Checks if password and confirmation password match
     * 
     * @return true if passwords match
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}