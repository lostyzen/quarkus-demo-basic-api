package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.exception.MessageAlreadyDeletedException;
import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.in.DeleteMessagePort;
import io.lostyzen.demo.domain.port.out.MessageRepository;

/**
 * Use Case: Delete a message
 * Implements the DeleteMessagePort interface to provide loose coupling
 */
@ApplicationScoped
public class DeleteMessageUseCase implements DeleteMessagePort {

    private final MessageRepository messageRepository;

    @Inject
    public DeleteMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(MessageId messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId.getValue()));

        // Check if message is already deleted
        if (message.getStatus() == MessageStatus.DELETED) {
            throw new MessageAlreadyDeletedException(messageId.getValue());
        }

        // Logical deletion (status change)
        message.delete();
        messageRepository.save(message);
    }

    public void executeHardDelete(MessageId messageId) {
        // Physical deletion from the database
        if (!messageRepository.findById(messageId).isPresent()) {
            throw new MessageNotFoundException(messageId.getValue());
        }
        messageRepository.deleteById(messageId);
    }
}
