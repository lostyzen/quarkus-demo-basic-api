package org.acme.demo.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de l'entité Message
 * Ces tests sont ULTRA rapides car ils ne dépendent d'aucune infrastructure
 */
class MessageTest {

    @Test
    @DisplayName("Création d'un message valide")
    void should_create_message_with_valid_data() {
        // Given
        String content = "Contenu du message";
        String author = "John Doe";

        // When
        Message message = new Message(content, author);

        // Then
        assertNotNull(message.getId());
        assertEquals(content, message.getContent());
        assertEquals(author, message.getAuthor());
        assertEquals(MessageStatus.DRAFT, message.getStatus());
        assertNotNull(message.getCreatedAt());
        assertNotNull(message.getUpdatedAt());
    }

    @Test
    @DisplayName("Échec création message avec contenu vide")
    void should_fail_when_content_is_empty() {
        // Given
        String emptyContent = "";
        String author = "John Doe";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Message(emptyContent, author)
        );
        assertEquals("Le contenu du message ne peut pas être vide", exception.getMessage());
    }

    @Test
    @DisplayName("Échec création message avec auteur vide")
    void should_fail_when_author_is_empty() {
        // Given
        String content = "Contenu valide";
        String emptyAuthor = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Message(content, emptyAuthor)
        );
        assertEquals("L'auteur du message ne peut pas être vide", exception.getMessage());
    }

    @Test
    @DisplayName("Publication d'un message en brouillon")
    void should_publish_draft_message() {
        // Given
        Message message = new Message("Contenu", "Auteur");

        // When
        message.publish();

        // Then
        assertEquals(MessageStatus.PUBLISHED, message.getStatus());
        assertTrue(message.isPublished());
    }

    @Test
    @DisplayName("Échec publication d'un message supprimé")
    void should_fail_to_publish_deleted_message() {
        // Given
        Message message = new Message("Contenu", "Auteur");
        message.delete(); // Message supprimé

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            message::publish
        );
        assertTrue(exception.getMessage().contains("Impossible de publier"));
    }

    @Test
    @DisplayName("Modification du contenu d'un message")
    void should_update_message_content() {
        // Given
        Message message = new Message("Contenu initial", "Auteur");
        String newContent = "Nouveau contenu";

        // When
        message.updateContent(newContent);

        // Then
        assertEquals(newContent, message.getContent());
    }

    @Test
    @DisplayName("Échec modification contenu message supprimé")
    void should_fail_to_update_deleted_message() {
        // Given
        Message message = new Message("Contenu", "Auteur");
        message.delete();

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> message.updateContent("Nouveau contenu")
        );
        assertEquals("Impossible de modifier un message supprimé", exception.getMessage());
    }

    @Test
    @DisplayName("Archivage d'un message publié")
    void should_archive_published_message() {
        // Given
        Message message = new Message("Contenu", "Auteur");
        message.publish();

        // When
        message.archive();

        // Then
        assertEquals(MessageStatus.ARCHIVED, message.getStatus());
    }

    @Test
    @DisplayName("Transitions de statut valides")
    void should_validate_status_transitions() {
        // Test des transitions valides selon les règles métier
        assertTrue(MessageStatus.DRAFT.canTransitionTo(MessageStatus.PUBLISHED));
        assertTrue(MessageStatus.DRAFT.canTransitionTo(MessageStatus.DELETED));
        assertTrue(MessageStatus.PUBLISHED.canTransitionTo(MessageStatus.ARCHIVED));
        assertTrue(MessageStatus.ARCHIVED.canTransitionTo(MessageStatus.PUBLISHED));

        // Transitions invalides
        assertFalse(MessageStatus.DELETED.canTransitionTo(MessageStatus.PUBLISHED));
        assertFalse(MessageStatus.DELETED.canTransitionTo(MessageStatus.ARCHIVED));
    }
}
