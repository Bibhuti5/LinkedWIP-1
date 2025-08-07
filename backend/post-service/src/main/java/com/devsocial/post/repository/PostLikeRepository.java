package com.devsocial.post.repository;

import com.devsocial.post.model.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PostLike entity
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * Check if user has liked a post
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * Find like by post and user
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * Count likes for a post
     */
    long countByPostId(Long postId);
    
    /**
     * Find users who liked a post
     */
    @Query("SELECT pl.userId FROM PostLike pl WHERE pl.post.id = :postId")
    Page<Long> findUserIdsByPostId(@Param("postId") Long postId, Pageable pageable);
    
    /**
     * Delete like by post and user
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * Count likes by reaction type for a post
     */
    long countByPostIdAndReactionType(Long postId, PostLike.ReactionType reactionType);
}