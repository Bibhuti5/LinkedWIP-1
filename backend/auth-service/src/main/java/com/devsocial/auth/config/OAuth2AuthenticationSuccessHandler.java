package com.devsocial.auth.config;

import com.devsocial.auth.model.User;
import com.devsocial.auth.repository.UserRepository;
import com.devsocial.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * OAuth2 Authentication Success Handler
 * 
 * This handler is invoked when OAuth2 authentication is successful.
 * It performs the following operations:
 * - Extracts user information from OAuth2 provider
 * - Creates or updates user account in the database
 * - Generates JWT tokens for the authenticated user
 * - Redirects to the frontend with authentication tokens
 * 
 * Supported OAuth2 providers:
 * - GitHub
 * - Google (can be extended)
 * - Other providers (can be extended)
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    /**
     * Frontend URL for redirecting after successful authentication
     */
    @Value("${app.oauth2.authorizedRedirectUri:http://localhost:3000/auth/oauth2/redirect}")
    private String authorizedRedirectUri;
    
    /**
     * JWT token expiration time in milliseconds
     */
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;
    
    /**
     * Refresh token expiration time in milliseconds
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;
    
    /**
     * Handles successful OAuth2 authentication
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param authentication Spring Security authentication object containing OAuth2 user info
     * @throws IOException if redirect fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String registrationId = getRegistrationId(request);
            
            log.info("Processing OAuth2 authentication success for provider: {}", registrationId);
            
            // Process user based on OAuth2 provider
            User user = processOAuth2User(oAuth2User, registrationId);
            
            // Generate JWT tokens
            String accessToken = jwtUtil.generateTokenFromUsername(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // Update last login timestamp
            userRepository.updateLastLoginAt(user.getId(), LocalDateTime.now());
            
            // Build redirect URL with tokens
            String redirectUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .queryParam("expiresIn", jwtExpiration / 1000)
                    .build().toUriString();
            
            log.info("Redirecting OAuth2 user {} to: {}", user.getUsername(), authorizedRedirectUri);
            
            // Redirect to frontend with tokens
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            log.error("Error during OAuth2 authentication success handling", e);
            
            // Redirect to error page
            String errorUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("error", "oauth2_processing_error")
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
    
    /**
     * Processes OAuth2 user information and creates/updates user account
     * 
     * @param oAuth2User OAuth2 user information from provider
     * @param registrationId OAuth2 provider registration ID
     * @return User entity (created or existing)
     */
    private User processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }
        
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(registrationId, userInfo.getId());
        
        if (existingUser.isPresent()) {
            // Update existing OAuth2 user
            return updateExistingUser(existingUser.get(), userInfo);
        } else {
            // Check if user exists with same email but different provider
            Optional<User> emailUser = userRepository.findByEmail(userInfo.getEmail());
            if (emailUser.isPresent()) {
                // Link OAuth2 account to existing user
                return linkOAuth2Account(emailUser.get(), registrationId, userInfo);
            } else {
                // Create new OAuth2 user
                return createNewUser(registrationId, userInfo);
            }
        }
    }
    
    /**
     * Updates existing OAuth2 user with latest information from provider
     * 
     * @param existingUser Existing user entity
     * @param userInfo OAuth2 user information
     * @return Updated user entity
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo) {
        // Update user information with latest from OAuth2 provider
        existingUser.setEmail(userInfo.getEmail());
        existingUser.setFirstName(userInfo.getFirstName());
        existingUser.setLastName(userInfo.getLastName());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Links OAuth2 account to existing user account
     * 
     * @param existingUser Existing user with same email
     * @param provider OAuth2 provider name
     * @param userInfo OAuth2 user information
     * @return Updated user entity
     */
    private User linkOAuth2Account(User existingUser, String provider, OAuth2UserInfo userInfo) {
        existingUser.setProvider(provider);
        existingUser.setProviderId(userInfo.getId());
        
        // Update name information if not already set
        if (existingUser.getFirstName() == null && userInfo.getFirstName() != null) {
            existingUser.setFirstName(userInfo.getFirstName());
        }
        if (existingUser.getLastName() == null && userInfo.getLastName() != null) {
            existingUser.setLastName(userInfo.getLastName());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Creates a new user account from OAuth2 information
     * 
     * @param provider OAuth2 provider name
     * @param userInfo OAuth2 user information
     * @return New user entity
     */
    private User createNewUser(String provider, OAuth2UserInfo userInfo) {
        // Generate unique username
        String baseUsername = userInfo.getName() != null ? 
            userInfo.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "") :
            userInfo.getEmail().split("@")[0];
        
        String uniqueUsername = generateUniqueUsername(baseUsername);
        
        User user = User.builder()
                .username(uniqueUsername)
                .email(userInfo.getEmail())
                .password(UUID.randomUUID().toString()) // Random password for OAuth users
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .provider(provider)
                .providerId(userInfo.getId())
                .role(User.Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .emailVerified(true) // OAuth emails are pre-verified
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Generates a unique username by appending numbers if necessary
     * 
     * @param baseUsername Base username
     * @return Unique username
     */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        
        // Ensure minimum length
        if (username.length() < 3) {
            username = username + "user";
        }
        
        if (!userRepository.existsByUsername(username)) {
            return username;
        }
        
        int counter = 1;
        String uniqueUsername;
        do {
            uniqueUsername = username + counter;
            counter++;
        } while (userRepository.existsByUsername(uniqueUsername));
        
        return uniqueUsername;
    }
    
    /**
     * Extracts OAuth2 provider registration ID from the request
     * 
     * @param request HTTP request
     * @return Registration ID (e.g., "github", "google")
     */
    private String getRegistrationId(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // Extract registration ID from URI like /oauth2/callback/github
        String[] segments = requestURI.split("/");
        return segments[segments.length - 1];
    }
}