package com.devsocial.user.dto;

import com.devsocial.user.model.UserProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for UserProfile entity for API communication
 * 
 * This DTO is used for transferring user profile data between the API and clients.
 * It includes validation annotations and excludes sensitive information.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {
    
    /**
     * User ID (matches auth service user ID)
     */
    private Long userId;
    
    /**
     * User's professional bio/description
     */
    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;
    
    /**
     * User's current job title
     */
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;
    
    /**
     * User's current company
     */
    @Size(max = 100, message = "Company must not exceed 100 characters")
    private String company;
    
    /**
     * User's location
     */
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    /**
     * User's website URL
     */
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;
    
    /**
     * URL to user's profile picture
     */
    private String profilePictureUrl;
    
    /**
     * URL to user's cover/banner image
     */
    private String coverImageUrl;
    
    /**
     * GitHub username
     */
    private String githubUsername;
    
    /**
     * GitHub profile URL
     */
    private String githubUrl;
    
    /**
     * Number of GitHub repositories
     */
    private Integer githubRepos;
    
    /**
     * Number of GitHub followers
     */
    private Integer githubFollowers;
    
    /**
     * Number of GitHub stars across all repos
     */
    private Integer githubStars;
    
    /**
     * GitHub bio
     */
    private String githubBio;
    
    /**
     * Last time GitHub data was synced
     */
    private LocalDateTime githubLastSync;
    
    /**
     * Whether the profile is public
     */
    private Boolean isPublic;
    
    /**
     * Whether the user is available for work
     */
    private Boolean isAvailableForWork;
    
    /**
     * Number of followers on the platform
     */
    private Integer followersCount;
    
    /**
     * Number of users this user is following
     */
    private Integer followingCount;
    
    /**
     * Number of posts by this user
     */
    private Integer postsCount;
    
    /**
     * User's experience level
     */
    private UserProfile.ExperienceLevel experienceLevel;
    
    /**
     * Profile creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Profile last update timestamp
     */
    private LocalDateTime updatedAt;
    
    /**
     * User's skills (optional, loaded on demand)
     */
    private List<UserSkillDto> skills;
    
    /**
     * User's social links (optional, loaded on demand)
     */
    private List<SocialLinkDto> socialLinks;
    
    /**
     * Basic user information from auth service (optional)
     */
    private BasicUserInfo userInfo;
    
    /**
     * Whether the current user is following this profile (context-dependent)
     */
    private Boolean isFollowing;
    
    /**
     * Whether the current user can edit this profile (context-dependent)
     */
    private Boolean canEdit;
    
    /**
     * Checks if the user has GitHub integration
     * 
     * @return true if GitHub username is set
     */
    public boolean hasGitHubIntegration() {
        return githubUsername != null && !githubUsername.trim().isEmpty();
    }
    
    /**
     * Checks if GitHub data is stale (older than 24 hours)
     * 
     * @return true if GitHub data should be refreshed
     */
    public boolean shouldRefreshGitHubData() {
        if (githubLastSync == null) {
            return true;
        }
        return githubLastSync.isBefore(LocalDateTime.now().minusHours(24));
    }
    
    /**
     * Gets the experience level display name
     * 
     * @return Experience level display name or empty string
     */
    public String getExperienceLevelDisplay() {
        return experienceLevel != null ? experienceLevel.getDisplayName() : "";
    }
    
    /**
     * Checks if the profile is complete (has minimum required information)
     * 
     * @return true if profile has bio and job title
     */
    public boolean isProfileComplete() {
        return bio != null && !bio.trim().isEmpty() && 
               jobTitle != null && !jobTitle.trim().isEmpty();
    }
    
    /**
     * Gets the total GitHub activity score
     * 
     * @return Combined score based on repos, followers, and stars
     */
    public int getGitHubActivityScore() {
        int score = 0;
        if (githubRepos != null) score += githubRepos * 2;
        if (githubFollowers != null) score += githubFollowers;
        if (githubStars != null) score += githubStars * 3;
        return score;
    }
    
    /**
     * Basic user information from auth service
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BasicUserInfo {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        
        /**
         * Gets the full display name
         * 
         * @return Full name or username as fallback
         */
        public String getDisplayName() {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else {
                return username;
            }
        }
    }
}