package org.acme.demo.domain.service;

import org.acme.demo.domain.exception.MessageNotFoundException;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.model.MessageStatus;
import org.acme.demo.domain.port.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du Use Case PublishMessage
 * Démontre comment l'architecture hexagonale facilite les tests de logique complexe
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

    @Test
    @DisplayName("Publication réussie d'un message en brouillon")
    void should_publish_draft_message_successfully() {
        // Given
        MessageId messageId = MessageId.generate();
        Message draftMessage = new Message("Contenu", "Auteur");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(draftMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(draftMessage);

        // When
        Message result = publishMessageUseCase.execute(messageId);

        // Then
        assertEquals(MessageStatus.PUBLISHED, result.getStatus());
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(1)).save(draftMessage);
    }

    @Test
    @DisplayName("Échec publication message inexistant")
    void should_fail_when_message_not_found() {
        // Given
        MessageId messageId = MessageId.generate();
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // When & Then
        MessageNotFoundException exception = assertThrows(
            MessageNotFoundException.class,
            () -> publishMessageUseCase.execute(messageId)
        );

        assertTrue(exception.getMessage().contains(messageId.getValue()));
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Échec publication message déjà supprimé")
    void should_fail_to_publish_deleted_message() {
        // Given
        MessageId messageId = MessageId.generate();
        Message deletedMessage = new Message("Contenu", "Auteur");
        deletedMessage.delete(); // Message supprimé

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(deletedMessage));

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> publishMessageUseCase.execute(messageId)
        );

        assertTrue(exception.getMessage().contains("Impossible de publier"));
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, never()).save(any(Message.class));
    }
}
