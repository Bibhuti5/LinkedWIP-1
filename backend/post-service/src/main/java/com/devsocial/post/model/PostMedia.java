package com.devsocial.post.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PostMedia entity representing media files attached to posts
 * 
 * This entity handles various types of media content including:
 * - Images (JPG, PNG, GIF, WebP)
 * - Videos (MP4, WebM, MOV)
 * - Architecture diagrams and technical drawings
 * - Code screenshots and documentation
 * 
 * Features:
 * - Multiple file format support
 * - Metadata storage (dimensions, file size, duration)
 * - CDN integration for optimized delivery
 * - Thumbnail generation for videos and images
 * - Alt text support for accessibility
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "post_media", indexes = {
    @Index(name = "idx_post_id", columnList = "post_id"),
    @Index(name = "idx_media_type", columnList = "media_type"),
    @Index(name = "idx_display_order", columnList = "display_order")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMedia {
    
    /**
     * Primary key for the media file
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the parent post
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    /**
     * Type of media content
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;
    
    /**
     * Original filename
     */
    @NotBlank(message = "Filename is required")
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    @Column(nullable = false)
    private String filename;
    
    /**
     * URL to the media file (CDN or storage URL)
     */
    @NotBlank(message = "File URL is required")
    @Size(max = 1000, message = "File URL must not exceed 1000 characters")
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    /**
     * URL to the thumbnail (for videos and large images)
     */
    @Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    /**
     * MIME type of the file
     */
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    @Column(name = "mime_type")
    private String mimeType;
    
    /**
     * File size in bytes
     */
    @Column(name = "file_size")
    private Long fileSize;
    
    /**
     * Width in pixels (for images and videos)
     */
    private Integer width;
    
    /**
     * Height in pixels (for images and videos)
     */
    private Integer height;
    
    /**
     * Duration in seconds (for videos and audio)
     */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    /**
     * Alternative text for accessibility
     */
    @Size(max = 500, message = "Alt text must not exceed 500 characters")
    @Column(name = "alt_text")
    private String altText;
    
    /**
     * Caption or description for the media
     */
    @Size(max = 1000, message = "Caption must not exceed 1000 characters")
    private String caption;
    
    /**
     * Display order within the post (for multiple media)
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
    
    /**
     * Whether this media is the featured/primary media for the post
     */
    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;
    
    /**
     * Processing status for uploaded media
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;
    
    /**
     * Storage provider (S3, CloudFront, etc.)
     */
    @Size(max = 50, message = "Storage provider must not exceed 50 characters")
    @Column(name = "storage_provider")
    private String storageProvider;
    
    /**
     * Storage path/key in the provider
     */
    @Size(max = 500, message = "Storage path must not exceed 500 characters")
    @Column(name = "storage_path")
    private String storagePath;
    
    /**
     * Timestamp when the media was uploaded
     */
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    /**
     * Timestamp when processing was completed
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    /**
     * Sets timestamp before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the media is an image
     * 
     * @return true if media type is image
     */
    public boolean isImage() {
        return mediaType == MediaType.IMAGE;
    }
    
    /**
     * Checks if the media is a video
     * 
     * @return true if media type is video
     */
    public boolean isVideo() {
        return mediaType == MediaType.VIDEO;
    }
    
    /**
     * Checks if the media processing is complete
     * 
     * @return true if processing is completed
     */
    public boolean isProcessingComplete() {
        return processingStatus == ProcessingStatus.COMPLETED;
    }
    
    /**
     * Gets formatted file size string
     * 
     * @return Formatted file size (e.g., "2.5 MB")
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Gets formatted duration string
     * 
     * @return Formatted duration (e.g., "2:30")
     */
    public String getFormattedDuration() {
        if (durationSeconds == null || durationSeconds <= 0) {
            return null;
        }
        
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        
        if (minutes > 60) {
            int hours = minutes / 60;
            minutes = minutes % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Media type enumeration
     */
    public enum MediaType {
        IMAGE("Image"),
        VIDEO("Video"),
        AUDIO("Audio"),
        DOCUMENT("Document"),
        DIAGRAM("Diagram"),
        ARCHIVE("Archive");
        
        private final String displayName;
        
        MediaType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Processing status enumeration
     */
    public enum ProcessingStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        ProcessingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}