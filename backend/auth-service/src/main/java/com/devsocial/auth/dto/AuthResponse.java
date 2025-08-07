package com.devsocial.auth.dto;

import com.devsocial.common.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for authentication responses
 * 
 * This DTO contains all the information returned after successful authentication,
 * including JWT tokens and user details. It's used for both login and signup responses.
 * 
 * Features:
 * - Access token for API authentication
 * - Refresh token for token renewal
 * - Token expiration information
 * - User profile information
 * - Authentication metadata
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    /**
     * JWT access token for API authentication
     * This token should be included in the Authorization header for API calls
     */
    private String accessToken;
    
    /**
     * JWT refresh token for obtaining new access tokens
     * This token has a longer expiration time than access token
     */
    private String refreshToken;
    
    /**
     * Type of token (usually "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * Access token expiration time in seconds
     */
    private Long expiresIn;
    
    /**
     * Refresh token expiration time in seconds
     */
    private Long refreshExpiresIn;
    
    /**
     * Timestamp when the tokens were issued
     */
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();
    
    /**
     * User profile information
     * This contains basic user details without sensitive information
     */
    private UserDto user;
    
    /**
     * Whether this is the user's first login
     * Useful for showing onboarding flows
     */
    @Builder.Default
    private Boolean isFirstLogin = false;
    
    /**
     * Authentication provider used (local, github, etc.)
     */
    private String provider;
    
    /**
     * Additional message for the client
     */
    private String message;
    
    /**
     * Creates a successful authentication response with tokens and user info
     * 
     * @param accessToken JWT access token
     * @param refreshToken JWT refresh token
     * @param user User profile information
     * @param expiresIn Access token expiration in seconds
     * @param refreshExpiresIn Refresh token expiration in seconds
     * @return AuthResponse with all authentication data
     */
    public static AuthResponse success(String accessToken, String refreshToken, UserDto user, 
                                     Long expiresIn, Long refreshExpiresIn) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshExpiresIn)
                .provider("local")
                .message("Authentication successful")
                .build();
    }
    
    /**
     * Creates a successful OAuth authentication response
     * 
     * @param accessToken JWT access token
     * @param refreshToken JWT refresh token
     * @param user User profile information
     * @param expiresIn Access token expiration in seconds
     * @param refreshExpiresIn Refresh token expiration in seconds
     * @param provider OAuth provider name
     * @param isFirstLogin Whether this is first login
     * @return AuthResponse with OAuth authentication data
     */
    public static AuthResponse oauthSuccess(String accessToken, String refreshToken, UserDto user,
                                          Long expiresIn, Long refreshExpiresIn, String provider,
                                          Boolean isFirstLogin) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshExpiresIn)
                .provider(provider)
                .isFirstLogin(isFirstLogin)
                .message("OAuth authentication successful")
                .build();
    }
}