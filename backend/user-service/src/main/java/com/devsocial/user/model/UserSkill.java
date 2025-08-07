package com.devsocial.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Skill entity representing technical skills and proficiency levels
 * 
 * This entity stores information about user's technical skills including:
 * - Skill name and category
 * - Proficiency level (1-5 scale)
 * - Years of experience
 * - Endorsements from other users
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "user_skills", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "skill_name"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill {
    
    /**
     * Primary key for the skill
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
     * Name of the skill (e.g., "Java", "React", "Docker")
     */
    @NotBlank(message = "Skill name is required")
    @Size(max = 50, message = "Skill name must not exceed 50 characters")
    @Column(name = "skill_name", nullable = false)
    private String skillName;
    
    /**
     * Category of the skill
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;
    
    /**
     * Proficiency level (1-5 scale)
     * 1 = Beginner, 2 = Novice, 3 = Intermediate, 4 = Advanced, 5 = Expert
     */
    @Min(value = 1, message = "Proficiency level must be between 1 and 5")
    @Max(value = 5, message = "Proficiency level must be between 1 and 5")
    @Column(nullable = false)
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
    @Builder.Default
    private Integer endorsements = 0;
    
    /**
     * Whether this skill is featured on the profile
     */
    @Builder.Default
    private Boolean isFeatured = false;
    
    /**
     * Timestamp when the skill was added
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the skill was last updated
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
     * Gets the proficiency level as a descriptive string
     * 
     * @return Proficiency level description
     */
    public String getProficiencyDescription() {
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
     * Skill category enumeration
     */
    public enum SkillCategory {
        PROGRAMMING_LANGUAGE("Programming Language"),
        FRAMEWORK("Framework"),
        DATABASE("Database"),
        CLOUD("Cloud Platform"),
        DEVOPS("DevOps"),
        MOBILE("Mobile Development"),
        WEB("Web Development"),
        DATA_SCIENCE("Data Science"),
        MACHINE_LEARNING("Machine Learning"),
        DESIGN("Design"),
        PROJECT_MANAGEMENT("Project Management"),
        OTHER("Other");
        
        private final String displayName;
        
        SkillCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}