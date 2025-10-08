package org.acme.demo.domain.service;

import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.port.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du Use Case CreateMessage
 * Ces tests utilisent des mocks pour isoler complètement la logique métier
 * Avantage de l'architecture hexagonale : tests rapides et sans dépendances externes
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

    @Test
    @DisplayName("Création réussie d'un message")
    void should_create_message_successfully() {
        // Given
        String content = "Contenu du message";
        String author = "John Doe";

        Message expectedMessage = new Message(content, author);
        when(messageRepository.save(any(Message.class))).thenReturn(expectedMessage);

        // When
        Message result = createMessageUseCase.execute(content, author);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(author, result.getAuthor());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("Échec création avec contenu invalide")
    void should_fail_with_invalid_content() {
        // Given
        String invalidContent = "";
        String author = "John Doe";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createMessageUseCase.execute(invalidContent, author)
        );

        assertEquals("Le contenu du message ne peut pas être vide", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Échec création avec auteur invalide")
    void should_fail_with_invalid_author() {
        // Given
        String content = "Contenu valide";
        String invalidAuthor = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createMessageUseCase.execute(content, invalidAuthor)
        );

        assertEquals("L'auteur du message ne peut pas être vide", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }
}
