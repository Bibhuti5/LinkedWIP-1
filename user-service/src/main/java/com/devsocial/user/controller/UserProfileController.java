package com.devsocial.user.controller;

import com.devsocial.common.dto.ApiResponse;
import com.devsocial.user.dto.UpdateProfileRequest;
import com.devsocial.user.dto.UserProfileDto;
import com.devsocial.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user profile operations
 * 
 * This controller handles all HTTP requests related to user profiles including:
 * - Profile creation and retrieval
 * - Profile updates
 * - Profile search and discovery
 * - GitHub integration management
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    /**
     * Gets or creates a user profile
     * 
     * @param authentication Current user authentication
     * @return ResponseEntity with user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getMyProfile(Authentication authentication) {
        log.info("Getting profile for current user: {}", authentication.getName());
        
        try {
            Long userId = extractUserIdFromAuth(authentication);
            ApiResponse<UserProfileDto> response = userProfileService.getOrCreateProfile(userId);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting current user profile: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get profile", 500));
        }
    }
    
    /**
     * Gets a user profile by user ID
     * 
     * @param userId User ID
     * @param authentication Current user authentication (optional)
     * @return ResponseEntity with user profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Getting profile for user ID: {}", userId);
        
        try {
            Long currentUserId = authentication != null ? extractUserIdFromAuth(authentication) : null;
            ApiResponse<UserProfileDto> response = userProfileService.getProfile(userId, currentUserId);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting user profile for ID {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get profile", 500));
        }
    }
    
    /**
     * Updates the current user's profile
     * 
     * @param updateRequest Profile update request
     * @param authentication Current user authentication
     * @return ResponseEntity with updated profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest updateRequest,
            Authentication authentication) {
        log.info("Updating profile for user: {}", authentication.getName());
        
        try {
            Long userId = extractUserIdFromAuth(authentication);
            ApiResponse<UserProfileDto> response = userProfileService.updateProfile(userId, updateRequest);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to update profile", 500));
        }
    }
    
    /**
     * Searches user profiles
     * 
     * @param query Search query
     * @param pageable Pagination parameters
     * @return ResponseEntity with search results
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> searchProfiles(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching profiles with query: {}", query);
        
        try {
            ApiResponse<Page<UserProfileDto>> response = userProfileService.searchProfiles(query, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error searching profiles: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to search profiles", 500));
        }
    }
    
    /**
     * Gets trending profiles (most followed)
     * 
     * @param pageable Pagination parameters
     * @return ResponseEntity with trending profiles
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getTrendingProfiles(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting trending profiles");
        
        try {
            ApiResponse<Page<UserProfileDto>> response = userProfileService.getTrendingProfiles(pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting trending profiles: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get trending profiles", 500));
        }
    }
    
    /**
     * Synchronizes GitHub data for the current user
     * 
     * @param authentication Current user authentication
     * @return ResponseEntity with sync result
     */
    @PostMapping("/github/sync")
    public ResponseEntity<ApiResponse<UserProfileDto>> syncGitHubData(Authentication authentication) {
        log.info("Syncing GitHub data for user: {}", authentication.getName());
        
        try {
            Long userId = extractUserIdFromAuth(authentication);
            ApiResponse<UserProfileDto> response = userProfileService.syncGitHubData(userId);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error syncing GitHub data: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to sync GitHub data", 500));
        }
    }
    
    /**
     * Gets profiles by location
     * 
     * @param location Location to filter by
     * @param pageable Pagination parameters
     * @return ResponseEntity with profiles in location
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getProfilesByLocation(
            @PathVariable String location,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting profiles by location: {}", location);
        
        try {
            // This would use a repository method to filter by location
            ApiResponse<Page<UserProfileDto>> response = userProfileService.searchProfiles(location, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting profiles by location: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get profiles by location", 500));
        }
    }
    
    /**
     * Gets profiles available for work
     * 
     * @param pageable Pagination parameters
     * @return ResponseEntity with available profiles
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getAvailableProfiles(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting profiles available for work");
        
        try {
            // This would be implemented in the service layer
            ApiResponse<Page<UserProfileDto>> response = userProfileService.searchProfiles("", pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting available profiles: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get available profiles", 500));
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("User service is healthy"));
    }
    
    /**
     * Extracts user ID from authentication object
     * This assumes the authentication contains user ID in the name or principal
     * In a real implementation, this would be more sophisticated
     * 
     * @param authentication Authentication object
     * @return User ID
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        try {
            // This is a simplified implementation
            // In reality, you'd extract this from JWT claims or user details
            String name = authentication.getName();
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // Fallback - in real implementation, you'd have proper user ID extraction
            log.warn("Could not extract user ID from authentication, using fallback");
            return 1L; // Fallback for development
        }
    }
    
    /**
     * Exception handler for validation errors
     * 
     * @param ex Validation exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, 400));
    }
    
    /**
     * Generic exception handler
     * 
     * @param ex Exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error in UserProfileController: {}", ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("An unexpected error occurred", 500));
    }
}