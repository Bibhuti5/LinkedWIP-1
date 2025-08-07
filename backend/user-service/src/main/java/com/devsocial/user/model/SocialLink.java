package com.devsocial.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Social Link entity representing user's social media and professional links
 * 
 * This entity stores various social media and professional platform links
 * for the user's profile, including LinkedIn, Twitter, personal websites, etc.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "social_links",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "platform"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLink {
    
    /**
     * Primary key for the social link
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the user profile
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
    
    /**
     * Social media platform
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialPlatform platform;
    
    /**
     * URL to the social media profile
     */
    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    @Column(nullable = false)
    private String url;
    
    /**
     * Display name for the link (optional)
     */
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    private String displayName;
    
    /**
     * Whether this link is featured on the profile
     */
    @Builder.Default
    private Boolean isFeatured = false;
    
    /**
     * Display order for sorting links
     */
    private Integer displayOrder;
    
    /**
     * Timestamp when the link was added
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the link was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Sets timestamps before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * Updates the updatedAt timestamp before updating in database
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Social platform enumeration
     */
    public enum SocialPlatform {
        LINKEDIN("LinkedIn", "https://linkedin.com/in/", "#0077B5"),
        TWITTER("Twitter", "https://twitter.com/", "#1DA1F2"),
        GITHUB("GitHub", "https://github.com/", "#333333"),
        STACKOVERFLOW("Stack Overflow", "https://stackoverflow.com/users/", "#F58025"),
        DRIBBBLE("Dribbble", "https://dribbble.com/", "#EA4C89"),
        BEHANCE("Behance", "https://behance.net/", "#1769FF"),
        MEDIUM("Medium", "https://medium.com/@", "#00AB6C"),
        DEV_TO("Dev.to", "https://dev.to/", "#0A0A0A"),
        YOUTUBE("YouTube", "https://youtube.com/", "#FF0000"),
        TWITCH("Twitch", "https://twitch.tv/", "#9146FF"),
        DISCORD("Discord", "https://discord.gg/", "#5865F2"),
        TELEGRAM("Telegram", "https://t.me/", "#0088CC"),
        WEBSITE("Personal Website", "", "#6C757D"),
        BLOG("Blog", "", "#6C757D"),
        PORTFOLIO("Portfolio", "", "#6C757D"),
        OTHER("Other", "", "#6C757D");
        
        private final String displayName;
        private final String baseUrl;
        private final String brandColor;
        
        SocialPlatform(String displayName, String baseUrl, String brandColor) {
            this.displayName = displayName;
            this.baseUrl = baseUrl;
            this.brandColor = brandColor;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public String getBrandColor() {
            return brandColor;
        }
        
        /**
         * Validates if the URL matches the platform's expected format
         * 
         * @param url The URL to validate
         * @return true if the URL is valid for this platform
         */
        public boolean isValidUrl(String url) {
            if (url == null || url.trim().isEmpty()) {
                return false;
            }
            
            // For platforms with specific base URLs, check if URL contains the base
            if (!baseUrl.isEmpty()) {
                return url.toLowerCase().contains(baseUrl.toLowerCase()) ||
                       url.toLowerCase().contains(this.name().toLowerCase());
            }
            
            // For generic platforms, just check if it's a valid URL format
            return url.matches("^https?://.*");
        }
    }
}