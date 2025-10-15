package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.port.out.MessageRepository;

/**
 * Use Case: Create a new message
 */
@ApplicationScoped
public class CreateMessageUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public CreateMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message execute(String content, String author) {
        // Business validation is already in the Message entity
        Message message = new Message(content, author);
        return messageRepository.save(message);
    }
}
