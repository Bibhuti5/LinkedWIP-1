package com.devsocial.user.service;

import com.devsocial.user.dto.GitHubUserData;
import com.devsocial.user.dto.GitHubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Service for integrating with GitHub API
 * 
 * This service handles all interactions with the GitHub API including:
 * - Fetching user profile information
 * - Retrieving repository data
 * - Calculating statistics (stars, followers, etc.)
 * - Caching responses to minimize API calls
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubIntegrationService {
    
    private final WebClient webClient;
    
    @Value("${github.api.base-url:https://api.github.com}")
    private String githubApiBaseUrl;
    
    @Value("${github.api.token:}")
    private String githubToken;
    
    /**
     * Fetches GitHub user data by username
     * 
     * @param username GitHub username
     * @return GitHubUserData containing user information
     */
    @Cacheable(value = "github-users", key = "#username", unless = "#result == null")
    public GitHubUserData fetchUserData(String username) {
        log.info("Fetching GitHub data for user: {}", username);
        
        try {
            WebClient.RequestHeadersSpec<?> request = webClient
                    .get()
                    .uri(githubApiBaseUrl + "/users/{username}", username);
            
            // Add authorization header if token is available
            if (githubToken != null && !githubToken.isEmpty()) {
                request = request.header(HttpHeaders.AUTHORIZATION, "token " + githubToken);
            }
            
            GitHubUserData userData = request
                    .retrieve()
                    .bodyToMono(GitHubUserData.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (userData != null) {
                log.info("Successfully fetched GitHub data for user: {}", username);
                return userData;
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("GitHub user not found: {}", username);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error("GitHub API rate limit exceeded or access forbidden");
            } else {
                log.error("Error fetching GitHub user data for {}: {}", username, e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error fetching GitHub data for {}: {}", username, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Fetches user's repositories from GitHub
     * 
     * @param username GitHub username
     * @param page Page number for pagination
     * @param perPage Items per page (max 100)
     * @return List of repositories
     */
    @Cacheable(value = "github-repos", key = "#username + '_' + #page", unless = "#result == null")
    public List<GitHubRepository> fetchUserRepositories(String username, int page, int perPage) {
        log.info("Fetching repositories for GitHub user: {} (page: {}, perPage: {})", username, page, perPage);
        
        try {
            WebClient.RequestHeadersSpec<?> request = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(githubApiBaseUrl + "/users/{username}/repos")
                            .queryParam("sort", "updated")
                            .queryParam("direction", "desc")
                            .queryParam("page", page)
                            .queryParam("per_page", Math.min(perPage, 100))
                            .build(username));
            
            // Add authorization header if token is available
            if (githubToken != null && !githubToken.isEmpty()) {
                request = request.header(HttpHeaders.AUTHORIZATION, "token " + githubToken);
            }
            
            List<GitHubRepository> repositories = request
                    .retrieve()
                    .bodyToFlux(GitHubRepository.class)
                    .timeout(Duration.ofSeconds(15))
                    .collectList()
                    .block();
            
            if (repositories != null) {
                log.info("Successfully fetched {} repositories for user: {}", repositories.size(), username);
                return repositories;
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("GitHub user or repositories not found: {}", username);
            } else {
                log.error("Error fetching repositories for {}: {}", username, e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error fetching repositories for {}: {}", username, e.getMessage());
        }
        
        return List.of();
    }
    
    /**
     * Calculates total stars across all user repositories
     * 
     * @param username GitHub username
     * @return Total number of stars
     */
    public int calculateTotalStars(String username) {
        log.info("Calculating total stars for GitHub user: {}", username);
        
        int totalStars = 0;
        int page = 1;
        int perPage = 100;
        
        try {
            List<GitHubRepository> repositories;
            do {
                repositories = fetchUserRepositories(username, page, perPage);
                totalStars += repositories.stream()
                        .mapToInt(GitHubRepository::getStargazersCount)
                        .sum();
                page++;
            } while (repositories.size() == perPage); // Continue if we got a full page
            
            log.info("Total stars for {}: {}", username, totalStars);
            return totalStars;
            
        } catch (Exception e) {
            log.error("Error calculating total stars for {}: {}", username, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Validates if a GitHub username exists
     * 
     * @param username GitHub username to validate
     * @return true if the username exists
     */
    public boolean validateGitHubUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            GitHubUserData userData = fetchUserData(username);
            return userData != null;
        } catch (Exception e) {
            log.error("Error validating GitHub username {}: {}", username, e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the rate limit status from GitHub API
     * 
     * @return Rate limit information
     */
    public GitHubRateLimit getRateLimit() {
        try {
            WebClient.RequestHeadersSpec<?> request = webClient
                    .get()
                    .uri(githubApiBaseUrl + "/rate_limit");
            
            // Add authorization header if token is available
            if (githubToken != null && !githubToken.isEmpty()) {
                request = request.header(HttpHeaders.AUTHORIZATION, "token " + githubToken);
            }
            
            return request
                    .retrieve()
                    .bodyToMono(GitHubRateLimit.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
        } catch (Exception e) {
            log.error("Error fetching GitHub rate limit: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Rate limit information from GitHub API
     */
    public static class GitHubRateLimit {
        private RateInfo core;
        private RateInfo search;
        
        // Getters and setters
        public RateInfo getCore() { return core; }
        public void setCore(RateInfo core) { this.core = core; }
        public RateInfo getSearch() { return search; }
        public void setSearch(RateInfo search) { this.search = search; }
        
        public static class RateInfo {
            private int limit;
            private int remaining;
            private long reset;
            
            // Getters and setters
            public int getLimit() { return limit; }
            public void setLimit(int limit) { this.limit = limit; }
            public int getRemaining() { return remaining; }
            public void setRemaining(int remaining) { this.remaining = remaining; }
            public long getReset() { return reset; }
            public void setReset(long reset) { this.reset = reset; }
        }
    }
}