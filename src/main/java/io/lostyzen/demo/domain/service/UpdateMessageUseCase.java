package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.port.in.UpdateMessagePort;
import io.lostyzen.demo.domain.port.out.MessageRepository;

/**
 * Use Case: Update message content
 * Implements the UpdateMessagePort interface to provide loose coupling
 */
@ApplicationScoped
public class UpdateMessageUseCase implements UpdateMessagePort {

    private final MessageRepository messageRepository;

    @Inject
    public UpdateMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message execute(MessageId messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId.getValue()));

        // Business validation is in the entity
        message.updateContent(newContent);

        return messageRepository.save(message);
    }
}
