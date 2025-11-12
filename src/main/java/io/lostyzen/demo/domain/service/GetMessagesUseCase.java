package io.lostyzen.demo.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.in.GetMessagesPort;
import io.lostyzen.demo.domain.port.out.MessageRepository;

import java.util.List;

/**
 * Use Case: Retrieve messages
 * Implements the GetMessagesPort interface to provide loose coupling
 */
@ApplicationScoped
public class GetMessagesUseCase implements GetMessagesPort {

    private final MessageRepository messageRepository;

    @Inject
    public GetMessagesUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> getAllActive() {
        return messageRepository.findAllActive();
    }

    @Override
    public List<Message> getByStatus(MessageStatus status) {
        return messageRepository.findByStatus(status);
    }

    @Override
    public List<Message> getByAuthor(String author) {
        return messageRepository.findByAuthor(author);
    }
}
