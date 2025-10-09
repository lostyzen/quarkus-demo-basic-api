package org.acme.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.demo.domain.exception.MessageNotFoundException;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.port.out.MessageRepository;

/**
 * Use Case: Update message content
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

        // Business validation is in the entity
        message.updateContent(newContent);

        return messageRepository.save(message);
    }
}
