package org.acme.demo.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Message entity
 * These tests are ULTRA fast as they don't depend on any infrastructure
 */
class MessageTest {

    @Nested
    @DisplayName("Message Creation")
    class MessageCreation {

        @Test
        @DisplayName("Should create message with valid data")
        void should_create_message_with_valid_data() {
            // Given
            String content = "Valid message content";
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
        @DisplayName("Should fail when content is empty")
        void should_fail_when_content_is_empty() {
            // Given
            String emptyContent = "";
            String author = "John Doe";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Message(emptyContent, author)
            );
            assertEquals("Message content cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail when content is null")
        void should_fail_when_content_is_null() {
            // Given
            String nullContent = null;
            String author = "John Doe";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Message(nullContent, author)
            );
            assertEquals("Message content cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail when author is empty")
        void should_fail_when_author_is_empty() {
            // Given
            String content = "Valid content";
            String emptyAuthor = "";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Message(content, emptyAuthor)
            );
            assertEquals("Author cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail when author is null")
        void should_fail_when_author_is_null() {
            // Given
            String content = "Valid content";
            String nullAuthor = null;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Message(content, nullAuthor)
            );
            assertEquals("Author cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should trim content when creating message")
        void should_trim_content_when_creating_message() {
            // Given
            String contentWithSpaces = "  Content with spaces  ";
            String author = "John Doe";

            // When
            Message message = new Message(contentWithSpaces, author);

            // Then
            assertEquals("Content with spaces", message.getContent());
        }
    }

    @Nested
    @DisplayName("Message Status Transitions")
    class MessageStatusTransitions {

        @Test
        @DisplayName("Should publish draft message")
        void should_publish_draft_message() {
            // Given
            Message message = new Message("Test content", "John Doe");
            assertEquals(MessageStatus.DRAFT, message.getStatus());

            // When
            message.publish();

            // Then
            assertEquals(MessageStatus.PUBLISHED, message.getStatus());
            // Note: publishedAt field doesn't exist in current implementation
        }

        @Test
        @DisplayName("Should fail to publish already published message")
        void should_fail_to_publish_already_published_message() {
            // Given
            Message message = new Message("Test content", "John Doe");
            message.publish();

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                message::publish
            );
            assertTrue(exception.getMessage().contains("Cannot transition"));
        }

        @Test
        @DisplayName("Should delete draft message")
        void should_delete_draft_message() {
            // Given
            Message message = new Message("Test content", "John Doe");

            // When
            message.delete();

            // Then
            assertEquals(MessageStatus.DELETED, message.getStatus());
        }

        @Test
        @DisplayName("Should fail to delete already deleted message")
        void should_fail_to_delete_already_deleted_message() {
            // Given
            Message message = new Message("Test content", "John Doe");
            message.delete();

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                message::delete
            );
            assertTrue(exception.getMessage().contains("Cannot transition"));
        }

        @Test
        @DisplayName("Should archive published message")
        void should_archive_published_message() {
            // Given
            Message message = new Message("Test content", "John Doe");
            message.publish();

            // When
            message.archive();

            // Then
            assertEquals(MessageStatus.ARCHIVED, message.getStatus());
        }
    }

    @Nested
    @DisplayName("Message Content Update")
    class MessageContentUpdate {

        @Test
        @DisplayName("Should update content of draft message")
        void should_update_content_of_draft_message() {
            // Given
            Message message = new Message("Original content", "John Doe");
            String newContent = "Updated content";

            // When
            message.updateContent(newContent);

            // Then
            assertEquals(newContent, message.getContent());
        }

        @Test
        @DisplayName("Should update content of published message")
        void should_update_content_of_published_message() {
            // Given
            Message message = new Message("Original content", "John Doe");
            message.publish();
            String newContent = "Updated content";

            // When
            message.updateContent(newContent);

            // Then
            assertEquals(newContent, message.getContent());
        }

        @Test
        @DisplayName("Should fail to update content of deleted message")
        void should_fail_to_update_content_of_deleted_message() {
            // Given
            Message message = new Message("Original content", "John Doe");
            message.delete();
            String newContent = "Updated content";

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> message.updateContent(newContent)
            );
            assertEquals("Cannot modify deleted message", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail to update with empty content")
        void should_fail_to_update_with_empty_content() {
            // Given
            Message message = new Message("Original content", "John Doe");
            String emptyContent = "";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> message.updateContent(emptyContent)
            );
            assertEquals("Message content cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should trim content when updating")
        void should_trim_content_when_updating() {
            // Given
            Message message = new Message("Original content", "John Doe");
            String contentWithSpaces = "  Updated content  ";

            // When
            message.updateContent(contentWithSpaces);

            // Then
            assertEquals("Updated content", message.getContent());
        }
    }

    @Nested
    @DisplayName("Business Rules Validation")
    class BusinessRulesValidation {

        @Test
        @DisplayName("Should enforce maximum content length")
        void should_enforce_maximum_content_length() {
            // Given
            String tooLongContent = "a".repeat(1001); // Assuming max is 1000
            String author = "John Doe";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Message(tooLongContent, author)
            );
            assertTrue(exception.getMessage().contains("too long"));
        }

        @Test
        @DisplayName("Should accept content at maximum length")
        void should_accept_content_at_maximum_length() {
            // Given
            String maxLengthContent = "a".repeat(1000); // Assuming max is 1000
            String author = "John Doe";

            // When & Then
            assertDoesNotThrow(() -> new Message(maxLengthContent, author));
        }

        @Test
        @DisplayName("Should generate unique IDs for different messages")
        void should_generate_unique_ids_for_different_messages() {
            // Given & When
            Message message1 = new Message("Content 1", "Author 1");
            Message message2 = new Message("Content 2", "Author 2");

            // Then
            assertNotEquals(message1.getId(), message2.getId());
        }

        @Test
        @DisplayName("Should update timestamp when content changes")
        void should_update_timestamp_when_content_changes() {
            // Given
            Message message = new Message("Original content", "John Doe");
            var originalUpdatedAt = message.getUpdatedAt();

            // Small delay to ensure timestamp difference
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // When
            message.updateContent("New content");

            // Then
            assertTrue(message.getUpdatedAt().isAfter(originalUpdatedAt));
        }
    }
}
