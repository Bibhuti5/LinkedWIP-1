package com.devsocial.user.repository;

import com.devsocial.user.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for UserFollow entity database operations
 * 
 * This repository provides methods for managing follow relationships including:
 * - Follow/unfollow operations
 * - Follower and following queries
 * - Mutual follower detection
 * - Follow statistics
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    
    /**
     * Checks if a follow relationship exists between two users
     * 
     * @param followerId Follower user ID
     * @param followingId Following user ID
     * @return true if relationship exists
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    /**
     * Finds a follow relationship between two users
     * 
     * @param followerId Follower user ID
     * @param followingId Following user ID
     * @return Optional follow relationship
     */
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    /**
     * Gets all users that a user is following (ordered by recent)
     * 
     * @param followerId Follower user ID
     * @param pageable Pagination information
     * @return Page of follow relationships
     */
    Page<UserFollow> findByFollowerIdOrderByCreatedAtDesc(Long followerId, Pageable pageable);
    
    /**
     * Gets all users following a specific user (ordered by recent)
     * 
     * @param followingId Following user ID
     * @param pageable Pagination information
     * @return Page of follow relationships
     */
    Page<UserFollow> findByFollowingIdOrderByCreatedAtDesc(Long followingId, Pageable pageable);
    
    /**
     * Counts how many users a user is following
     * 
     * @param followerId Follower user ID
     * @return Count of following relationships
     */
    long countByFollowerId(Long followerId);
    
    /**
     * Counts how many followers a user has
     * 
     * @param followingId Following user ID
     * @return Count of follower relationships
     */
    long countByFollowingId(Long followingId);
    
    /**
     * Gets the set of user IDs that a user is following
     * 
     * @param followerId Follower user ID
     * @return Set of following user IDs
     */
    @Query("SELECT uf.followingId FROM UserFollow uf WHERE uf.followerId = :followerId")
    Set<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
    
    /**
     * Gets the set of user IDs that are following a user
     * 
     * @param followingId Following user ID
     * @return Set of follower user IDs
     */
    @Query("SELECT uf.followerId FROM UserFollow uf WHERE uf.followingId = :followingId")
    Set<Long> findFollowerIdsByFollowingId(@Param("followingId") Long followingId);
    
    /**
     * Gets mutual followers between two users
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Set of mutual follower user IDs
     */
    @Query("SELECT uf1.followerId FROM UserFollow uf1 " +
           "WHERE uf1.followingId = :userId1 " +
           "AND uf1.followerId IN (" +
           "    SELECT uf2.followerId FROM UserFollow uf2 " +
           "    WHERE uf2.followingId = :userId2" +
           ")")
    Set<Long> findMutualFollowers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * Gets users who follow both specified users (for suggestions)
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Set of user IDs who follow both users
     */
    @Query("SELECT uf1.followingId FROM UserFollow uf1 " +
           "WHERE uf1.followerId = :userId1 " +
           "AND uf1.followingId IN (" +
           "    SELECT uf2.followingId FROM UserFollow uf2 " +
           "    WHERE uf2.followerId = :userId2" +
           ")")
    Set<Long> findCommonFollowing(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * Deletes all follow relationships for a user (both as follower and following)
     * 
     * @param userId User ID
     */
    void deleteByFollowerIdOrFollowingId(Long userId, Long userId2);
    
    /**
     * Gets the most recent followers for a user
     * 
     * @param followingId Following user ID
     * @param limit Number of recent followers to get
     * @return List of recent follow relationships
     */
    @Query("SELECT uf FROM UserFollow uf " +
           "WHERE uf.followingId = :followingId " +
           "ORDER BY uf.createdAt DESC " +
           "LIMIT :limit")
    Set<UserFollow> findRecentFollowers(@Param("followingId") Long followingId, @Param("limit") int limit);
    
    /**
     * Gets users that a user recently started following
     * 
     * @param followerId Follower user ID
     * @param limit Number of recent follows to get
     * @return List of recent follow relationships
     */
    @Query("SELECT uf FROM UserFollow uf " +
           "WHERE uf.followerId = :followerId " +
           "ORDER BY uf.createdAt DESC " +
           "LIMIT :limit")
    Set<UserFollow> findRecentFollowing(@Param("followerId") Long followerId, @Param("limit") int limit);
    
    /**
     * Checks if two users follow each other (mutual follow)
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if they follow each other
     */
    @Query("SELECT COUNT(uf) = 2 FROM UserFollow uf " +
           "WHERE (uf.followerId = :userId1 AND uf.followingId = :userId2) " +
           "OR (uf.followerId = :userId2 AND uf.followingId = :userId1)")
    boolean areMutualFollowers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * Gets follow statistics for multiple users
     * 
     * @param userIds Set of user IDs
     * @return Map-like results with user ID and follow counts
     */
    @Query("SELECT uf.followerId, COUNT(uf) as followingCount " +
           "FROM UserFollow uf " +
           "WHERE uf.followerId IN :userIds " +
           "GROUP BY uf.followerId")
    Set<Object[]> getFollowingCountsForUsers(@Param("userIds") Set<Long> userIds);
    
    /**
     * Gets follower statistics for multiple users
     * 
     * @param userIds Set of user IDs
     * @return Map-like results with user ID and follower counts
     */
    @Query("SELECT uf.followingId, COUNT(uf) as followerCount " +
           "FROM UserFollow uf " +
           "WHERE uf.followingId IN :userIds " +
           "GROUP BY uf.followingId")
    Set<Object[]> getFollowerCountsForUsers(@Param("userIds") Set<Long> userIds);
}