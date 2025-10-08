package org.acme.demo.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité métier Message avec sa logique business
 */
public class Message {
    private final MessageId id;
    private String content;
    private MessageStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String author;

    // Constructeur pour création d'un nouveau message
    public Message(String content, String author) {
        this(MessageId.generate(), content, MessageStatus.DRAFT, author, LocalDateTime.now(), LocalDateTime.now());
    }

    // Constructeur complet (pour reconstruction depuis la persistance)
    public Message(MessageId id, String content, MessageStatus status, String author,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "L'ID ne peut pas être null");
        this.createdAt = Objects.requireNonNull(createdAt, "La date de création ne peut pas être null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "La date de mise à jour ne peut pas être null");

        this.setContent(content);
        this.setAuthor(author);
        this.status = Objects.requireNonNull(status, "Le statut ne peut pas être null");
    }

    // Logique métier : publication d'un message
    public void publish() {
        if (!status.canTransitionTo(MessageStatus.PUBLISHED)) {
            throw new IllegalStateException(
                "Impossible de publier un message avec le statut : " + status.getDisplayName());
        }
        this.status = MessageStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    // Logique métier : archivage d'un message
    public void archive() {
        if (!status.canTransitionTo(MessageStatus.ARCHIVED)) {
            throw new IllegalStateException(
                "Impossible d'archiver un message avec le statut : " + status.getDisplayName());
        }
        this.status = MessageStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    // Logique métier : suppression d'un message
    public void delete() {
        if (!status.canTransitionTo(MessageStatus.DELETED)) {
            throw new IllegalStateException(
                "Impossible de supprimer un message avec le statut : " + status.getDisplayName());
        }
        this.status = MessageStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    // Logique métier : modification du contenu
    public void updateContent(String newContent) {
        if (this.status == MessageStatus.DELETED) {
            throw new IllegalStateException("Impossible de modifier un message supprimé");
        }
        this.setContent(newContent);
        this.updatedAt = LocalDateTime.now();
    }

    // Validation métier du contenu
    private void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas dépasser 1000 caractères");
        }
        this.content = content.trim();
    }

    // Validation métier de l'auteur
    private void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur du message ne peut pas être vide");
        }
        this.author = author.trim();
    }

    // Méthode utilitaire
    public boolean isPublished() {
        return this.status == MessageStatus.PUBLISHED;
    }

    public boolean isDeleted() {
        return this.status == MessageStatus.DELETED;
    }

    // Getters
    public MessageId getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getAuthor() {
        return author;
    }

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
                '}';
    }
}
