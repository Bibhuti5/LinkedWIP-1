package com.devsocial.auth.repository;

import com.devsocial.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for User entity database operations
 * 
 * This repository provides methods for user-related database operations including:
 * - Basic CRUD operations (inherited from JpaRepository)
 * - Custom finder methods for authentication
 * - User management operations
 * - OAuth user handling
 * 
 * Spring Data JPA automatically implements these methods based on method naming conventions
 * and custom JPQL queries where specified.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their username
     * 
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by their email address
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by either username or email
     * This method is useful for login where users can use either username or email
     * 
     * @param username The username to search for
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    
    /**
     * Finds a user by OAuth provider and provider ID
     * Used for OAuth authentication to link external accounts
     * 
     * @param provider The OAuth provider (e.g., "github", "google")
     * @param providerId The external user ID from the provider
     * @return Optional containing the user if found
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    
    /**
     * Finds a user by email verification token
     * Used during email verification process
     * 
     * @param token The email verification token
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailVerificationToken(String token);
    
    /**
     * Finds a user by password reset token
     * Used during password reset process
     * 
     * @param token The password reset token
     * @return Optional containing the user if found
     */
    Optional<User> findByPasswordResetToken(String token);
    
    /**
     * Checks if a username already exists in the database
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if an email already exists in the database
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a user exists with the given OAuth provider and provider ID
     * 
     * @param provider The OAuth provider
     * @param providerId The external user ID
     * @return true if user exists, false otherwise
     */
    boolean existsByProviderAndProviderId(String provider, String providerId);
    
    /**
     * Updates the user's last login timestamp
     * 
     * @param userId The user ID
     * @param lastLoginAt The last login timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") Long userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
    /**
     * Updates the user's email verification status
     * 
     * @param userId The user ID
     * @param emailVerified The email verification status
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = :emailVerified, u.emailVerificationToken = null WHERE u.id = :userId")
    void updateEmailVerificationStatus(@Param("userId") Long userId, @Param("emailVerified") Boolean emailVerified);
    
    /**
     * Updates the user's password and clears the reset token
     * 
     * @param userId The user ID
     * @param newPassword The new encrypted password
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword, u.passwordResetToken = null, u.passwordResetTokenExpiry = null WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
    
    /**
     * Sets the password reset token and expiry for a user
     * 
     * @param userId The user ID
     * @param resetToken The password reset token
     * @param expiry The token expiry time
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = :resetToken, u.passwordResetTokenExpiry = :expiry WHERE u.id = :userId")
    void setPasswordResetToken(@Param("userId") Long userId, @Param("resetToken") String resetToken, @Param("expiry") LocalDateTime expiry);
    
    /**
     * Finds all users with expired password reset tokens
     * Used for cleanup operations
     * 
     * @param now Current timestamp
     * @return List of users with expired tokens
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetTokenExpiry < :now AND u.passwordResetToken IS NOT NULL")
    Optional<User> findUsersWithExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
    
    /**
     * Counts the total number of active users
     * 
     * @return Number of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUsers();
    
    /**
     * Counts the number of users registered today
     * 
     * @param startOfDay Start of the current day
     * @return Number of users registered today
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startOfDay")
    long countUsersRegisteredToday(@Param("startOfDay") LocalDateTime startOfDay);
}