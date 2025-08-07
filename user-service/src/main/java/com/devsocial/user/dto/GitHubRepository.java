package com.devsocial.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for GitHub repository data from GitHub API
 * 
 * This DTO maps the response from GitHub's repositories API endpoint.
 * It contains repository information for user's GitHub integration.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepository {
    
    /**
     * Repository ID
     */
    private Long id;
    
    /**
     * Repository name
     */
    private String name;
    
    /**
     * Full repository name (owner/repo)
     */
    @JsonProperty("full_name")
    private String fullName;
    
    /**
     * Repository description
     */
    private String description;
    
    /**
     * Repository URL
     */
    @JsonProperty("html_url")
    private String htmlUrl;
    
    /**
     * Clone URL
     */
    @JsonProperty("clone_url")
    private String cloneUrl;
    
    /**
     * Primary programming language
     */
    private String language;
    
    /**
     * Number of stargazers
     */
    @JsonProperty("stargazers_count")
    private Integer stargazersCount;
    
    /**
     * Number of watchers
     */
    @JsonProperty("watchers_count")
    private Integer watchersCount;
    
    /**
     * Number of forks
     */
    @JsonProperty("forks_count")
    private Integer forksCount;
    
    /**
     * Repository size in KB
     */
    private Integer size;
    
    /**
     * Default branch name
     */
    @JsonProperty("default_branch")
    private String defaultBranch;
    
    /**
     * Whether repository is private
     */
    @JsonProperty("private")
    private Boolean isPrivate;
    
    /**
     * Whether repository is a fork
     */
    private Boolean fork;
    
    /**
     * Whether repository has issues enabled
     */
    @JsonProperty("has_issues")
    private Boolean hasIssues;
    
    /**
     * Whether repository has wiki enabled
     */
    @JsonProperty("has_wiki")
    private Boolean hasWiki;
    
    /**
     * Whether repository has pages enabled
     */
    @JsonProperty("has_pages")
    private Boolean hasPages;
    
    /**
     * Repository creation date
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    /**
     * Last update date
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Last push date
     */
    @JsonProperty("pushed_at")
    private LocalDateTime pushedAt;
    
    /**
     * Repository topics/tags
     */
    private String[] topics;
    
    /**
     * Repository visibility (public, private, internal)
     */
    private String visibility;
    
    /**
     * Whether repository is archived
     */
    private Boolean archived;
    
    /**
     * Whether repository is disabled
     */
    private Boolean disabled;
    
    /**
     * Repository license information
     */
    private License license;
    
    /**
     * Gets the repository display name
     * 
     * @return Repository name or full name
     */
    public String getDisplayName() {
        return name != null ? name : fullName;
    }
    
    /**
     * Checks if repository has a description
     * 
     * @return true if description is not empty
     */
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }
    
    /**
     * Checks if repository has a primary language
     * 
     * @return true if language is not empty
     */
    public boolean hasLanguage() {
        return language != null && !language.trim().isEmpty();
    }
    
    /**
     * Checks if repository has topics
     * 
     * @return true if topics array is not empty
     */
    public boolean hasTopics() {
        return topics != null && topics.length > 0;
    }
    
    /**
     * Checks if repository is public
     * 
     * @return true if repository is not private
     */
    public boolean isPublic() {
        return isPrivate != null && !isPrivate;
    }
    
    /**
     * Checks if repository is active (not archived or disabled)
     * 
     * @return true if repository is active
     */
    public boolean isActive() {
        return (archived == null || !archived) && (disabled == null || !disabled);
    }
    
    /**
     * Gets star count with fallback to 0
     * 
     * @return Star count or 0
     */
    public int getStarCount() {
        return stargazersCount != null ? stargazersCount : 0;
    }
    
    /**
     * Gets fork count with fallback to 0
     * 
     * @return Fork count or 0
     */
    public int getForkCount() {
        return forksCount != null ? forksCount : 0;
    }
    
    /**
     * License information for repository
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class License {
        private String key;
        private String name;
        @JsonProperty("spdx_id")
        private String spdxId;
        private String url;
        @JsonProperty("node_id")
        private String nodeId;
    }
}