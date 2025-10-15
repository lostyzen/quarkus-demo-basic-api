package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.port.out.MessageRepository;

/**
 * Use Case: Publish a message
 */
@ApplicationScoped
public class PublishMessageUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public PublishMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message execute(MessageId messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId.getValue()));

        // Business logic for publishing is in the entity
        message.publish();

        return messageRepository.save(message);
    }
}
