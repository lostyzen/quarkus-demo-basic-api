package org.acme.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.demo.domain.exception.MessageNotFoundException;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.port.out.MessageRepository;

/**
 * Use Case : Suppression d'un message
 */
@ApplicationScoped
public class DeleteMessageUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public DeleteMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(MessageId messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId.getValue()));

        // Suppression logique (changement de statut)
        message.delete();
        messageRepository.save(message);
    }

    public void executeHardDelete(MessageId messageId) {
        // Suppression physique de la base de données
        if (!messageRepository.findById(messageId).isPresent()) {
            throw new MessageNotFoundException(messageId.getValue());
        }
        messageRepository.deleteById(messageId);
    }
}
