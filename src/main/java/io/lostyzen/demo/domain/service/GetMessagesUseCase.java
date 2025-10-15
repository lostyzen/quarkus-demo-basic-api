package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.out.MessageRepository;

import java.util.List;

/**
 * Use Case: Retrieve messages
 */
@ApplicationScoped
public class GetMessagesUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public GetMessagesUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllActive() {
        return messageRepository.findAllActive();
    }

    public List<Message> getByStatus(MessageStatus status) {
        return messageRepository.findByStatus(status);
    }

    public List<Message> getByAuthor(String author) {
        return messageRepository.findByAuthor(author);
    }
}
