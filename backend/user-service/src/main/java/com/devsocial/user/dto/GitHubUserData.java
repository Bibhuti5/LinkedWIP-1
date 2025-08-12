package com.devsocial.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for GitHub user data from GitHub API
 * 
 * This DTO maps the response from GitHub's user API endpoint.
 * It contains all the relevant information we need to sync with user profiles.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserData {
    
    /**
     * GitHub user ID
     */
    private Long id;
    
    /**
     * GitHub username (login)
     */
    private String login;
    
    /**
     * Display name
     */
    private String name;
    
    /**
     * User's email address
     */
    private String email;
    
    /**
     * User's bio/description
     */
    private String bio;
    
    /**
     * User's location
     */
    private String location;
    
    /**
     * User's website/blog URL
     */
    private String blog;
    
    /**
     * User's company
     */
    private String company;
    
    /**
     * Profile avatar URL
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    /**
     * GitHub profile URL
     */
    @JsonProperty("html_url")
    private String htmlUrl;
    
    /**
     * Number of public repositories
     */
    @JsonProperty("public_repos")
    private Integer publicRepos;
    
    /**
     * Number of followers
     */
    private Integer followers;
    
    /**
     * Number of users following
     */
    private Integer following;
    
    /**
     * Account creation date
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    /**
     * Last update date
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Whether the user is hireable
     */
    private Boolean hireable;
    
    /**
     * User's Twitter username
     */
    @JsonProperty("twitter_username")
    private String twitterUsername;
    
    /**
     * Account type (User, Organization, etc.)
     */
    private String type;
    
    /**
     * Checks if this is a valid GitHub user response
     * 
     * @return true if required fields are present
     */
    public boolean isValid() {
        return id != null && login != null && !login.trim().isEmpty();
    }
    
    /**
     * Gets the full display name or falls back to username
     * 
     * @return Display name or username
     */
    public String getDisplayName() {
        return (name != null && !name.trim().isEmpty()) ? name : login;
    }
    
    /**
     * Checks if the user has a bio
     * 
     * @return true if bio is not empty
     */
    public boolean hasBio() {
        return bio != null && !bio.trim().isEmpty();
    }
    
    /**
     * Checks if the user has location information
     * 
     * @return true if location is not empty
     */
    public boolean hasLocation() {
        return location != null && !location.trim().isEmpty();
    }
    
    /**
     * Checks if the user has a website/blog
     * 
     * @return true if blog URL is not empty
     */
    public boolean hasWebsite() {
        return blog != null && !blog.trim().isEmpty();
    }
    
    /**
     * Checks if the user has company information
     * 
     * @return true if company is not empty
     */
    public boolean hasCompany() {
        return company != null && !company.trim().isEmpty();
    }
}