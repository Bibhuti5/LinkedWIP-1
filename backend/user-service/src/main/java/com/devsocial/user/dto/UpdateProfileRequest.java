package com.devsocial.user.dto;

import com.devsocial.user.model.UserProfile;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for profile update requests
 * 
 * This DTO is used for updating user profile information.
 * It includes validation and allows partial updates.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    /**
     * User's professional bio/description
     */
    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;
    
    /**
     * User's current job title
     */
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;
    
    /**
     * User's current company
     */
    @Size(max = 100, message = "Company must not exceed 100 characters")
    private String company;
    
    /**
     * User's location
     */
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    /**
     * User's website URL
     */
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;
    
    /**
     * URL to user's profile picture
     */
    @Size(max = 500, message = "Profile picture URL must not exceed 500 characters")
    private String profilePictureUrl;
    
    /**
     * URL to user's cover/banner image
     */
    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;
    
    /**
     * GitHub username for integration
     */
    @Size(max = 50, message = "GitHub username must not exceed 50 characters")
    private String githubUsername;
    
    /**
     * Whether the profile is public
     */
    private Boolean isPublic;
    
    /**
     * Whether the user is available for work
     */
    private Boolean isAvailableForWork;
    
    /**
     * User's experience level
     */
    private UserProfile.ExperienceLevel experienceLevel;
    
    /**
     * Checks if any field has been provided for update
     * 
     * @return true if at least one field is not null
     */
    public boolean hasUpdates() {
        return bio != null || jobTitle != null || company != null || 
               location != null || website != null || profilePictureUrl != null ||
               coverImageUrl != null || githubUsername != null || 
               isPublic != null || isAvailableForWork != null || 
               experienceLevel != null;
    }
    
    /**
     * Checks if GitHub username is being updated
     * 
     * @return true if GitHub username is provided
     */
    public boolean isUpdatingGitHub() {
        return githubUsername != null;
    }
    
    /**
     * Checks if profile visibility is being changed
     * 
     * @return true if isPublic is provided
     */
    public boolean isUpdatingVisibility() {
        return isPublic != null;
    }
    
    /**
     * Checks if work availability is being changed
     * 
     * @return true if isAvailableForWork is provided
     */
    public boolean isUpdatingWorkAvailability() {
        return isAvailableForWork != null;
    }
    
    /**
     * Validates that provided URLs are properly formatted
     * 
     * @return true if all provided URLs are valid
     */
    public boolean hasValidUrls() {
        return isValidUrl(website) && 
               isValidUrl(profilePictureUrl) && 
               isValidUrl(coverImageUrl);
    }
    
    /**
     * Helper method to validate URL format
     * 
     * @param url URL to validate
     * @return true if URL is null or properly formatted
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true;
        }
        return url.matches("^https?://.*");
    }
}