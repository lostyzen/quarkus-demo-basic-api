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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetMessages Use Case
 * Tests message retrieval functionality with different filtering criteria
 */
class GetMessagesUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    private GetMessagesUseCase getMessagesUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getMessagesUseCase = new GetMessagesUseCase(messageRepository);
    }

    @Nested
    @DisplayName("Get All Active Messages")
    class GetAllActiveMessages {

        @Test
        @DisplayName("Should return all active messages")
        void should_return_all_active_messages() {
            // Given
            Message message1 = new Message("Content 1", "Author 1");
            Message message2 = new Message("Content 2", "Author 2");
            message2.publish();

            List<Message> expectedMessages = Arrays.asList(message1, message2);
            when(messageRepository.findAllActive()).thenReturn(expectedMessages);

            // When
            List<Message> result = getMessagesUseCase.getAllActive();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(expectedMessages, result);
            verify(messageRepository).findAllActive();
        }

        @Test
        @DisplayName("Should return empty list when no active messages")
        void should_return_empty_list_when_no_active_messages() {
            // Given
            when(messageRepository.findAllActive()).thenReturn(Collections.emptyList());

            // When
            List<Message> result = getMessagesUseCase.getAllActive();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(messageRepository).findAllActive();
        }

        @Test
        @DisplayName("Should delegate to repository without modification")
        void should_delegate_to_repository_without_modification() {
            // Given
            List<Message> repositoryResult = Arrays.asList(
                new Message("Content 1", "Author 1"),
                new Message("Content 2", "Author 2")
            );
            when(messageRepository.findAllActive()).thenReturn(repositoryResult);

            // When
            List<Message> result = getMessagesUseCase.getAllActive();

            // Then
            assertSame(repositoryResult, result);
            verify(messageRepository, times(1)).findAllActive();
        }
    }

    @Nested
    @DisplayName("Get Messages By Status")
    class GetMessagesByStatus {

        @Test
        @DisplayName("Should return messages with draft status")
        void should_return_messages_with_draft_status() {
            // Given
            MessageStatus draftStatus = MessageStatus.DRAFT;
            Message draftMessage1 = new Message("Draft 1", "Author 1");
            Message draftMessage2 = new Message("Draft 2", "Author 2");

            List<Message> draftMessages = Arrays.asList(draftMessage1, draftMessage2);
            when(messageRepository.findByStatus(draftStatus)).thenReturn(draftMessages);

            // When
            List<Message> result = getMessagesUseCase.getByStatus(draftStatus);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(draftMessages, result);
            verify(messageRepository).findByStatus(draftStatus);
        }

        @Test
        @DisplayName("Should return messages with published status")
        void should_return_messages_with_published_status() {
            // Given
            MessageStatus publishedStatus = MessageStatus.PUBLISHED;
            Message publishedMessage = new Message("Published content", "Author");
            publishedMessage.publish();

            List<Message> publishedMessages = Arrays.asList(publishedMessage);
            when(messageRepository.findByStatus(publishedStatus)).thenReturn(publishedMessages);

            // When
            List<Message> result = getMessagesUseCase.getByStatus(publishedStatus);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(MessageStatus.PUBLISHED, result.get(0).getStatus());
            verify(messageRepository).findByStatus(publishedStatus);
        }

        @Test
        @DisplayName("Should return empty list for status with no messages")
        void should_return_empty_list_for_status_with_no_messages() {
            // Given
            MessageStatus archivedStatus = MessageStatus.ARCHIVED;
            when(messageRepository.findByStatus(archivedStatus)).thenReturn(Collections.emptyList());

            // When
            List<Message> result = getMessagesUseCase.getByStatus(archivedStatus);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(messageRepository).findByStatus(archivedStatus);
        }

        @Test
        @DisplayName("Should handle all message statuses")
        void should_handle_all_message_statuses() {
            // Test all possible statuses
            for (MessageStatus status : MessageStatus.values()) {
                // Given
                reset(messageRepository);
                when(messageRepository.findByStatus(status)).thenReturn(Collections.emptyList());

                // When
                List<Message> result = getMessagesUseCase.getByStatus(status);

                // Then
                assertNotNull(result);
                verify(messageRepository).findByStatus(status);
            }
        }
    }

    @Nested
    @DisplayName("Get Messages By Author")
    class GetMessagesByAuthor {

        @Test
        @DisplayName("Should return messages from specific author")
        void should_return_messages_from_specific_author() {
            // Given
            String author = "John Doe";
            Message message1 = new Message("Content 1", author);
            Message message2 = new Message("Content 2", author);

            List<Message> authorMessages = Arrays.asList(message1, message2);
            when(messageRepository.findByAuthor(author)).thenReturn(authorMessages);

            // When
            List<Message> result = getMessagesUseCase.getByAuthor(author);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            result.forEach(message -> assertEquals(author, message.getAuthor()));
            verify(messageRepository).findByAuthor(author);
        }

        @Test
        @DisplayName("Should return empty list for author with no messages")
        void should_return_empty_list_for_author_with_no_messages() {
            // Given
            String author = "Unknown Author";
            when(messageRepository.findByAuthor(author)).thenReturn(Collections.emptyList());

            // When
            List<Message> result = getMessagesUseCase.getByAuthor(author);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(messageRepository).findByAuthor(author);
        }

        @Test
        @DisplayName("Should handle case sensitive author search")
        void should_handle_case_sensitive_author_search() {
            // Given
            String exactAuthor = "John Doe";
            String differentCaseAuthor = "john doe";

            when(messageRepository.findByAuthor(exactAuthor)).thenReturn(Arrays.asList(new Message("Content", exactAuthor)));
            when(messageRepository.findByAuthor(differentCaseAuthor)).thenReturn(Collections.emptyList());

            // When
            List<Message> exactResult = getMessagesUseCase.getByAuthor(exactAuthor);
            List<Message> differentCaseResult = getMessagesUseCase.getByAuthor(differentCaseAuthor);

            // Then
            assertEquals(1, exactResult.size());
            assertTrue(differentCaseResult.isEmpty());

            verify(messageRepository).findByAuthor(exactAuthor);
            verify(messageRepository).findByAuthor(differentCaseAuthor);
        }

        @Test
        @DisplayName("Should delegate exact author string to repository")
        void should_delegate_exact_author_string_to_repository() {
            // Given
            String authorWithSpaces = "  John Doe  ";
            when(messageRepository.findByAuthor(authorWithSpaces)).thenReturn(Collections.emptyList());

            // When
            getMessagesUseCase.getByAuthor(authorWithSpaces);

            // Then
            // Should pass the exact string without trimming (repository responsibility)
            verify(messageRepository).findByAuthor(authorWithSpaces);
        }
    }

    @Nested
    @DisplayName("Repository Integration")
    class RepositoryIntegration {

        @Test
        @DisplayName("Should not modify repository results")
        void should_not_modify_repository_results() {
            // Given
            List<Message> originalList = Arrays.asList(
                new Message("Content 1", "Author 1"),
                new Message("Content 2", "Author 2")
            );
            when(messageRepository.findAllActive()).thenReturn(originalList);

            // When
            List<Message> result = getMessagesUseCase.getAllActive();

            // Then
            assertSame(originalList, result); // Should return exact same reference
            assertEquals(originalList.size(), result.size());
        }

        @Test
        @DisplayName("Should make single repository call per request")
        void should_make_single_repository_call_per_request() {
            // Given
            when(messageRepository.findAllActive()).thenReturn(Collections.emptyList());
            when(messageRepository.findByStatus(any())).thenReturn(Collections.emptyList());
            when(messageRepository.findByAuthor(any())).thenReturn(Collections.emptyList());

            // When
            getMessagesUseCase.getAllActive();
            getMessagesUseCase.getByStatus(MessageStatus.DRAFT);
            getMessagesUseCase.getByAuthor("John Doe");

            // Then
            verify(messageRepository, times(1)).findAllActive();
            verify(messageRepository, times(1)).findByStatus(MessageStatus.DRAFT);
            verify(messageRepository, times(1)).findByAuthor("John Doe");
        }
    }
}
