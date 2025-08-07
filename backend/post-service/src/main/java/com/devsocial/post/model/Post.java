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
 * Post entity representing user-generated content
 * 
 * This entity represents posts in the DevSocial platform including:
 * - Video/project showcases
 * - Architecture diagrams and explanations
 * - Code snippets and tutorials
 * - Developer insights and experiences
 * 
 * Features:
 * - Rich content with multiple media types
 * - Tagging and categorization system
 * - Social interactions (likes, comments, shares)
 * - Content moderation and visibility controls
 * - Search optimization and trending algorithms
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_author_id", columnList = "author_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_post_type", columnList = "post_type"),
    @Index(name = "idx_visibility", columnList = "visibility"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_trending_score", columnList = "trending_score")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    /**
     * Primary key for the post
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Author of the post (user ID from auth service)
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    /**
     * Post title
     */
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    /**
     * Post content/description
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;
    
    /**
     * Brief summary/excerpt of the post
     */
    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;
    
    /**
     * Type of post
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;
    
    /**
     * Visibility level of the post
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostVisibility visibility = PostVisibility.PUBLIC;
    
    /**
     * Current status of the post
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.PUBLISHED;
    
    /**
     * Number of likes on the post
     */
    @Builder.Default
    private Integer likesCount = 0;
    
    /**
     * Number of comments on the post
     */
    @Builder.Default
    private Integer commentsCount = 0;
    
    /**
     * Number of shares/reposts
     */
    @Builder.Default
    private Integer sharesCount = 0;
    
    /**
     * Number of views on the post
     */
    @Builder.Default
    private Integer viewsCount = 0;
    
    /**
     * Trending score for algorithm ranking
     */
    @Builder.Default
    private Double trendingScore = 0.0;
    
    /**
     * Featured image URL for the post
     */
    @Size(max = 500, message = "Featured image URL must not exceed 500 characters")
    private String featuredImageUrl;
    
    /**
     * Thumbnail URL for video posts
     */
    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters")
    private String thumbnailUrl;
    
    /**
     * Duration for video content (in seconds)
     */
    private Integer durationSeconds;
    
    /**
     * Programming language for code-related posts
     */
    @Size(max = 50, message = "Programming language must not exceed 50 characters")
    private String programmingLanguage;
    
    /**
     * Difficulty level for tutorial posts
     */
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    /**
     * External source URL (GitHub repo, live demo, etc.)
     */
    @Size(max = 500, message = "Source URL must not exceed 500 characters")
    private String sourceUrl;
    
    /**
     * Demo URL for live projects
     */
    @Size(max = 500, message = "Demo URL must not exceed 500 characters")
    private String demoUrl;
    
    /**
     * Whether the post allows comments
     */
    @Builder.Default
    private Boolean allowComments = true;
    
    /**
     * Whether the post is featured/pinned
     */
    @Builder.Default
    private Boolean isFeatured = false;
    
    /**
     * Whether the post is marked as original content
     */
    @Builder.Default
    private Boolean isOriginalContent = true;
    
    /**
     * Timestamp when the post was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the post was last updated
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Timestamp when the post was published
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    /**
     * Post media files (images, videos, diagrams)
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostMedia> mediaFiles;
    
    /**
     * Post tags for categorization
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostTag> tags;
    
    /**
     * Post comments
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> comments;
    
    /**
     * Post likes/reactions
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes;
    
    /**
     * Sets timestamps before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == PostStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = now;
        }
    }
    
    /**
     * Updates the updatedAt timestamp before updating in database
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == PostStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Checks if the post is published and visible
     * 
     * @return true if post is published and public
     */
    public boolean isVisibleToPublic() {
        return status == PostStatus.PUBLISHED && visibility == PostVisibility.PUBLIC;
    }
    
    /**
     * Checks if the post has media content
     * 
     * @return true if post has media files
     */
    public boolean hasMedia() {
        return mediaFiles != null && !mediaFiles.isEmpty();
    }
    
    /**
     * Checks if the post is a video post
     * 
     * @return true if post type is video
     */
    public boolean isVideoPost() {
        return postType == PostType.VIDEO;
    }
    
    /**
     * Gets the engagement score based on interactions
     * 
     * @return Engagement score
     */
    public double getEngagementScore() {
        int totalInteractions = (likesCount != null ? likesCount : 0) +
                               (commentsCount != null ? commentsCount : 0) +
                               (sharesCount != null ? sharesCount : 0);
        int views = viewsCount != null ? viewsCount : 1;
        return (double) totalInteractions / views * 100;
    }
    
    /**
     * Post type enumeration
     */
    public enum PostType {
        VIDEO("Video"),
        PROJECT("Project Showcase"),
        TUTORIAL("Tutorial"),
        ARTICLE("Article"),
        CODE_SNIPPET("Code Snippet"),
        ARCHITECTURE("Architecture Diagram"),
        DISCUSSION("Discussion"),
        ANNOUNCEMENT("Announcement"),
        QUESTION("Question"),
        REVIEW("Review");
        
        private final String displayName;
        
        PostType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Post visibility enumeration
     */
    public enum PostVisibility {
        PUBLIC("Public"),
        FOLLOWERS_ONLY("Followers Only"),
        PRIVATE("Private"),
        UNLISTED("Unlisted");
        
        private final String displayName;
        
        PostVisibility(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Post status enumeration
     */
    public enum PostStatus {
        DRAFT("Draft"),
        PUBLISHED("Published"),
        ARCHIVED("Archived"),
        DELETED("Deleted"),
        MODERATED("Under Moderation"),
        REJECTED("Rejected");
        
        private final String displayName;
        
        PostStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Difficulty level enumeration
     */
    public enum DifficultyLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced"),
        EXPERT("Expert");
        
        private final String displayName;
        
        DifficultyLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}