package org.acme.demo.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.model.MessageStatus;

import java.time.LocalDateTime;

/**
 * Entité JPA pour la persistance des messages
 * Cette classe fait partie de la couche infrastructure
 */
@Entity
@Table(name = "messages")
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

    // Constructeur par défaut pour JPA
    public MessageEntity() {}

    // Constructeur depuis le modèle de domaine
    public MessageEntity(Message message) {
        this.id = message.getId().getValue();
        this.content = message.getContent();
        this.status = message.getStatus();
        this.author = message.getAuthor();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }

    // Conversion vers le modèle de domaine
    public Message toDomainModel() {
        return new Message(
            MessageId.of(this.id),
            this.content,
            this.status,
            this.author,
            this.createdAt,
            this.updatedAt
        );
    }

    // Mise à jour depuis le modèle de domaine
    public void updateFromDomainModel(Message message) {
        this.content = message.getContent();
        this.status = message.getStatus();
        this.author = message.getAuthor();
        this.updatedAt = message.getUpdatedAt();
    }
}
