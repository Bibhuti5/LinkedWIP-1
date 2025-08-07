package com.devsocial.post.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PostTag entity representing tags/categories for posts
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "post_tags",
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_name"}),
       indexes = {
           @Index(name = "idx_post_id", columnList = "post_id"),
           @Index(name = "idx_tag_name", columnList = "tag_name")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name must not exceed 50 characters")
    @Column(name = "tag_name", nullable = false)
    private String tagName;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TagType tagType = TagType.GENERAL;
    
    public enum TagType {
        GENERAL, TECHNOLOGY, FRAMEWORK, LANGUAGE, TOOL, CONCEPT
    }
}