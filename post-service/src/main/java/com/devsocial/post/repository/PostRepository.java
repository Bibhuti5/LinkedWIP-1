package com.devsocial.post.repository;

import com.devsocial.post.model.Post;
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
 * Repository interface for Post entity
 * 
 * Provides comprehensive data access methods for post management including:
 * - CRUD operations
 * - Search and filtering
 * - Trending and discovery algorithms
 * - Social interaction queries
 * - Content moderation support
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // ============= BASIC QUERIES =============
    
    /**
     * Find posts by author ID
     */
    Page<Post> findByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, Post.PostStatus status, Pageable pageable);
    
    /**
     * Find all published posts
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findPublishedPosts(Pageable pageable);
    
    /**
     * Find posts by type
     */
    Page<Post> findByPostTypeAndStatusAndVisibilityOrderByCreatedAtDesc(
        Post.PostType postType, Post.PostStatus status, Post.PostVisibility visibility, Pageable pageable);
    
    /**
     * Find posts by programming language
     */
    Page<Post> findByProgrammingLanguageAndStatusAndVisibilityOrderByCreatedAtDesc(
        String programmingLanguage, Post.PostStatus status, Post.PostVisibility visibility, Pageable pageable);
    
    // ============= SEARCH QUERIES =============
    
    /**
     * Search posts by title and content
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.trendingScore DESC, p.createdAt DESC")
    Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Search posts with filters
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "AND (:postType IS NULL OR p.postType = :postType) " +
           "AND (:programmingLanguage IS NULL OR p.programmingLanguage = :programmingLanguage) " +
           "AND (:difficultyLevel IS NULL OR p.difficultyLevel = :difficultyLevel) " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.trendingScore DESC, p.createdAt DESC")
    Page<Post> searchPostsWithFilters(
        @Param("searchTerm") String searchTerm,
        @Param("postType") Post.PostType postType,
        @Param("programmingLanguage") String programmingLanguage,
        @Param("difficultyLevel") Post.DifficultyLevel difficultyLevel,
        Pageable pageable);
    
    // ============= TRENDING AND DISCOVERY =============
    
    /**
     * Find trending posts based on engagement score
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "AND p.createdAt >= :timeThreshold " +
           "ORDER BY p.trendingScore DESC, p.likesCount DESC")
    Page<Post> findTrendingPosts(@Param("timeThreshold") LocalDateTime timeThreshold, Pageable pageable);
    
    /**
     * Find most liked posts
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "ORDER BY p.likesCount DESC, p.createdAt DESC")
    Page<Post> findMostLikedPosts(Pageable pageable);
    
    /**
     * Find most commented posts
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "ORDER BY p.commentsCount DESC, p.createdAt DESC")
    Page<Post> findMostCommentedPosts(Pageable pageable);
    
    /**
     * Find featured posts
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "AND p.isFeatured = true ORDER BY p.createdAt DESC")
    Page<Post> findFeaturedPosts(Pageable pageable);
    
    // ============= USER-SPECIFIC QUERIES =============
    
    /**
     * Find posts from users that the current user follows
     */
    @Query("SELECT p FROM Post p WHERE p.authorId IN :followingUserIds " +
           "AND p.status = 'PUBLISHED' " +
           "AND (p.visibility = 'PUBLIC' OR p.visibility = 'FOLLOWERS_ONLY') " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findPostsFromFollowingUsers(@Param("followingUserIds") List<Long> followingUserIds, Pageable pageable);
    
    /**
     * Find user's draft posts
     */
    Page<Post> findByAuthorIdAndStatusOrderByUpdatedAtDesc(Long authorId, Post.PostStatus status, Pageable pageable);
    
    /**
     * Find posts liked by a user
     */
    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    Page<Post> findPostsLikedByUser(@Param("userId") Long userId, Pageable pageable);
    
    // ============= ANALYTICS QUERIES =============
    
    /**
     * Count posts by author
     */
    long countByAuthorIdAndStatus(Long authorId, Post.PostStatus status);
    
    /**
     * Count posts created today
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :startOfDay")
    long countPostsCreatedToday(@Param("startOfDay") LocalDateTime startOfDay);
    
    /**
     * Count posts by type
     */
    long countByPostTypeAndStatus(Post.PostType postType, Post.PostStatus status);
    
    /**
     * Get total views for user's posts
     */
    @Query("SELECT COALESCE(SUM(p.viewsCount), 0) FROM Post p WHERE p.authorId = :authorId")
    long getTotalViewsForUser(@Param("authorId") Long authorId);
    
    /**
     * Get total likes for user's posts
     */
    @Query("SELECT COALESCE(SUM(p.likesCount), 0) FROM Post p WHERE p.authorId = :authorId")
    long getTotalLikesForUser(@Param("authorId") Long authorId);
    
    // ============= UPDATE QUERIES =============
    
    /**
     * Update post view count
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
    
    /**
     * Update post like count
     */
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = :likesCount WHERE p.id = :postId")
    void updateLikesCount(@Param("postId") Long postId, @Param("likesCount") Integer likesCount);
    
    /**
     * Update post comment count
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = :commentsCount WHERE p.id = :postId")
    void updateCommentsCount(@Param("postId") Long postId, @Param("commentsCount") Integer commentsCount);
    
    /**
     * Update trending score
     */
    @Modifying
    @Query("UPDATE Post p SET p.trendingScore = :trendingScore WHERE p.id = :postId")
    void updateTrendingScore(@Param("postId") Long postId, @Param("trendingScore") Double trendingScore);
    
    // ============= MODERATION QUERIES =============
    
    /**
     * Find posts pending moderation
     */
    Page<Post> findByStatusOrderByCreatedAtAsc(Post.PostStatus status, Pageable pageable);
    
    /**
     * Find posts by author for moderation
     */
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // ============= RECOMMENDATION QUERIES =============
    
    /**
     * Find similar posts by programming language and type
     */
    @Query("SELECT p FROM Post p WHERE p.id != :excludePostId " +
           "AND p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "AND (p.programmingLanguage = :programmingLanguage OR p.postType = :postType) " +
           "ORDER BY p.trendingScore DESC, p.createdAt DESC")
    Page<Post> findSimilarPosts(
        @Param("excludePostId") Long excludePostId,
        @Param("programmingLanguage") String programmingLanguage,
        @Param("postType") Post.PostType postType,
        Pageable pageable);
    
    /**
     * Find posts by tags (would require join with PostTag)
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.tagName IN :tagNames " +
           "AND p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' " +
           "ORDER BY p.trendingScore DESC")
    Page<Post> findPostsByTags(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
    // ============= CLEANUP QUERIES =============
    
    /**
     * Find old deleted posts for cleanup
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'DELETED' AND p.updatedAt < :cutoffDate")
    List<Post> findOldDeletedPosts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find posts with processing issues
     */
    @Query("SELECT p FROM Post p WHERE EXISTS (SELECT m FROM p.mediaFiles m WHERE m.processingStatus = 'FAILED')")
    List<Post> findPostsWithFailedMedia();
}