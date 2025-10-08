package org.acme.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.demo.domain.exception.MessageNotFoundException;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.port.out.MessageRepository;

/**
 * Use Case : Modification du contenu d'un message
 */
@ApplicationScoped
public class UpdateMessageUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public UpdateMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message execute(MessageId messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId.getValue()));

        // La validation métier est dans l'entité
        message.updateContent(newContent);

        return messageRepository.save(message);
    }
}
