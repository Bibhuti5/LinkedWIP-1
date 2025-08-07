package com.devsocial.post.dto;

import com.devsocial.post.model.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Post entity
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    
    private Long id;
    private Long authorId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;
    
    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;
    
    private Post.PostType postType;
    private Post.PostVisibility visibility;
    private Post.PostStatus status;
    
    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Integer viewsCount;
    private Double trendingScore;
    
    private String featuredImageUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private String programmingLanguage;
    private Post.DifficultyLevel difficultyLevel;
    private String sourceUrl;
    private String demoUrl;
    
    private Boolean allowComments;
    private Boolean isFeatured;
    private Boolean isOriginalContent;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Related data
    private List<PostMediaDto> mediaFiles;
    private List<PostTagDto> tags;
    private List<PostCommentDto> comments;
    
    // User context
    private Boolean isLiked;
    private Boolean canEdit;
    private Boolean canDelete;
    
    // Author information (from user service)
    private AuthorInfo author;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AuthorInfo {
        private Long userId;
        private String username;
        private String displayName;
        private String profilePictureUrl;
        private Boolean isVerified;
    }
}