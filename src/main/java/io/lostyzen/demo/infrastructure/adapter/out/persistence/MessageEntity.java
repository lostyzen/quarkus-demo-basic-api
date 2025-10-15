package io.lostyzen.demo.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;

import java.time.LocalDateTime;

/**
 * JPA entity for message persistence
 * This class is part of the infrastructure layer
 * Uses Lombok to reduce boilerplate code
 */
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MessageEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id", length = 36)
    public String id;

    @Column(name = "content", length = 1000, nullable = false)
    public String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public MessageStatus status;

    @Column(name = "author", nullable = false)
    public String author;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Column(name = "published_at")
    public LocalDateTime publishedAt; // Nullable because DRAFT messages don't have publication date

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt; // Nullable because non-deleted messages don't have deletion date

    // Constructor from domain model
    public MessageEntity(Message message) {
        this.id = message.getId().getValue();
        this.content = message.getContent();
        this.status = message.getStatus();
        this.author = message.getAuthor();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
        this.publishedAt = message.getPublishedAt();
        this.deletedAt = message.getDeletedAt();
    }

    // Convert to domain model
    public Message toDomainModel() {
        return new Message(
            MessageId.of(this.id),
            this.content,
            this.status,
            this.author,
            this.createdAt,
            this.updatedAt,
            this.publishedAt,
            this.deletedAt
        );
    }

    // Update from domain model
    public void updateFromDomainModel(Message message) {
        this.content = message.getContent();
        this.status = message.getStatus();
        this.author = message.getAuthor();
        this.updatedAt = message.getUpdatedAt();
        this.publishedAt = message.getPublishedAt();
        this.deletedAt = message.getDeletedAt();
    }
}
