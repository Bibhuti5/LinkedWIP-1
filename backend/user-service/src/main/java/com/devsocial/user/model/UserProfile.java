package com.devsocial.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Profile entity representing extended user information
 * 
 * This entity stores detailed profile information beyond basic authentication data.
 * It includes professional information, social connections, and GitHub integration data.
 * 
 * Features:
 * - Professional profile information
 * - Social connections (followers/following)
 * - GitHub integration data
 * - Skills and experience
 * - Portfolio showcase
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    /**
     * Primary key - matches the auth service user ID
     */
    @Id
    private Long userId;
    
    /**
     * User's professional bio/description
     */
    @Column(columnDefinition = "TEXT")
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
     * User's location (city, country)
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
    @Size(max = 500, message = "Profile picture URL must not exceed 500 characters")
    private String profilePictureUrl;
    
    /**
     * URL to user's cover/banner image
     */
    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;
    
    /**
     * GitHub username for integration
     */
    @Size(max = 50, message = "GitHub username must not exceed 50 characters")
    private String githubUsername;
    
    /**
     * GitHub profile URL
     */
    @Size(max = 255, message = "GitHub URL must not exceed 255 characters")
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
     * GitHub bio from profile
     */
    @Column(columnDefinition = "TEXT")
    private String githubBio;
    
    /**
     * Last time GitHub data was synced
     */
    private LocalDateTime githubLastSync;
    
    /**
     * Whether the user's profile is public
     */
    @Builder.Default
    private Boolean isPublic = true;
    
    /**
     * Whether the user is available for opportunities
     */
    @Builder.Default
    private Boolean isAvailableForWork = false;
    
    /**
     * Number of followers on the platform
     */
    @Builder.Default
    private Integer followersCount = 0;
    
    /**
     * Number of users this user is following
     */
    @Builder.Default
    private Integer followingCount = 0;
    
    /**
     * Number of posts by this user
     */
    @Builder.Default
    private Integer postsCount = 0;
    
    /**
     * User's experience level
     */
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    
    /**
     * Timestamp when the profile was created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the profile was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * User's skills - mapped as separate entity
     */
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSkill> skills;
    
    /**
     * User's social links - mapped as separate entity
     */
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SocialLink> socialLinks;
    
    /**
     * Sets timestamps before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * Updates the updatedAt timestamp before updating in database
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the user has GitHub integration enabled
     * 
     * @return true if GitHub username is set
     */
    public boolean hasGitHubIntegration() {
        return githubUsername != null && !githubUsername.trim().isEmpty();
    }
    
    /**
     * Checks if GitHub data needs to be refreshed (older than 24 hours)
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
     * Experience level enumeration
     */
    public enum ExperienceLevel {
        STUDENT("Student"),
        JUNIOR("Junior (0-2 years)"),
        MID("Mid-level (2-5 years)"),
        SENIOR("Senior (5-10 years)"),
        LEAD("Lead/Principal (10+ years)"),
        EXECUTIVE("Executive/CTO");
        
        private final String displayName;
        
        ExperienceLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}