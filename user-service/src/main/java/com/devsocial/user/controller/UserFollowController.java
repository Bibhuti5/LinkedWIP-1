package com.devsocial.user.controller;

import com.devsocial.common.dto.ApiResponse;
import com.devsocial.user.dto.UserProfileDto;
import com.devsocial.user.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user follow operations
 * 
 * This controller handles all HTTP requests related to social connections including:
 * - Following and unfollowing users
 * - Getting followers and following lists
 * - Follow suggestions and recommendations
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserFollowController {
    
    private final UserFollowService userFollowService;
    
    /**
     * Follows a user
     * 
     * @param userId User ID to follow
     * @param authentication Current user authentication
     * @return ResponseEntity with follow result
     */
    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<String>> followUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("User {} attempting to follow user {}", authentication.getName(), userId);
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<String> response = userFollowService.followUser(currentUserId, userId);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error following user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to follow user", 500));
        }
    }
    
    /**
     * Unfollows a user
     * 
     * @param userId User ID to unfollow
     * @param authentication Current user authentication
     * @return ResponseEntity with unfollow result
     */
    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("User {} attempting to unfollow user {}", authentication.getName(), userId);
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<String> response = userFollowService.unfollowUser(currentUserId, userId);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error unfollowing user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to unfollow user", 500));
        }
    }
    
    /**
     * Gets the list of users that the current user is following
     * 
     * @param authentication Current user authentication
     * @param pageable Pagination parameters
     * @return ResponseEntity with following list
     */
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getMyFollowing(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting following list for user: {}", authentication.getName());
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getFollowing(currentUserId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting following list: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get following list", 500));
        }
    }
    
    /**
     * Gets the list of users following the current user
     * 
     * @param authentication Current user authentication
     * @param pageable Pagination parameters
     * @return ResponseEntity with followers list
     */
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getMyFollowers(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting followers list for user: {}", authentication.getName());
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getFollowers(currentUserId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting followers list: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get followers list", 500));
        }
    }
    
    /**
     * Gets the list of users that a specific user is following
     * 
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return ResponseEntity with following list
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getUserFollowing(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting following list for user ID: {}", userId);
        
        try {
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getFollowing(userId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting following list for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get following list", 500));
        }
    }
    
    /**
     * Gets the list of users following a specific user
     * 
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return ResponseEntity with followers list
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getUserFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting followers list for user ID: {}", userId);
        
        try {
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getFollowers(userId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting followers list for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get followers list", 500));
        }
    }
    
    /**
     * Gets follow suggestions for the current user
     * 
     * @param authentication Current user authentication
     * @param pageable Pagination parameters
     * @return ResponseEntity with suggested users to follow
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getFollowSuggestions(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Getting follow suggestions for user: {}", authentication.getName());
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getFollowSuggestions(currentUserId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting follow suggestions: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get follow suggestions", 500));
        }
    }
    
    /**
     * Gets mutual followers between current user and another user
     * 
     * @param userId Other user ID
     * @param authentication Current user authentication
     * @param pageable Pagination parameters
     * @return ResponseEntity with mutual followers
     */
    @GetMapping("/mutual/{userId}")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getMutualFollowers(
            @PathVariable Long userId,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting mutual followers between {} and {}", authentication.getName(), userId);
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            ApiResponse<Page<UserProfileDto>> response = userFollowService.getMutualFollowers(currentUserId, userId, pageable);
            
            return ResponseEntity.status(response.getStatusCode()).body(response);
            
        } catch (Exception e) {
            log.error("Error getting mutual followers: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get mutual followers", 500));
        }
    }
    
    /**
     * Checks if current user is following another user
     * 
     * @param userId User ID to check
     * @param authentication Current user authentication
     * @return ResponseEntity with follow status
     */
    @GetMapping("/following/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> getFollowStatus(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Checking follow status for user {} by {}", userId, authentication.getName());
        
        try {
            Long currentUserId = extractUserIdFromAuth(authentication);
            boolean isFollowing = userFollowService.isFollowing(currentUserId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("Follow status retrieved", isFollowing));
            
        } catch (Exception e) {
            log.error("Error checking follow status: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to check follow status", 500));
        }
    }
    
    /**
     * Extracts user ID from authentication object
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
     * Generic exception handler
     * 
     * @param ex Exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error in UserFollowController: {}", ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("An unexpected error occurred", 500));
    }
}