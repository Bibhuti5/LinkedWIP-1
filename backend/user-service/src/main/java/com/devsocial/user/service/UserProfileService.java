package com.devsocial.user.service;

import com.devsocial.common.dto.ApiResponse;
import com.devsocial.user.dto.GitHubUserData;
import com.devsocial.user.dto.UpdateProfileRequest;
import com.devsocial.user.dto.UserProfileDto;
import com.devsocial.user.model.UserProfile;
import com.devsocial.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing user profiles
 * 
 * This service handles all business logic related to user profiles including:
 * - Profile creation and updates
 * - GitHub integration synchronization
 * - Profile search and discovery
 * - Privacy and visibility management
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final GitHubIntegrationService gitHubIntegrationService;
    private final UserFollowService userFollowService;
    
    /**
     * Creates or gets a user profile for the given user ID
     * 
     * @param userId User ID from auth service
     * @return ApiResponse containing the user profile
     */
    public ApiResponse<UserProfileDto> getOrCreateProfile(Long userId) {
        log.info("Getting or creating profile for user ID: {}", userId);
        
        try {
            Optional<UserProfile> existingProfile = userProfileRepository.findById(userId);
            
            if (existingProfile.isPresent()) {
                UserProfileDto profileDto = convertToDto(existingProfile.get());
                return ApiResponse.success("Profile retrieved successfully", profileDto);
            }
            
            // Create new profile with default values
            UserProfile newProfile = UserProfile.builder()
                    .userId(userId)
                    .isPublic(true)
                    .isAvailableForWork(false)
                    .followersCount(0)
                    .followingCount(0)
                    .postsCount(0)
                    .build();
            
            UserProfile savedProfile = userProfileRepository.save(newProfile);
            UserProfileDto profileDto = convertToDto(savedProfile);
            
            log.info("Created new profile for user ID: {}", userId);
            return ApiResponse.success("Profile created successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error getting or creating profile for user ID {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to get or create profile", 500);
        }
    }
    
    /**
     * Updates a user profile
     * 
     * @param userId User ID
     * @param updateRequest Update request with new profile data
     * @return ApiResponse containing the updated profile
     */
    public ApiResponse<UserProfileDto> updateProfile(Long userId, UpdateProfileRequest updateRequest) {
        log.info("Updating profile for user ID: {}", userId);
        
        try {
            if (!updateRequest.hasUpdates()) {
                return ApiResponse.error("No updates provided", 400);
            }
            
            if (!updateRequest.hasValidUrls()) {
                return ApiResponse.error("Invalid URL format provided", 400);
            }
            
            Optional<UserProfile> profileOpt = userProfileRepository.findById(userId);
            if (profileOpt.isEmpty()) {
                return ApiResponse.error("Profile not found", 404);
            }
            
            UserProfile profile = profileOpt.get();
            
            // Update fields if provided
            if (updateRequest.getBio() != null) {
                profile.setBio(updateRequest.getBio());
            }
            if (updateRequest.getJobTitle() != null) {
                profile.setJobTitle(updateRequest.getJobTitle());
            }
            if (updateRequest.getCompany() != null) {
                profile.setCompany(updateRequest.getCompany());
            }
            if (updateRequest.getLocation() != null) {
                profile.setLocation(updateRequest.getLocation());
            }
            if (updateRequest.getWebsite() != null) {
                profile.setWebsite(updateRequest.getWebsite());
            }
            if (updateRequest.getProfilePictureUrl() != null) {
                profile.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
            }
            if (updateRequest.getCoverImageUrl() != null) {
                profile.setCoverImageUrl(updateRequest.getCoverImageUrl());
            }
            if (updateRequest.getIsPublic() != null) {
                profile.setIsPublic(updateRequest.getIsPublic());
            }
            if (updateRequest.getIsAvailableForWork() != null) {
                profile.setIsAvailableForWork(updateRequest.getIsAvailableForWork());
            }
            if (updateRequest.getExperienceLevel() != null) {
                profile.setExperienceLevel(updateRequest.getExperienceLevel());
            }
            
            // Handle GitHub username update
            if (updateRequest.isUpdatingGitHub()) {
                String newGithubUsername = updateRequest.getGithubUsername();
                if (newGithubUsername != null && !newGithubUsername.trim().isEmpty()) {
                    // Validate GitHub username
                    if (gitHubIntegrationService.validateGitHubUsername(newGithubUsername)) {
                        profile.setGithubUsername(newGithubUsername);
                        // Trigger GitHub data sync asynchronously
                        syncGitHubDataAsync(userId, newGithubUsername);
                    } else {
                        return ApiResponse.error("Invalid GitHub username", 400);
                    }
                } else {
                    // Remove GitHub integration
                    profile.setGithubUsername(null);
                    profile.setGithubUrl(null);
                    profile.setGithubRepos(null);
                    profile.setGithubFollowers(null);
                    profile.setGithubStars(null);
                    profile.setGithubBio(null);
                    profile.setGithubLastSync(null);
                }
            }
            
            UserProfile updatedProfile = userProfileRepository.save(profile);
            UserProfileDto profileDto = convertToDto(updatedProfile);
            
            log.info("Updated profile for user ID: {}", userId);
            return ApiResponse.success("Profile updated successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error updating profile for user ID {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to update profile", 500);
        }
    }
    
    /**
     * Gets a user profile by user ID
     * 
     * @param userId User ID
     * @param currentUserId Current user's ID (for context)
     * @return ApiResponse containing the user profile
     */
    @Transactional(readOnly = true)
    public ApiResponse<UserProfileDto> getProfile(Long userId, Long currentUserId) {
        log.info("Getting profile for user ID: {} (requested by: {})", userId, currentUserId);
        
        try {
            Optional<UserProfile> profileOpt = userProfileRepository.findById(userId);
            if (profileOpt.isEmpty()) {
                return ApiResponse.error("Profile not found", 404);
            }
            
            UserProfile profile = profileOpt.get();
            
            // Check if profile is public or if it's the user's own profile
            if (!profile.getIsPublic() && !userId.equals(currentUserId)) {
                return ApiResponse.error("Profile is private", 403);
            }
            
            UserProfileDto profileDto = convertToDto(profile);
            
            // Add context-specific information
            if (currentUserId != null && !userId.equals(currentUserId)) {
                // Check if current user is following this profile
                boolean isFollowing = userFollowService.isFollowing(currentUserId, userId);
                profileDto.setIsFollowing(isFollowing);
            }
            
            profileDto.setCanEdit(userId.equals(currentUserId));
            
            return ApiResponse.success("Profile retrieved successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error getting profile for user ID {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to get profile", 500);
        }
    }
    
    /**
     * Searches user profiles
     * 
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of user profiles
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> searchProfiles(String searchTerm, Pageable pageable) {
        log.info("Searching profiles with term: {}", searchTerm);
        
        try {
            Page<UserProfile> profiles;
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                profiles = userProfileRepository.findPublicProfiles(pageable);
            } else {
                profiles = userProfileRepository.searchProfiles(searchTerm.trim(), pageable);
            }
            
            Page<UserProfileDto> profileDtos = profiles.map(this::convertToDto);
            
            return ApiResponse.success("Profiles retrieved successfully", profileDtos);
            
        } catch (Exception e) {
            log.error("Error searching profiles with term {}: {}", searchTerm, e.getMessage());
            return ApiResponse.error("Failed to search profiles", 500);
        }
    }
    
    /**
     * Synchronizes GitHub data for a user profile
     * 
     * @param userId User ID
     * @return ApiResponse with sync result
     */
    public ApiResponse<UserProfileDto> syncGitHubData(Long userId) {
        log.info("Syncing GitHub data for user ID: {}", userId);
        
        try {
            Optional<UserProfile> profileOpt = userProfileRepository.findById(userId);
            if (profileOpt.isEmpty()) {
                return ApiResponse.error("Profile not found", 404);
            }
            
            UserProfile profile = profileOpt.get();
            if (!profile.hasGitHubIntegration()) {
                return ApiResponse.error("GitHub integration not configured", 400);
            }
            
            GitHubUserData githubData = gitHubIntegrationService.fetchUserData(profile.getGithubUsername());
            if (githubData == null || !githubData.isValid()) {
                return ApiResponse.error("Failed to fetch GitHub data", 500);
            }
            
            // Calculate total stars
            int totalStars = gitHubIntegrationService.calculateTotalStars(profile.getGithubUsername());
            
            // Update profile with GitHub data
            profile.setGithubUrl(githubData.getHtmlUrl());
            profile.setGithubRepos(githubData.getPublicRepos());
            profile.setGithubFollowers(githubData.getFollowers());
            profile.setGithubStars(totalStars);
            profile.setGithubBio(githubData.getBio());
            profile.setGithubLastSync(LocalDateTime.now());
            
            // Update profile information if not already set
            if (profile.getBio() == null && githubData.hasBio()) {
                profile.setBio(githubData.getBio());
            }
            if (profile.getLocation() == null && githubData.hasLocation()) {
                profile.setLocation(githubData.getLocation());
            }
            if (profile.getWebsite() == null && githubData.hasWebsite()) {
                profile.setWebsite(githubData.getBlog());
            }
            if (profile.getCompany() == null && githubData.hasCompany()) {
                profile.setCompany(githubData.getCompany());
            }
            if (profile.getProfilePictureUrl() == null && githubData.getAvatarUrl() != null) {
                profile.setProfilePictureUrl(githubData.getAvatarUrl());
            }
            
            UserProfile updatedProfile = userProfileRepository.save(profile);
            UserProfileDto profileDto = convertToDto(updatedProfile);
            
            log.info("Successfully synced GitHub data for user ID: {}", userId);
            return ApiResponse.success("GitHub data synchronized successfully", profileDto);
            
        } catch (Exception e) {
            log.error("Error syncing GitHub data for user ID {}: {}", userId, e.getMessage());
            return ApiResponse.error("Failed to sync GitHub data", 500);
        }
    }
    
    /**
     * Gets profiles that are trending (most followed recently)
     * 
     * @param pageable Pagination information
     * @return Page of trending profiles
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<UserProfileDto>> getTrendingProfiles(Pageable pageable) {
        log.info("Getting trending profiles");
        
        try {
            Page<UserProfile> profiles = userProfileRepository.findMostFollowed(pageable);
            Page<UserProfileDto> profileDtos = profiles.map(this::convertToDto);
            
            return ApiResponse.success("Trending profiles retrieved successfully", profileDtos);
            
        } catch (Exception e) {
            log.error("Error getting trending profiles: {}", e.getMessage());
            return ApiResponse.error("Failed to get trending profiles", 500);
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
                .website(profile.getWebsite())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .coverImageUrl(profile.getCoverImageUrl())
                .githubUsername(profile.getGithubUsername())
                .githubUrl(profile.getGithubUrl())
                .githubRepos(profile.getGithubRepos())
                .githubFollowers(profile.getGithubFollowers())
                .githubStars(profile.getGithubStars())
                .githubBio(profile.getGithubBio())
                .githubLastSync(profile.getGithubLastSync())
                .isPublic(profile.getIsPublic())
                .isAvailableForWork(profile.getIsAvailableForWork())
                .followersCount(profile.getFollowersCount())
                .followingCount(profile.getFollowingCount())
                .postsCount(profile.getPostsCount())
                .experienceLevel(profile.getExperienceLevel())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
    
    /**
     * Synchronizes GitHub data asynchronously
     * 
     * @param userId User ID
     * @param githubUsername GitHub username
     */
    private void syncGitHubDataAsync(Long userId, String githubUsername) {
        // In a real application, this would be done asynchronously using @Async
        // For now, we'll just log that it should be done asynchronously
        log.info("Should sync GitHub data asynchronously for user {} with GitHub username {}", 
                userId, githubUsername);
    }
}