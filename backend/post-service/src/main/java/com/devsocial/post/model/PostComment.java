package com.devsocial.post.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PostComment entity representing comments on posts
 * 
 * This entity supports threaded comments with replies and reactions.
 * Features include comment moderation, editing history, and social interactions.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "post_comments", indexes = {
    @Index(name = "idx_post_id", columnList = "post_id"),
    @Index(name = "idx_author_id", columnList = "author_id"),
    @Index(name = "idx_parent_comment", columnList = "parent_comment_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PostComment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> replies;
    
    @Builder.Default
    private Integer likesCount = 0;
    
    @Builder.Default
    private Boolean isEdited = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.isEdited = true;
    }
    
    public boolean isReply() {
        return parentComment != null;
    }
    
    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }
}