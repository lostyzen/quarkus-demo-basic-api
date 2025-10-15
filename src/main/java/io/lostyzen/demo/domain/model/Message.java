package io.lostyzen.demo.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Business entity Message with business logic
 * Uses Lombok @Getter to reduce boilerplate code while preserving immutability
 */
@Getter
public class Message {
    private final MessageId id;
    private String content;
    private MessageStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime deletedAt;
    private String author;

    // Constructor for creating a new message
    public Message(String content, String author) {
        this(MessageId.generate(), content, MessageStatus.DRAFT, author, LocalDateTime.now(), LocalDateTime.now(), null, null);
    }

    // Complete constructor (for reconstruction from persistence)
    public Message(MessageId id, String content, MessageStatus status, String author,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, content, status, author, createdAt, updatedAt, null, null);
    }

    // Complete constructor with publishedAt (for reconstruction from persistence)
    public Message(MessageId id, String content, MessageStatus status, String author,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt) {
        this(id, content, status, author, createdAt, updatedAt, publishedAt, null);
    }

    // Complete constructor with publishedAt and deletedAt (for reconstruction from persistence)
    public Message(MessageId id, String content, MessageStatus status, String author,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt, LocalDateTime deletedAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update date cannot be null");
        this.publishedAt = publishedAt; // Can be null for non-published messages
        this.deletedAt = deletedAt; // Can be null for non-deleted messages

        this.setContent(content);
        this.setAuthor(author);
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    // Business logic: publish a message
    public void publish() {
        if (!status.canTransitionTo(MessageStatus.PUBLISHED)) {
            throw new IllegalStateException(
                "Cannot transition from " + status.getDisplayName() + " to PUBLISHED");
        }
        this.status = MessageStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now(); // Set publication timestamp
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic: archive a message
    public void archive() {
        if (!status.canTransitionTo(MessageStatus.ARCHIVED)) {
            throw new IllegalStateException(
                "Cannot transition from " + status.getDisplayName() + " to ARCHIVED");
        }
        this.status = MessageStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic: delete a message
    public void delete() {
        if (!status.canTransitionTo(MessageStatus.DELETED)) {
            throw new IllegalStateException(
                "Cannot transition from " + status.getDisplayName() + " to DELETED");
        }
        this.status = MessageStatus.DELETED;
        this.deletedAt = LocalDateTime.now(); // Set deletion timestamp
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic: update content
    public void updateContent(String newContent) {
        if (this.status == MessageStatus.DELETED) {
            throw new IllegalStateException("Cannot modify deleted message");
        }
        this.setContent(newContent);
        this.updatedAt = LocalDateTime.now();
    }

    // Business validation for content
    private void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Message content is too long (max 1000 characters)");
        }
        this.content = content.trim();
    }

    // Business validation for author
    private void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        this.author = author.trim();
    }

    // Utility methods
    public boolean isPublished() {
        return this.status == MessageStatus.PUBLISHED;
    }

    public boolean isDeleted() {
        return this.status == MessageStatus.DELETED;
    }

    // Equals and hashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", author='" + author + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
