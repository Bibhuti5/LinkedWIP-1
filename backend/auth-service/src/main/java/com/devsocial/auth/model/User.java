package com.devsocial.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity representing a user in the authentication system
 * 
 * This entity implements Spring Security's UserDetails interface to integrate
 * seamlessly with Spring Security authentication and authorization mechanisms.
 * 
 * Features:
 * - Basic user information (username, email, password)
 * - Account status management (enabled, locked, expired)
 * - OAuth provider integration
 * - Audit fields (creation and update timestamps)
 * - Password management with BCrypt encoding
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    /**
     * Primary key for the user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Unique username for the user
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * User's email address
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * User's encrypted password
     * Note: This field should never be exposed in API responses
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * User's first name
     */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(length = 50)
    private String firstName;
    
    /**
     * User's last name
     */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(length = 50)
    private String lastName;
    
    /**
     * OAuth provider (e.g., 'github', 'google', 'local' for regular signup)
     */
    @Column(length = 20)
    @Builder.Default
    private String provider = "local";
    
    /**
     * OAuth provider ID (external user ID from OAuth provider)
     */
    @Column(name = "provider_id")
    private String providerId;
    
    /**
     * User's role in the system
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
    
    /**
     * Whether the user account is enabled
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    /**
     * Whether the user account is locked
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;
    
    /**
     * Whether the user account is expired
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;
    
    /**
     * Whether the user credentials are expired
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    
    /**
     * Whether the user's email is verified
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;
    
    /**
     * Email verification token
     */
    @Column(name = "email_verification_token")
    private String emailVerificationToken;
    
    /**
     * Password reset token
     */
    @Column(name = "password_reset_token")
    private String passwordResetToken;
    
    /**
     * Password reset token expiry time
     */
    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;
    
    /**
     * Timestamp when the user was created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Last login timestamp
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    /**
     * Sets timestamps before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * Updates the updatedAt timestamp before updating in database
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // UserDetails interface implementation
    
    /**
     * Returns the authorities granted to the user
     * 
     * @return Collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    /**
     * Returns the password used to authenticate the user
     * 
     * @return The user's password
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    /**
     * Returns the username used to authenticate the user
     * 
     * @return The username
     */
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * Indicates whether the user's account has expired
     * 
     * @return true if the account is non-expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    /**
     * Indicates whether the user is locked or unlocked
     * 
     * @return true if the account is non-locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    /**
     * Indicates whether the user's credentials have expired
     * 
     * @return true if the credentials are non-expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    /**
     * Indicates whether the user is enabled or disabled
     * 
     * @return true if the user is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Gets the user's full name
     * 
     * @return Full name combining first and last name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    /**
     * Checks if the user signed up via OAuth
     * 
     * @return true if user used OAuth provider
     */
    public boolean isOAuthUser() {
        return !provider.equals("local");
    }
    
    /**
     * User roles enumeration
     */
    public enum Role {
        USER,
        ADMIN,
        MODERATOR
    }
}