package com.devsocial.user.dto;

import com.devsocial.user.model.SocialLink;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for SocialLink entity for API communication
 * 
 * This DTO is used for transferring social link data between the API and clients.
 * It includes validation for social link creation and updates.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialLinkDto {
    
    /**
     * Social link ID (for updates)
     */
    private Long id;
    
    /**
     * Social media platform
     */
    @NotNull(message = "Platform is required")
    private SocialLink.SocialPlatform platform;
    
    /**
     * URL to the social media profile
     */
    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;
    
    /**
     * Display name for the link (optional)
     */
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    private String displayName;
    
    /**
     * Whether this link is featured on the profile
     */
    private Boolean isFeatured;
    
    /**
     * Display order for sorting links
     */
    private Integer displayOrder;
    
    /**
     * Link creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Link last update timestamp
     */
    private LocalDateTime updatedAt;
    
    /**
     * Gets the platform display name
     * 
     * @return Platform display name
     */
    public String getPlatformDisplay() {
        return platform != null ? platform.getDisplayName() : "";
    }
    
    /**
     * Gets the platform brand color
     * 
     * @return Platform brand color hex code
     */
    public String getBrandColor() {
        return platform != null ? platform.getBrandColor() : "#6C757D";
    }
    
    /**
     * Gets the platform base URL
     * 
     * @return Platform base URL
     */
    public String getBaseUrl() {
        return platform != null ? platform.getBaseUrl() : "";
    }
    
    /**
     * Gets the display name or falls back to platform name
     * 
     * @return Display name or platform name
     */
    public String getEffectiveDisplayName() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        return getPlatformDisplay();
    }
    
    /**
     * Checks if this link is featured
     * 
     * @return true if link is featured
     */
    public boolean isFeaturedLink() {
        return isFeatured != null && isFeatured;
    }
    
    /**
     * Gets the display order with fallback
     * 
     * @return Display order or 999 for unordered
     */
    public int getEffectiveDisplayOrder() {
        return displayOrder != null ? displayOrder : 999;
    }
    
    /**
     * Validates if the URL matches the platform's expected format
     * 
     * @return true if URL is valid for this platform
     */
    public boolean isValidUrl() {
        return platform != null && platform.isValidUrl(url);
    }
    
    /**
     * Gets a shortened version of the URL for display
     * 
     * @return Shortened URL
     */
    public String getShortUrl() {
        if (url == null || url.length() <= 50) {
            return url;
        }
        return url.substring(0, 47) + "...";
    }
    
    /**
     * Extracts the username/handle from the URL if possible
     * 
     * @return Username or null if cannot be extracted
     */
    public String extractUsername() {
        if (url == null || platform == null) {
            return null;
        }
        
        try {
            String baseUrl = platform.getBaseUrl();
            if (baseUrl.isEmpty()) {
                return null;
            }
            
            // Remove protocol and www
            String cleanUrl = url.toLowerCase()
                    .replaceAll("^https?://", "")
                    .replaceAll("^www\\.", "");
            
            String cleanBase = baseUrl.toLowerCase()
                    .replaceAll("^https?://", "")
                    .replaceAll("^www\\.", "");
            
            if (cleanUrl.startsWith(cleanBase)) {
                String username = cleanUrl.substring(cleanBase.length());
                // Remove trailing slash and query parameters
                username = username.replaceAll("[/?].*$", "");
                return username.isEmpty() ? null : username;
            }
            
        } catch (Exception e) {
            // Ignore extraction errors
        }
        
        return null;
    }
}