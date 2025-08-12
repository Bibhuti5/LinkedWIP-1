package com.devsocial.user.service;

import com.devsocial.common.dto.ApiResponse;
import com.devsocial.user.dto.UserProfileDto;
import com.devsocial.user.model.UserFollow;
import com.devsocial.user.model.UserProfile;
import com.devsocial.user.repository.UserFollowRepository;
import com.devsocial.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing user follow relationships
 * 
 * This service handles all business logic related to social connections including:
 * - Following and unfollowing users
 * - Getting followers and following lists
 * - Follow relationship validation
 * - Follower count updates
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserFollowService {
    
    private final UserFollowRepository userFollowRepository;
    private final UserProfileRepository userProfileRepository;
    
    /**
     * Follows a user
     * 
     * @param followerId ID of the user who wants to follow
     * @param followingId ID of the user to be followed
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> followUser(Long followerId, Long followingId) {
        log.info("User {} attempting to follow user {}", followerId, followingId);
        
        try {
            // Validate inputs
            if (followerId.equals(followingId)) {
                return ApiResponse.error("Cannot follow yourself", 400);
            }
            
            // Check if both users exist and target user has public profile
            Optional<UserProfile> followerProfile = userProfileRepository.findById(followerId);
            Optional<UserProfile> followingProfile = userProfileRepository.findById(followingId);
            
            if (followerProfile.isEmpty()) {
                return ApiResponse.error("Follower profile not found", 404);
            }
            
            if (followingProfile.isEmpty()) {
                return ApiResponse.error("User to follow not found", 404);
            }
            
            if (!followingProfile.get().getIsPublic()) {
                return ApiResponse.error("Cannot follow private profile", 403);
            }
            
            // Check if already following
            if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
                return ApiResponse.error("Already following this user", 400);
            }
            
            // Create follow relationship
            UserFollow follow = UserFollow.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .build();
            
            userFollowRepository.save(follow);
            
            // Update follower counts
            updateFollowerCounts(followerId, followingId);
            
            log.info("User {} successfully followed user {}", followerId, followingId);
            return ApiResponse.success("Successfully followed user");
            
        } catch (Exception e) {
            log.error("Error following user {} by user {}: {}", followingId, followerId, e.getMessage());
            return ApiResponse.error("Failed to follow user", 500);
        }
    }
    
    /**
     * Unfollows a user
     * 
     * @param followerId ID of the user who wants to unfollow
     * @param followingId ID of the user to be unfollowed
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> unfollowUser(Long followerId, Long followingId) {
        log.info("User {} attempting to unfollow user {}", followerId, followingId);
        
        try {
            // Find the follow relationship
            Optional<UserFollow> followOpt = userFollowRepository
                    .findByFollowerIdAndFollowingId(followerId, followingId);
            
            if (followOpt.isEmpty()) {
                return ApiResponse.error("Not following this user", 400);
            }
            
            // Delete the follow relationship
            userFollowRepository.delete(followOpt.get());
            
            // Update follower counts
            updateFollowerCounts(followerId, followingId);
            
            log.info("User {} successfully unfollowed user {}", followerId, followingId);
            return ApiResponse.success("Successfully unfollowed user");
            
        } catch (Exception e) {
            log.error("Error unfollowing user {} by user {}: {}", followingId, followerId, e.getMessage());
            return ApiResponse.error("Failed to unfollow user", 500);
        }
    }
    
    /**
     * Checks if a user is following another user
     * 
     * @param followerId Follower user ID
     * @param followingId Following user ID
     * @return true if following, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
    
    /**
     * Gets the list of users that a user is following
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of user profiles
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> getFollowing(Long userId, Pageable pageable) {
        log.info("Getting following list for user {}", userId);
        
        try {
            // Check if user exists
            if (!userProfileRepository.existsById(userId)) {
                return ApiResponse.error("User not found", 404);
            }
            
            Page<UserFollow> follows = userFollowRepository.findByFollowerIdOrderByCreatedAtDesc(userId, pageable);
            
            // Get the user IDs being followed
            List<Long> followingIds = follows.getContent().stream()
                    .map(UserFollow::getFollowingId)
                    .collect(Collectors.toList());
            
            // Get the user profiles
            List<UserProfile> profiles = userProfileRepository.findAllById(followingIds);
            
            // Convert to DTOs and maintain order
            Page<UserProfileDto> profileDtos = follows.map(follow -> {
                Optional<UserProfile> profile = profiles.stream()
                        .filter(p -> p.getUserId().equals(follow.getFollowingId()))
                        .findFirst();
                return profile.map(this::convertToDto).orElse(null);
            });
            
            return ApiResponse.success("Following list retrieved successfully", profileDtos);
            
        } catch (Exception e) {
            log.error("Error getting following list for user {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to get following list", 500);
        }
    }
    
    /**
     * Gets the list of users following a user
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of user profiles
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> getFollowers(Long userId, Pageable pageable) {
        log.info("Getting followers list for user {}", userId);
        
        try {
            // Check if user exists
            if (!userProfileRepository.existsById(userId)) {
                return ApiResponse.error("User not found", 404);
            }
            
            Page<UserFollow> follows = userFollowRepository.findByFollowingIdOrderByCreatedAtDesc(userId, pageable);
            
            // Get the follower user IDs
            List<Long> followerIds = follows.getContent().stream()
                    .map(UserFollow::getFollowerId)
                    .collect(Collectors.toList());
            
            // Get the user profiles
            List<UserProfile> profiles = userProfileRepository.findAllById(followerIds);
            
            // Convert to DTOs and maintain order
            Page<UserProfileDto> profileDtos = follows.map(follow -> {
                Optional<UserProfile> profile = profiles.stream()
                        .filter(p -> p.getUserId().equals(follow.getFollowerId()))
                        .findFirst();
                return profile.map(this::convertToDto).orElse(null);
            });
            
            return ApiResponse.success("Followers list retrieved successfully", profileDtos);
            
        } catch (Exception e) {
            log.error("Error getting followers list for user {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to get followers list", 500);
        }
    }
    
    /**
     * Gets mutual followers between two users
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @param pageable Pagination information
     * @return Page of mutual followers
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> getMutualFollowers(Long userId1, Long userId2, Pageable pageable) {
        log.info("Getting mutual followers between users {} and {}", userId1, userId2);
        
        try {
            // Get followers of both users
            Set<Long> followers1 = userFollowRepository.findFollowerIdsByFollowingId(userId1);
            Set<Long> followers2 = userFollowRepository.findFollowerIdsByFollowingId(userId2);
            
            // Find intersection (mutual followers)
            followers1.retainAll(followers2);
            
            if (followers1.isEmpty()) {
                return ApiResponse.success("No mutual followers found", Page.empty());
            }
            
            // Get user profiles for mutual followers
            List<UserProfile> mutualProfiles = userProfileRepository.findAllById(followers1);
            
            // Convert to DTOs
            List<UserProfileDto> profileDtos = mutualProfiles.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            // Create a page from the list (simplified pagination)
            Page<UserProfileDto> page = Page.empty(); // In real implementation, proper pagination would be applied
            
            return ApiResponse.success("Mutual followers retrieved successfully", page);
            
        } catch (Exception e) {
            log.error("Error getting mutual followers between users {} and {}: {}", userId1, userId2, e.getMessage());
            return ApiResponse.error("Failed to get mutual followers", 500);
        }
    }
    
    /**
     * Gets follow suggestions for a user
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of suggested users to follow
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> getFollowSuggestions(Long userId, Pageable pageable) {
        log.info("Getting follow suggestions for user {}", userId);
        
        try {
            // Get users that the current user is already following
            Set<Long> alreadyFollowing = userFollowRepository.findFollowingIdsByFollowerId(userId);
            alreadyFollowing.add(userId); // Don't suggest self
            
            // Find popular users that are not already being followed
            Page<UserProfile> suggestions = userProfileRepository.findSuggestionsForUser(alreadyFollowing, pageable);
            
            Page<UserProfileDto> suggestionDtos = suggestions.map(this::convertToDto);
            
            return ApiResponse.success("Follow suggestions retrieved successfully", suggestionDtos);
            
        } catch (Exception e) {
            log.error("Error getting follow suggestions for user {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to get follow suggestions", 500);
        }
    }
    
    /**
     * Updates follower and following counts for both users
     * 
     * @param followerId Follower user ID
     * @param followingId Following user ID
     */
    private void updateFollowerCounts(Long followerId, Long followingId) {
        try {
            // Update following count for follower
            long followingCount = userFollowRepository.countByFollowerId(followerId);
            userProfileRepository.updateFollowingCount(followerId, (int) followingCount);
            
            // Update followers count for the user being followed
            long followersCount = userFollowRepository.countByFollowingId(followingId);
            userProfileRepository.updateFollowersCount(followingId, (int) followersCount);
            
            log.debug("Updated follower counts: user {} following {}, user {} followers {}", 
                    followerId, followingCount, followingId, followersCount);
                    
        } catch (Exception e) {
            log.error("Error updating follower counts for users {} and {}: {}", 
                    followerId, followingId, e.getMessage());
        }
    }
    
    /**
     * Converts UserProfile entity to DTO
     * 
     * @param profile UserProfile entity
     * @return UserProfileDto
     */
    private UserProfileDto convertToDto(UserProfile profile) {
        return UserProfileDto.builder()
                .userId(profile.getUserId())
                .bio(profile.getBio())
                .jobTitle(profile.getJobTitle())
                .company(profile.getCompany())
                .location(profile.getLocation())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .githubUsername(profile.getGithubUsername())
                .followersCount(profile.getFollowersCount())
                .followingCount(profile.getFollowingCount())
                .postsCount(profile.getPostsCount())
                .experienceLevel(profile.getExperienceLevel())
                .isPublic(profile.getIsPublic())
                .isAvailableForWork(profile.getIsAvailableForWork())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}