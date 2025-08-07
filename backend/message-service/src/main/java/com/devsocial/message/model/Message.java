package com.devsocial.message.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Message entity representing chat messages between users
 * 
 * This entity supports various message types including:
 * - Text messages with emoji support
 * - Media messages (images, files, videos)
 * - System messages (user joined, left, etc.)
 * - Reply messages (threaded conversations)
 * 
 * Features:
 * - Message delivery status tracking
 * - Read receipts and timestamps
 * - Message editing and deletion
 * - Reaction support (emojis)
 * - Message threading for replies
 * - File attachments and media
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_message_type", columnList = "message_type"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    /**
     * Primary key for the message
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the conversation this message belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    /**
     * ID of the user who sent the message
     */
    @NotNull(message = "Sender ID is required")
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    /**
     * ID of the user who should receive the message
     */
    @NotNull(message = "Recipient ID is required")
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;
    
    /**
     * Type of message
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;
    
    /**
     * Message content (text, file URL, etc.)
     */
    @NotBlank(message = "Message content is required")
    @Size(max = 4000, message = "Message content must not exceed 4000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    /**
     * Original message content (for edited messages)
     */
    @Size(max = 4000, message = "Original content must not exceed 4000 characters")
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;
    
    /**
     * Message delivery and read status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;
    
    /**
     * Priority level of the message
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessagePriority priority = MessagePriority.NORMAL;
    
    /**
     * Whether the message has been edited
     */
    @Builder.Default
    private Boolean isEdited = false;
    
    /**
     * Whether the message has been deleted
     */
    @Builder.Default
    private Boolean isDeleted = false;
    
    /**
     * Whether the message is pinned in the conversation
     */
    @Builder.Default
    private Boolean isPinned = false;
    
    /**
     * Parent message for replies (threading)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;
    
    /**
     * Replies to this message
     */
    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> replies;
    
    /**
     * Message attachments (files, images, etc.)
     */
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageAttachment> attachments;
    
    /**
     * Message reactions (emojis)
     */
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageReaction> reactions;
    
    /**
     * Metadata for special message types (JSON format)
     */
    @Size(max = 2000, message = "Metadata must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * Timestamp when the message was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the message was last updated
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Timestamp when the message was delivered
     */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    /**
     * Timestamp when the message was read
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * Timestamp when the message was edited
     */
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
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
        if (this.isEdited && this.editedAt == null) {
            this.editedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Checks if the message is a reply to another message
     * 
     * @return true if this is a reply message
     */
    public boolean isReply() {
        return parentMessage != null;
    }
    
    /**
     * Checks if the message has replies
     * 
     * @return true if this message has replies
     */
    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }
    
    /**
     * Checks if the message has attachments
     * 
     * @return true if this message has attachments
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }
    
    /**
     * Checks if the message has reactions
     * 
     * @return true if this message has reactions
     */
    public boolean hasReactions() {
        return reactions != null && !reactions.isEmpty();
    }
    
    /**
     * Checks if the message has been read
     * 
     * @return true if the message has been read
     */
    public boolean isRead() {
        return status == MessageStatus.READ;
    }
    
    /**
     * Checks if the message has been delivered
     * 
     * @return true if the message has been delivered
     */
    public boolean isDelivered() {
        return status == MessageStatus.DELIVERED || status == MessageStatus.READ;
    }
    
    /**
     * Gets the reply count
     * 
     * @return Number of replies to this message
     */
    public int getReplyCount() {
        return replies != null ? replies.size() : 0;
    }
    
    /**
     * Gets the reaction count
     * 
     * @return Number of reactions to this message
     */
    public int getReactionCount() {
        return reactions != null ? reactions.size() : 0;
    }
    
    /**
     * Message type enumeration
     */
    public enum MessageType {
        TEXT("Text Message"),
        IMAGE("Image"),
        VIDEO("Video"),
        AUDIO("Audio"),
        FILE("File"),
        LOCATION("Location"),
        SYSTEM("System Message"),
        CALL("Call"),
        STICKER("Sticker"),
        GIF("GIF");
        
        private final String displayName;
        
        MessageType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Message status enumeration
     */
    public enum MessageStatus {
        SENT("Sent"),
        DELIVERED("Delivered"),
        READ("Read"),
        FAILED("Failed");
        
        private final String displayName;
        
        MessageStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Message priority enumeration
     */
    public enum MessagePriority {
        LOW("Low"),
        NORMAL("Normal"),
        HIGH("High"),
        URGENT("Urgent");
        
        private final String displayName;
        
        MessagePriority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}