package com.devsocial.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Follow entity representing follower/following relationships
 * 
 * This entity manages the social connections between users on the platform.
 * It tracks who follows whom and when the relationship was established.
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "user_follows",
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}),
       indexes = {
           @Index(name = "idx_follower", columnList = "follower_id"),
           @Index(name = "idx_following", columnList = "following_id"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow {
    
    /**
     * Primary key for the follow relationship
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID of the user who is following (the follower)
     */
    @Column(name = "follower_id", nullable = false)
    private Long followerId;
    
    /**
     * ID of the user being followed (the following)
     */
    @Column(name = "following_id", nullable = false)
    private Long followingId;
    
    /**
     * Timestamp when the follow relationship was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Sets timestamp before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Checks if this is a self-follow (which should not be allowed)
     * 
     * @return true if user is trying to follow themselves
     */
    public boolean isSelfFollow() {
        return followerId != null && followerId.equals(followingId);
    }
}