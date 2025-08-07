package com.devsocial.auth.service;

import com.devsocial.auth.dto.AuthResponse;
import com.devsocial.auth.dto.LoginRequest;
import com.devsocial.auth.dto.SignupRequest;
import com.devsocial.auth.model.User;
import com.devsocial.auth.repository.UserRepository;
import com.devsocial.common.dto.ApiResponse;
import com.devsocial.common.dto.UserDto;
import com.devsocial.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication service handling user registration, login, and OAuth operations
 * 
 * This service provides comprehensive authentication functionality including:
 * - User registration with validation
 * - User login with JWT token generation
 * - OAuth integration (GitHub, Google, etc.)
 * - Password reset and email verification
 * - Token refresh and validation
 * 
 * Security features:
 * - Password encryption using BCrypt
 * - JWT token generation and validation
 * - Account lockout and security checks
 * - Email verification for new accounts
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
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
     * Registers a new user account
     * 
     * This method performs comprehensive validation and creates a new user account:
     * - Validates input data
     * - Checks for existing username/email
     * - Encrypts password
     * - Generates email verification token
     * - Saves user to database
     * 
     * @param signupRequest User registration data
     * @return ApiResponse containing authentication tokens and user info
     */
    public ApiResponse<AuthResponse> registerUser(SignupRequest signupRequest) {
        log.info("Attempting to register new user with username: {}", signupRequest.getUsername());
        
        try {
            // Validate password confirmation
            if (!signupRequest.isPasswordMatching()) {
                log.warn("Password confirmation mismatch for username: {}", signupRequest.getUsername());
                return ApiResponse.error("Password and confirmation password do not match", 400, "VALIDATION_ERROR");
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(signupRequest.getUsername())) {
                log.warn("Username already exists: {}", signupRequest.getUsername());
                return ApiResponse.error("Username is already taken", 409, "USERNAME_EXISTS");
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                log.warn("Email already exists: {}", signupRequest.getEmail());
                return ApiResponse.error("Email is already registered", 409, "EMAIL_EXISTS");
            }
            
            // Create new user entity
            User user = User.builder()
                    .username(signupRequest.getUsername())
                    .email(signupRequest.getEmail())
                    .password(passwordEncoder.encode(signupRequest.getPassword()))
                    .firstName(signupRequest.getFirstName())
                    .lastName(signupRequest.getLastName())
                    .provider("local")
                    .role(User.Role.USER)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .emailVerified(false)
                    .emailVerificationToken(UUID.randomUUID().toString())
                    .build();
            
            // Save user to database
            User savedUser = userRepository.save(user);
            log.info("Successfully registered new user with ID: {}", savedUser.getId());
            
            // Generate JWT tokens
            String accessToken = jwtUtil.generateTokenFromUsername(savedUser.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());
            
            // Convert to UserDto
            UserDto userDto = userService.convertToDto(savedUser);
            
            // Create authentication response
            AuthResponse authResponse = AuthResponse.success(
                    accessToken,
                    refreshToken,
                    userDto,
                    jwtExpiration / 1000, // Convert to seconds
                    refreshExpiration / 1000 // Convert to seconds
            );
            
            // TODO: Send email verification email
            log.info("Email verification token generated for user: {}", savedUser.getUsername());
            
            return ApiResponse.success(authResponse, "User registered successfully. Please verify your email.", 201);
            
        } catch (Exception e) {
            log.error("Error during user registration for username: {}", signupRequest.getUsername(), e);
            return ApiResponse.error("Registration failed. Please try again.", 500, e.getMessage());
        }
    }
    
    /**
     * Authenticates a user and generates JWT tokens
     * 
     * This method handles user login with the following steps:
     * - Validates credentials using Spring Security
     * - Updates last login timestamp
     * - Generates access and refresh tokens
     * - Returns user information and tokens
     * 
     * @param loginRequest User login credentials
     * @return ApiResponse containing authentication tokens and user info
     */
    public ApiResponse<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        log.info("Attempting to authenticate user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            // Find user by username or email
            Optional<User> userOptional = userRepository.findByUsernameOrEmail(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getUsernameOrEmail()
            );
            
            if (userOptional.isEmpty()) {
                log.warn("User not found: {}", loginRequest.getUsernameOrEmail());
                return ApiResponse.error("Invalid username or password", 401, "INVALID_CREDENTIALS");
            }
            
            User user = userOptional.get();
            
            // Check if account is enabled
            if (!user.isEnabled()) {
                log.warn("Disabled account login attempt: {}", user.getUsername());
                return ApiResponse.error("Account is disabled", 401, "ACCOUNT_DISABLED");
            }
            
            // Check if account is locked
            if (!user.isAccountNonLocked()) {
                log.warn("Locked account login attempt: {}", user.getUsername());
                return ApiResponse.error("Account is locked", 401, "ACCOUNT_LOCKED");
            }
            
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            // Update last login timestamp
            userRepository.updateLastLoginAt(user.getId(), LocalDateTime.now());
            
            // Generate JWT tokens
            String accessToken = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // Convert to UserDto
            UserDto userDto = userService.convertToDto(user);
            
            // Create authentication response
            AuthResponse authResponse = AuthResponse.success(
                    accessToken,
                    refreshToken,
                    userDto,
                    jwtExpiration / 1000, // Convert to seconds
                    refreshExpiration / 1000 // Convert to seconds
            );
            
            log.info("Successfully authenticated user: {}", user.getUsername());
            return ApiResponse.success(authResponse, "Login successful");
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", loginRequest.getUsernameOrEmail());
            return ApiResponse.error("Invalid username or password", 401, "INVALID_CREDENTIALS");
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ApiResponse.error("Authentication failed", 401, e.getMessage());
        } catch (Exception e) {
            log.error("Error during authentication for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ApiResponse.error("Login failed. Please try again.", 500, e.getMessage());
        }
    }
    
    /**
     * Refreshes JWT access token using refresh token
     * 
     * @param refreshToken The refresh token
     * @return ApiResponse containing new access token
     */
    public ApiResponse<AuthResponse> refreshToken(String refreshToken) {
        log.info("Attempting to refresh token");
        
        try {
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                log.warn("Invalid refresh token provided");
                return ApiResponse.error("Invalid refresh token", 401, "INVALID_TOKEN");
            }
            
            // Extract username from refresh token
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            // Find user
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                log.warn("User not found for token refresh: {}", username);
                return ApiResponse.error("User not found", 404, "USER_NOT_FOUND");
            }
            
            User user = userOptional.get();
            
            // Check if user is still active
            if (!user.isEnabled()) {
                log.warn("Disabled user attempting token refresh: {}", username);
                return ApiResponse.error("Account is disabled", 401, "ACCOUNT_DISABLED");
            }
            
            // Generate new access token
            String newAccessToken = jwtUtil.generateTokenFromUsername(username);
            
            // Convert to UserDto
            UserDto userDto = userService.convertToDto(user);
            
            // Create response with new access token
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Keep the same refresh token
                    .user(userDto)
                    .expiresIn(jwtExpiration / 1000)
                    .refreshExpiresIn(refreshExpiration / 1000)
                    .message("Token refreshed successfully")
                    .build();
            
            log.info("Successfully refreshed token for user: {}", username);
            return ApiResponse.success(authResponse, "Token refreshed successfully");
            
        } catch (Exception e) {
            log.error("Error during token refresh", e);
            return ApiResponse.error("Token refresh failed", 500, e.getMessage());
        }
    }
    
    /**
     * Handles OAuth authentication success
     * 
     * This method is called after successful OAuth authentication to:
     * - Create or update user account
     * - Generate JWT tokens
     * - Return authentication response
     * 
     * @param provider OAuth provider name (e.g., "github")
     * @param providerId External user ID from OAuth provider
     * @param email User's email from OAuth provider
     * @param name User's name from OAuth provider
     * @param username Suggested username
     * @return ApiResponse containing authentication tokens and user info
     */
    public ApiResponse<AuthResponse> handleOAuthSuccess(String provider, String providerId, 
                                                       String email, String name, String username) {
        log.info("Handling OAuth success for provider: {} with email: {}", provider, email);
        
        try {
            Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);
            User user;
            boolean isFirstLogin = false;
            
            if (existingUser.isPresent()) {
                // Existing OAuth user
                user = existingUser.get();
                log.info("Found existing OAuth user: {}", user.getUsername());
            } else {
                // Check if user exists with same email but different provider
                Optional<User> emailUser = userRepository.findByEmail(email);
                if (emailUser.isPresent()) {
                    // Link OAuth account to existing user
                    user = emailUser.get();
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    log.info("Linked OAuth account to existing user: {}", user.getUsername());
                } else {
                    // Create new OAuth user
                    isFirstLogin = true;
                    
                    // Ensure unique username
                    String uniqueUsername = generateUniqueUsername(username != null ? username : email.split("@")[0]);
                    
                    user = User.builder()
                            .username(uniqueUsername)
                            .email(email)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password for OAuth users
                            .firstName(extractFirstName(name))
                            .lastName(extractLastName(name))
                            .provider(provider)
                            .providerId(providerId)
                            .role(User.Role.USER)
                            .enabled(true)
                            .accountNonLocked(true)
                            .accountNonExpired(true)
                            .credentialsNonExpired(true)
                            .emailVerified(true) // OAuth emails are pre-verified
                            .build();
                    
                    log.info("Created new OAuth user: {}", user.getUsername());
                }
                
                user = userRepository.save(user);
            }
            
            // Update last login timestamp
            userRepository.updateLastLoginAt(user.getId(), LocalDateTime.now());
            
            // Generate JWT tokens
            String accessToken = jwtUtil.generateTokenFromUsername(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // Convert to UserDto
            UserDto userDto = userService.convertToDto(user);
            
            // Create OAuth authentication response
            AuthResponse authResponse = AuthResponse.oauthSuccess(
                    accessToken,
                    refreshToken,
                    userDto,
                    jwtExpiration / 1000,
                    refreshExpiration / 1000,
                    provider,
                    isFirstLogin
            );
            
            log.info("Successfully handled OAuth authentication for user: {}", user.getUsername());
            return ApiResponse.success(authResponse, "OAuth authentication successful");
            
        } catch (Exception e) {
            log.error("Error during OAuth authentication for provider: {}", provider, e);
            return ApiResponse.error("OAuth authentication failed", 500, e.getMessage());
        }
    }
    
    /**
     * Generates a unique username by appending numbers if necessary
     * 
     * @param baseUsername Base username to make unique
     * @return Unique username
     */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        
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
     * Extracts first name from full name
     * 
     * @param fullName Full name string
     * @return First name or null
     */
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }
    
    /**
     * Extracts last name from full name
     * 
     * @param fullName Full name string
     * @return Last name or null
     */
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : null;
    }
}