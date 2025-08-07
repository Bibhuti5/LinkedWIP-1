package com.devsocial.auth.controller;

import com.devsocial.auth.dto.AuthResponse;
import com.devsocial.auth.dto.LoginRequest;
import com.devsocial.auth.dto.SignupRequest;
import com.devsocial.auth.service.AuthService;
import com.devsocial.auth.service.UserService;
import com.devsocial.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 * 
 * These tests verify the REST API endpoints for authentication functionality:
 * - User registration
 * - User login
 * - Token refresh
 * - Username/email availability checks
 * - Error handling and validation
 * 
 * @author DevSocial Team
 */
@WebMvcTest(AuthController.class)
@DisplayName("Authentication Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private SignupRequest validSignupRequest;
    private LoginRequest validLoginRequest;
    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        validSignupRequest = SignupRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        validLoginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();

        mockAuthResponse = AuthResponse.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .refreshExpiresIn(604800L)
                .build();
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        ApiResponse<AuthResponse> successResponse = ApiResponse.success(
                mockAuthResponse, 
                "User registered successfully", 
                201
        );
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"))
                .andExpected(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Should return validation error for invalid signup data")
    void shouldReturnValidationErrorForInvalidSignup() throws Exception {
        // Given - Invalid signup request (missing required fields)
        SignupRequest invalidRequest = SignupRequest.builder()
                .username("ab") // Too short
                .email("invalid-email") // Invalid format
                .password("123") // Too short
                .confirmPassword("456") // Doesn't match
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("Should return error when username already exists")
    void shouldReturnErrorWhenUsernameExists() throws Exception {
        // Given
        ApiResponse<AuthResponse> errorResponse = ApiResponse.error(
                "Username is already taken", 
                409, 
                "USERNAME_EXISTS"
        );
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.message").value("Username is already taken"))
                .andExpect(jsonPath("$.error").value("USERNAME_EXISTS"));
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfully() throws Exception {
        // Given
        ApiResponse<AuthResponse> successResponse = ApiResponse.success(
                mockAuthResponse, 
                "Login successful"
        );
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
    }

    @Test
    @DisplayName("Should return error for invalid login credentials")
    void shouldReturnErrorForInvalidCredentials() throws Exception {
        // Given
        ApiResponse<AuthResponse> errorResponse = ApiResponse.error(
                "Invalid username or password", 
                401, 
                "INVALID_CREDENTIALS"
        );
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        ApiResponse<AuthResponse> successResponse = ApiResponse.success(
                mockAuthResponse, 
                "Token refreshed successfully"
        );
        when(authService.refreshToken(refreshToken)).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + refreshToken + "\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
    }

    @Test
    @DisplayName("Should check username availability successfully")
    void shouldCheckUsernameAvailability() throws Exception {
        // Given
        String username = "availableuser";
        when(userService.isUsernameAvailable(username)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Username is available"));
    }

    @Test
    @DisplayName("Should return false when username is not available")
    void shouldReturnFalseWhenUsernameNotAvailable() throws Exception {
        // Given
        String username = "takenuser";
        when(userService.isUsernameAvailable(username)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data").value(false))
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    @DisplayName("Should return validation error for invalid username format")
    void shouldReturnValidationErrorForInvalidUsername() throws Exception {
        // Given - Username too short
        String invalidUsername = "ab";

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", invalidUsername))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpected(jsonPath("$.message").value("Username must be between 3 and 50 characters"));
    }

    @Test
    @DisplayName("Should check email availability successfully")
    void shouldCheckEmailAvailability() throws Exception {
        // Given
        String email = "available@example.com";
        when(userService.isEmailAvailable(email)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Email is available"));
    }

    @Test
    @DisplayName("Should return validation error for invalid email format")
    void shouldReturnValidationErrorForInvalidEmail() throws Exception {
        // Given - Invalid email format
        String invalidEmail = "invalid-email";

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", invalidEmail))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpected(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    @DisplayName("Should return health check successfully")
    void shouldReturnHealthCheckSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data").value("Authentication service is healthy"))
                .andExpect(jsonPath("$.message").value("Service is running"));
    }

    @Test
    @DisplayName("Should handle logout successfully")
    void shouldHandleLogoutSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data").value("Logout successful"))
                .andExpect(jsonPath("$.message").value("Please discard your authentication tokens"));
    }

    @Test
    @DisplayName("Should handle missing request body gracefully")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}