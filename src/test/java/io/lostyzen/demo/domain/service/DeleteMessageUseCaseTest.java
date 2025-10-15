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
 * Unit tests for DeleteMessage Use Case
 * Tests logical deletion functionality with proper business rule validation
 */
class DeleteMessageUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    private DeleteMessageUseCase deleteMessageUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteMessageUseCase = new DeleteMessageUseCase(messageRepository);
    }

    @Nested
    @DisplayName("Successful Message Deletion")
    class SuccessfulMessageDeletion {

        @Test
        @DisplayName("Should delete draft message successfully")
        void should_delete_draft_message_successfully() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message draftMessage = new Message("Test content", "John Doe");

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(draftMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            deleteMessageUseCase.execute(messageId);

            // Then
            assertEquals(MessageStatus.DELETED, draftMessage.getStatus());
            verify(messageRepository).findById(messageId);
            verify(messageRepository).save(draftMessage);
        }

        @Test
        @DisplayName("Should delete published message successfully")
        void should_delete_published_message_successfully() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message publishedMessage = new Message("Test content", "John Doe");
            publishedMessage.publish();

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(publishedMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            deleteMessageUseCase.execute(messageId);

            // Then
            assertEquals(MessageStatus.DELETED, publishedMessage.getStatus());
        }

        @Test
        @DisplayName("Should perform logical deletion not physical")
        void should_perform_logical_deletion_not_physical() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = new Message("Test content", "John Doe");
            var originalId = message.getId();
            var originalContent = message.getContent();

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            deleteMessageUseCase.execute(messageId);

            // Then
            // Message still exists with same identity and content
            assertEquals(originalId, message.getId());
            assertEquals(originalContent, message.getContent());
            assertEquals(MessageStatus.DELETED, message.getStatus());

            // Verify no physical deletion was attempted
            verify(messageRepository, never()).deleteById(any(MessageId.class));
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
                () -> deleteMessageUseCase.execute(messageId)
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
        @DisplayName("Should fail to delete already deleted message")
        void should_fail_to_delete_already_deleted_message() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message deletedMessage = new Message("Test content", "John Doe");
            deletedMessage.delete(); // Already deleted

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(deletedMessage));

            // When & Then
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> deleteMessageUseCase.execute(messageId)
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
        @DisplayName("Should delegate deletion logic to domain entity")
        void should_delegate_deletion_logic_to_domain_entity() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message message = spy(new Message("Test content", "John Doe"));

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            deleteMessageUseCase.execute(messageId);

            // Then
            verify(message).delete(); // Verify domain method was called
        }

        @Test
        @DisplayName("Should preserve all message data except status")
        void should_preserve_all_message_data_except_status() {
            // Given
            MessageId messageId = MessageId.of("test-id");
            Message originalMessage = new Message("Test content", "John Doe");
            var originalId = originalMessage.getId();
            var originalContent = originalMessage.getContent();
            var originalAuthor = originalMessage.getAuthor();
            var originalCreatedAt = originalMessage.getCreatedAt();

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(originalMessage));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            deleteMessageUseCase.execute(messageId);

            // Then
            assertEquals(originalId, originalMessage.getId());
            assertEquals(originalContent, originalMessage.getContent());
            assertEquals(originalAuthor, originalMessage.getAuthor());
            assertEquals(originalCreatedAt, originalMessage.getCreatedAt());
            assertEquals(MessageStatus.DELETED, originalMessage.getStatus());
        }
    }
}
