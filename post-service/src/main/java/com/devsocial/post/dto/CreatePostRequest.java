package com.devsocial.post.dto;

import com.devsocial.post.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating new posts
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;
    
    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;
    
    @NotNull(message = "Post type is required")
    private Post.PostType postType;
    
    @Builder.Default
    private Post.PostVisibility visibility = Post.PostVisibility.PUBLIC;
    
    @Builder.Default
    private Post.PostStatus status = Post.PostStatus.DRAFT;
    
    private String featuredImageUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private String programmingLanguage;
    private Post.DifficultyLevel difficultyLevel;
    private String sourceUrl;
    private String demoUrl;
    
    @Builder.Default
    private Boolean allowComments = true;
    
    @Builder.Default
    private Boolean isOriginalContent = true;
    
    private List<String> tags;
    private List<CreateMediaRequest> mediaFiles;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMediaRequest {
        private String filename;
        private String fileUrl;
        private String thumbnailUrl;
        private String mimeType;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private Integer durationSeconds;
        private String altText;
        private String caption;
        private Integer displayOrder;
        private Boolean isFeatured;
    }
}