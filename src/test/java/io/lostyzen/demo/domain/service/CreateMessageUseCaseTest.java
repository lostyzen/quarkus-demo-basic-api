package io.lostyzen.demo.domain.service;

import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateMessage Use Case
 * These tests use mocks to completely isolate business logic
 * Hexagonal architecture advantage: fast tests without external dependencies
 */
class CreateMessageUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    private CreateMessageUseCase createMessageUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createMessageUseCase = new CreateMessageUseCase(messageRepository);
    }

    @Nested
    @DisplayName("Successful Message Creation")
    class SuccessfulMessageCreation {

        @Test
        @DisplayName("Should create message successfully with valid data")
        void should_create_message_successfully() {
            // Given
            String content = "Test message content";
            String author = "John Doe";

            Message savedMessage = new Message(content, author);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

            // When
            Message result = createMessageUseCase.execute(content, author);

            // Then
            assertNotNull(result);
            assertEquals(content, result.getContent());
            assertEquals(author, result.getAuthor());
            assertEquals(MessageStatus.DRAFT, result.getStatus());

            // Verify repository interaction
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("Should pass through repository save result")
        void should_pass_through_repository_save_result() {
            // Given
            String content = "Test content";
            String author = "Test Author";

            Message expectedMessage = new Message(content, author);
            when(messageRepository.save(any(Message.class))).thenReturn(expectedMessage);

            // When
            Message result = createMessageUseCase.execute(content, author);

            // Then
            assertSame(expectedMessage, result);
        }
    }

    @Nested
    @DisplayName("Invalid Input Handling")
    class InvalidInputHandling {

        @Test
        @DisplayName("Should fail when content is empty")
        void should_fail_when_content_is_empty() {
            // Given
            String emptyContent = "";
            String author = "John Doe";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createMessageUseCase.execute(emptyContent, author)
            );

            assertEquals("Message content cannot be empty", exception.getMessage());
            verify(messageRepository, never()).save(any(Message.class));
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
                () -> createMessageUseCase.execute(content, emptyAuthor)
            );

            assertEquals("Author cannot be empty", exception.getMessage());
            verify(messageRepository, never()).save(any(Message.class));
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
                () -> createMessageUseCase.execute(nullContent, author)
            );

            assertEquals("Message content cannot be empty", exception.getMessage());
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("Repository Integration")
    class RepositoryIntegration {

        @Test
        @DisplayName("Should delegate message creation to domain entity")
        void should_delegate_message_creation_to_domain_entity() {
            // Given
            String content = "Test content";
            String author = "Test Author";

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Message result = createMessageUseCase.execute(content, author);

            // Then
            // Verify that the message was created properly by the domain entity
            assertEquals(content, result.getContent());
            assertEquals(author, result.getAuthor());
            assertEquals(MessageStatus.DRAFT, result.getStatus());
            assertNotNull(result.getId());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should call repository save with created message")
        void should_call_repository_save_with_created_message() {
            // Given
            String content = "Test content";
            String author = "Test Author";

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            createMessageUseCase.execute(content, author);

            // Then
            verify(messageRepository).save(argThat(message ->
                message.getContent().equals(content) &&
                message.getAuthor().equals(author) &&
                message.getStatus() == MessageStatus.DRAFT
            ));
        }
    }
}
