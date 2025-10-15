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
 * Unit tests for PublishMessage Use Case
 * Tests the business logic for publishing messages with proper isolation
 */
class PublishMessageUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    private PublishMessageUseCase publishMessageUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publishMessageUseCase = new PublishMessageUseCase(messageRepository);
    }

    @Nested
    @DisplayName("Successful Message Publishing")
    class SuccessfulMessagePublishing {

        @Test
        @DisplayName("Should publish draft message successfully")
        void should_publish_draft_message_successfully() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message draftMessage = new Message("Test content", "John Doe");

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(draftMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = publishMessageUseCase.execute(messageId);

            // Then
            assertNotNull(result);
            assertEquals(MessageStatus.PUBLISHED, result.getStatus());
            // Note: publishedAt field doesn't exist in current implementation

            verify(messageRepository).findById(messageId);
            verify(messageRepository).save(draftMessage);
        }

        @Test
        @DisplayName("Should return updated message from repository")
        void should_return_updated_message_from_repository() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message draftMessage = new Message("Test content", "John Doe");
            Message savedMessage = new Message("Test content", "John Doe");
            savedMessage.publish();

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(draftMessage));
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

            // When
            Message result = publishMessageUseCase.execute(messageId);

            // Then
            assertSame(savedMessage, result);
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
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // When & Then
            MessageNotFoundException exception = assertThrows(
                MessageNotFoundException.class,
                () -> publishMessageUseCase.execute(messageId)
            );

            assertEquals("non-existent-id", exception.getMessage());
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("Invalid State Transitions")
    class InvalidStateTransitions {

        @Test
        @DisplayName("Should fail to publish already published message")
        void should_fail_to_publish_already_published_message() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message publishedMessage = new Message("Test content", "John Doe");
            publishedMessage.publish(); // Already published

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(publishedMessage));

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> publishMessageUseCase.execute(messageId)
            );

            assertTrue(exception.getMessage().contains("Cannot transition"));
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }

        @Test
        @DisplayName("Should fail to publish deleted message")
        void should_fail_to_publish_deleted_message() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message deletedMessage = new Message("Test content", "John Doe");
            deletedMessage.delete(); // Deleted message

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(deletedMessage));

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> publishMessageUseCase.execute(messageId)
            );

            assertTrue(exception.getMessage().contains("Cannot transition"));
            verify(messageRepository).findById(messageId);
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("Business Logic Delegation")
    class BusinessLogicDelegation {

        @Test
        @DisplayName("Should delegate publishing logic to domain entity")
        void should_delegate_publishing_logic_to_domain_entity() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = spy(new Message("Test content", "John Doe"));

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            publishMessageUseCase.execute(messageId);

            // Then
            verify(message).publish(); // Verify domain method was called
        }

        @Test
        @DisplayName("Should preserve message identity and timestamps")
        void should_preserve_message_identity_and_timestamps() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message originalMessage = new Message("Test content", "John Doe");
            var originalId = originalMessage.getId();
            var originalCreatedAt = originalMessage.getCreatedAt();

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(originalMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = publishMessageUseCase.execute(messageId);

            // Then
            assertEquals(originalId, result.getId());
            assertEquals(originalCreatedAt, result.getCreatedAt());
            assertEquals("Test content", result.getContent());
            assertEquals("John Doe", result.getAuthor());
        }
    }
}
