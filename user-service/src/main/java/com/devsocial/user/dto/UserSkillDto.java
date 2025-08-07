package com.devsocial.user.dto;

import com.devsocial.user.model.UserSkill;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for UserSkill entity for API communication
 * 
 * This DTO is used for transferring user skill data between the API and clients.
 * It includes validation for skill creation and updates.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSkillDto {
    
    /**
     * Skill ID (for updates)
     */
    private Long id;
    
    /**
     * Name of the skill
     */
    @NotBlank(message = "Skill name is required")
    @Size(max = 50, message = "Skill name must not exceed 50 characters")
    private String skillName;
    
    /**
     * Category of the skill
     */
    @NotNull(message = "Skill category is required")
    private UserSkill.SkillCategory category;
    
    /**
     * Proficiency level (1-5)
     */
    @NotNull(message = "Proficiency level is required")
    @Min(value = 1, message = "Proficiency level must be between 1 and 5")
    @Max(value = 5, message = "Proficiency level must be between 1 and 5")
    private Integer proficiencyLevel;
    
    /**
     * Years of experience with this skill
     */
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;
    
    /**
     * Number of endorsements from other users
     */
    private Integer endorsements;
    
    /**
     * Whether this skill is featured on the profile
     */
    private Boolean isFeatured;
    
    /**
     * Skill creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Skill last update timestamp
     */
    private LocalDateTime updatedAt;
    
    /**
     * Gets the proficiency level as a descriptive string
     * 
     * @return Proficiency level description
     */
    public String getProficiencyDescription() {
        if (proficiencyLevel == null) {
            return "Unknown";
        }
        return switch (proficiencyLevel) {
            case 1 -> "Beginner";
            case 2 -> "Novice";
            case 3 -> "Intermediate";
            case 4 -> "Advanced";
            case 5 -> "Expert";
            default -> "Unknown";
        };
    }
    
    /**
     * Gets the category display name
     * 
     * @return Category display name
     */
    public String getCategoryDisplay() {
        return category != null ? category.getDisplayName() : "";
    }
    
    /**
     * Gets the endorsement count with fallback
     * 
     * @return Endorsement count or 0
     */
    public int getEndorsementCount() {
        return endorsements != null ? endorsements : 0;
    }
    
    /**
     * Gets the years of experience with fallback
     * 
     * @return Years of experience or 0
     */
    public int getExperienceYears() {
        return yearsOfExperience != null ? yearsOfExperience : 0;
    }
    
    /**
     * Checks if this skill is featured
     * 
     * @return true if skill is featured
     */
    public boolean isFeaturedSkill() {
        return isFeatured != null && isFeatured;
    }
    
    /**
     * Checks if this skill has endorsements
     * 
     * @return true if skill has endorsements
     */
    public boolean hasEndorsements() {
        return getEndorsementCount() > 0;
    }
    
    /**
     * Calculates a skill score based on proficiency and experience
     * 
     * @return Skill score for ranking purposes
     */
    public int getSkillScore() {
        int score = 0;
        if (proficiencyLevel != null) {
            score += proficiencyLevel * 20;
        }
        if (yearsOfExperience != null) {
            score += Math.min(yearsOfExperience * 5, 50); // Cap experience bonus at 50
        }
        if (endorsements != null) {
            score += Math.min(endorsements * 2, 20); // Cap endorsement bonus at 20
        }
        return score;
    }
}