package com.devsocial.post.repository;

import com.devsocial.post.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PostComment entity
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    /**
     * Find comments by post ID (top-level comments only)
     */
    Page<PostComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
    
    /**
     * Find replies to a comment
     */
    Page<PostComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId, Pageable pageable);
    
    /**
     * Count comments for a post
     */
    long countByPostId(Long postId);
    
    /**
     * Count replies for a comment
     */
    long countByParentCommentId(Long parentCommentId);
    
    /**
     * Find comments by author
     */
    Page<PostComment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    /**
     * Find recent comments across all posts
     */
    @Query("SELECT c FROM PostComment c JOIN c.post p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' ORDER BY c.createdAt DESC")
    Page<PostComment> findRecentPublicComments(Pageable pageable);
}