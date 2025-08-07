package com.devsocial.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for User information
 * 
 * This DTO is used to transfer user data between different services and layers
 * without exposing sensitive information like passwords.
 * 
 * Features included:
 * - Basic user information
 * - GitHub integration data
 * - Profile customization
 * - Social connections
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    
    /**
     * Unique identifier for the user
     */
    private Long id;
    
    /**
     * User's unique username
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * User's email address
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * User's first name
     */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    /**
     * User's last name
     */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    /**
     * User's professional bio/description
     */
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    /**
     * URL to user's profile picture
     */
    private String profilePictureUrl;
    
    /**
     * User's location (city, country)
     */
    private String location;
    
    /**
     * User's website URL
     */
    private String website;
    
    /**
     * User's current job title
     */
    private String jobTitle;
    
    /**
     * User's current company
     */
    private String company;
    
    /**
     * List of user's technical skills
     */
    private List<String> skills;
    
    /**
     * GitHub username for integration
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
     * Whether the user's profile is public
     */
    @Builder.Default
    private Boolean isPublic = true;
    
    /**
     * Whether the user is currently active
     */
    @Builder.Default
    private Boolean isActive = true;
    
    /**
     * Whether the user's email is verified
     */
    @Builder.Default
    private Boolean isEmailVerified = false;
    
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
     * Timestamp when the user account was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user profile was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Last time the user was active on the platform
     */
    private LocalDateTime lastActiveAt;
    
    /**
     * Gets the user's full name by combining first and last name
     * 
     * @return Full name or username if names are not available
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    /**
     * Checks if the user has GitHub integration enabled
     * 
     * @return true if GitHub username is set
     */
    public boolean hasGitHubIntegration() {
        return githubUsername != null && !githubUsername.trim().isEmpty();
    }
}