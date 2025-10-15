package io.lostyzen.demo.domain.service;

import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateMessage Use Case
 * Tests content update functionality with proper business rule validation
 */
class UpdateMessageUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    private UpdateMessageUseCase updateMessageUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateMessageUseCase = new UpdateMessageUseCase(messageRepository);
    }

    @Nested
    @DisplayName("Successful Content Update")
    class SuccessfulContentUpdate {

        @Test
        @DisplayName("Should update draft message content successfully")
        void should_update_draft_message_content_successfully() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message draftMessage = new Message("Original content", "John Doe");
            String newContent = "Updated content";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(draftMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = updateMessageUseCase.execute(messageId, newContent);

            // Then
            assertNotNull(result);
            assertEquals(newContent, result.getContent());
            assertEquals("John Doe", result.getAuthor());
            assertEquals(MessageStatus.DRAFT, result.getStatus());

            verify(messageRepository).findById(messageId);
            verify(messageRepository).save(draftMessage);
        }

        @Test
        @DisplayName("Should update published message content successfully")
        void should_update_published_message_content_successfully() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message publishedMessage = new Message("Original content", "John Doe");
            publishedMessage.publish();
            String newContent = "Updated content";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(publishedMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = updateMessageUseCase.execute(messageId, newContent);

            // Then
            assertNotNull(result);
            assertEquals(newContent, result.getContent());
            assertEquals(MessageStatus.PUBLISHED, result.getStatus());
        }

        @Test
        @DisplayName("Should trim whitespace from new content")
        void should_trim_whitespace_from_new_content() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = new Message("Original content", "John Doe");
            String contentWithSpaces = "  Updated content  ";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = updateMessageUseCase.execute(messageId, contentWithSpaces);

            // Then
            assertEquals("Updated content", result.getContent());
        }
    }

    @Nested
    @DisplayName("Message Not Found")
    class MessageNotFound {

        @Test
        @DisplayName("Should throw exception when message does not exist")
        void should_throw_exception_when_message_not_found() {
            // Given
            MessageId messageId = MessageId.of("non-existent-id");
            String newContent = "New content";
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // When & Then
            MessageNotFoundException exception = assertThrows(
                MessageNotFoundException.class,
                () -> updateMessageUseCase.execute(messageId, newContent)
            );

            assertEquals("non-existent-id", exception.getMessage());
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("Invalid Content Update")
    class InvalidContentUpdate {

        @Test
        @DisplayName("Should fail to update deleted message")
        void should_fail_to_update_deleted_message() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message deletedMessage = new Message("Original content", "John Doe");
            deletedMessage.delete();
            String newContent = "New content";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(deletedMessage));

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> updateMessageUseCase.execute(messageId, newContent)
            );

            assertEquals("Cannot modify deleted message", exception.getMessage());
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }

        @Test
        @DisplayName("Should fail when new content is empty")
        void should_fail_when_new_content_is_empty() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = new Message("Original content", "John Doe");
            String emptyContent = "";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateMessageUseCase.execute(messageId, emptyContent)
            );

            assertEquals("Message content cannot be empty", exception.getMessage());
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }

        @Test
        @DisplayName("Should fail when new content is null")
        void should_fail_when_new_content_is_null() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = new Message("Original content", "John Doe");
            String nullContent = null;

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateMessageUseCase.execute(messageId, nullContent)
            );

            assertEquals("Message content cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Business Logic Delegation")
    class BusinessLogicDelegation {

        @Test
        @DisplayName("Should delegate content update to domain entity")
        void should_delegate_content_update_to_domain_entity() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = spy(new Message("Original content", "John Doe"));
            String newContent = "New content";

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            updateMessageUseCase.execute(messageId, newContent);

            // Then
            verify(message).updateContent(newContent);
        }
    }
}
