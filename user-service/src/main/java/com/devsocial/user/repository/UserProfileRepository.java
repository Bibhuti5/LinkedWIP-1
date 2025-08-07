package com.devsocial.user.repository;

import com.devsocial.user.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserProfile entity database operations
 * 
 * This repository provides methods for user profile-related database operations including:
 * - Profile CRUD operations
 * - Search and discovery functionality
 * - Social connection queries
 * - GitHub integration data management
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Finds a user profile by GitHub username
     * 
     * @param githubUsername GitHub username to search for
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByGithubUsername(String githubUsername);
    
    /**
     * Finds all public user profiles
     * 
     * @param pageable Pagination information
     * @return Page of public user profiles
     */
    @Query("SELECT up FROM UserProfile up WHERE up.isPublic = true ORDER BY up.updatedAt DESC")
    Page<UserProfile> findPublicProfiles(Pageable pageable);
    
    /**
     * Searches user profiles by various criteria
     * 
     * @param searchTerm Search term to match against name, bio, job title, company
     * @param pageable Pagination information
     * @return Page of matching user profiles
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND (" +
           "LOWER(up.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(up.jobTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(up.company) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(up.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           ") ORDER BY up.updatedAt DESC")
    Page<UserProfile> searchProfiles(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds user profiles by location
     * 
     * @param location Location to search for
     * @param pageable Pagination information
     * @return Page of user profiles in the specified location
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND LOWER(up.location) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "ORDER BY up.updatedAt DESC")
    Page<UserProfile> findByLocation(@Param("location") String location, Pageable pageable);
    
    /**
     * Finds user profiles by company
     * 
     * @param company Company name to search for
     * @param pageable Pagination information
     * @return Page of user profiles working at the specified company
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND LOWER(up.company) LIKE LOWER(CONCAT('%', :company, '%')) " +
           "ORDER BY up.updatedAt DESC")
    Page<UserProfile> findByCompany(@Param("company") String company, Pageable pageable);
    
    /**
     * Finds user profiles by experience level
     * 
     * @param experienceLevel Experience level to filter by
     * @param pageable Pagination information
     * @return Page of user profiles with the specified experience level
     */
    Page<UserProfile> findByExperienceLevelAndIsPublicTrue(
            UserProfile.ExperienceLevel experienceLevel, Pageable pageable);
    
    /**
     * Finds users available for work
     * 
     * @param pageable Pagination information
     * @return Page of user profiles available for work opportunities
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND up.isAvailableForWork = true " +
           "ORDER BY up.updatedAt DESC")
    Page<UserProfile> findAvailableForWork(Pageable pageable);
    
    /**
     * Finds users with GitHub integration
     * 
     * @param pageable Pagination information
     * @return Page of user profiles with GitHub integration
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND up.githubUsername IS NOT NULL " +
           "ORDER BY up.githubStars DESC NULLS LAST")
    Page<UserProfile> findWithGitHubIntegration(Pageable pageable);
    
    /**
     * Finds users whose GitHub data needs refresh
     * 
     * @param cutoffTime Time before which GitHub data is considered stale
     * @return List of user profiles needing GitHub data refresh
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.githubUsername IS NOT NULL AND " +
           "(up.githubLastSync IS NULL OR up.githubLastSync < :cutoffTime)")
    List<UserProfile> findProfilesNeedingGitHubRefresh(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Updates GitHub integration data for a user profile
     * 
     * @param userId User ID
     * @param githubUsername GitHub username
     * @param githubUrl GitHub profile URL
     * @param githubRepos Number of repositories
     * @param githubFollowers Number of GitHub followers
     * @param githubStars Total stars across repositories
     * @param githubBio GitHub bio
     * @param githubLastSync Last sync timestamp
     */
    @Modifying
    @Query("UPDATE UserProfile up SET " +
           "up.githubUsername = :githubUsername, " +
           "up.githubUrl = :githubUrl, " +
           "up.githubRepos = :githubRepos, " +
           "up.githubFollowers = :githubFollowers, " +
           "up.githubStars = :githubStars, " +
           "up.githubBio = :githubBio, " +
           "up.githubLastSync = :githubLastSync " +
           "WHERE up.userId = :userId")
    void updateGitHubData(@Param("userId") Long userId,
                         @Param("githubUsername") String githubUsername,
                         @Param("githubUrl") String githubUrl,
                         @Param("githubRepos") Integer githubRepos,
                         @Param("githubFollowers") Integer githubFollowers,
                         @Param("githubStars") Integer githubStars,
                         @Param("githubBio") String githubBio,
                         @Param("githubLastSync") LocalDateTime githubLastSync);
    
    /**
     * Updates follower count for a user profile
     * 
     * @param userId User ID
     * @param followersCount New follower count
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.followersCount = :followersCount WHERE up.userId = :userId")
    void updateFollowersCount(@Param("userId") Long userId, @Param("followersCount") Integer followersCount);
    
    /**
     * Updates following count for a user profile
     * 
     * @param userId User ID
     * @param followingCount New following count
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.followingCount = :followingCount WHERE up.userId = :userId")
    void updateFollowingCount(@Param("userId") Long userId, @Param("followingCount") Integer followingCount);
    
    /**
     * Updates post count for a user profile
     * 
     * @param userId User ID
     * @param postsCount New post count
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.postsCount = :postsCount WHERE up.userId = :userId")
    void updatePostsCount(@Param("userId") Long userId, @Param("postsCount") Integer postsCount);
    
    /**
     * Gets user profiles ordered by follower count (most popular)
     * 
     * @param pageable Pagination information
     * @return Page of user profiles ordered by popularity
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true " +
           "ORDER BY up.followersCount DESC, up.updatedAt DESC")
    Page<UserProfile> findMostFollowed(Pageable pageable);
    
    /**
     * Gets user profiles with most GitHub stars
     * 
     * @param pageable Pagination information
     * @return Page of user profiles ordered by GitHub stars
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE up.isPublic = true AND up.githubStars IS NOT NULL " +
           "ORDER BY up.githubStars DESC")
    Page<UserProfile> findMostStarred(Pageable pageable);
    
    /**
     * Counts total number of public profiles
     * 
     * @return Number of public profiles
     */
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.isPublic = true")
    long countPublicProfiles();
    
    /**
     * Counts profiles created today
     * 
     * @param startOfDay Start of the current day
     * @return Number of profiles created today
     */
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.createdAt >= :startOfDay")
    long countProfilesCreatedToday(@Param("startOfDay") LocalDateTime startOfDay);
}