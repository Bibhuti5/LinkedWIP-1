package com.devsocial.auth.config;

/**
 * Interface for OAuth2 user information from different providers
 * 
 * This interface provides a common abstraction for user information
 * retrieved from various OAuth2 providers (GitHub, Google, etc.).
 * Each provider returns user data in different formats, and this
 * interface standardizes the access to common user attributes.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
public interface OAuth2UserInfo {
    
    /**
     * Gets the unique user ID from the OAuth2 provider
     * 
     * @return Provider-specific user ID
     */
    String getId();
    
    /**
     * Gets the user's display name
     * 
     * @return User's full name or display name
     */
    String getName();
    
    /**
     * Gets the user's email address
     * 
     * @return User's email address
     */
    String getEmail();
    
    /**
     * Gets the user's first name
     * 
     * @return User's first name (may be null)
     */
    String getFirstName();
    
    /**
     * Gets the user's last name
     * 
     * @return User's last name (may be null)
     */
    String getLastName();
    
    /**
     * Gets the user's profile picture URL
     * 
     * @return URL to user's profile picture (may be null)
     */
    String getImageUrl();
}