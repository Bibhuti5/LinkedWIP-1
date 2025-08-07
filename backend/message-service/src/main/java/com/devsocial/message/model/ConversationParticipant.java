package com.devsocial.message.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ConversationParticipant entity representing users in conversations
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "conversation_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}),
       indexes = {
           @Index(name = "idx_conversation_id", columnList = "conversation_id"),
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_joined_at", columnList = "joined_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isMuted = false;
    
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
    
    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
    
    public enum ParticipantRole {
        OWNER, ADMIN, MODERATOR, MEMBER
    }
}