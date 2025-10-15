package io.lostyzen.demo.infrastructure.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.port.out.MessageRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the message repository
 * This class is part of the infrastructure layer and implements the port defined in the domain
 */
@ApplicationScoped
@Transactional
public class JpaMessageRepository implements MessageRepository {

    @Override
    public Message save(Message message) {
        MessageEntity entity = MessageEntity.findById(message.getId().getValue());

        if (entity == null) {
            // Create a new message
            entity = new MessageEntity(message);
            entity.persist();
        } else {
            // Update an existing message
            entity.updateFromDomainModel(message);
        }

        return entity.toDomainModel();
    }

    @Override
    public Optional<Message> findById(MessageId id) {
        MessageEntity entity = MessageEntity.findById(id.getValue());
        return entity != null ? Optional.of(entity.toDomainModel()) : Optional.empty();
    }

    @Override
    public List<Message> findByStatus(MessageStatus status) {
        return MessageEntity.<MessageEntity>list("status", status)
                .stream()
                .map(MessageEntity::toDomainModel)
                .toList();
    }

    @Override
    public List<Message> findByAuthor(String author) {
        return MessageEntity.<MessageEntity>list("author", author)
                .stream()
                .map(MessageEntity::toDomainModel)
                .toList();
    }

    @Override
    public List<Message> findAllActive() {
        return MessageEntity.<MessageEntity>list("status != ?1", MessageStatus.DELETED)
                .stream()
                .map(MessageEntity::toDomainModel)
                .toList();
    }

    @Override
    public void deleteById(MessageId id) {
        MessageEntity.deleteById(id.getValue());
    }

    @Override
    public long countByStatus(MessageStatus status) {
        return MessageEntity.count("status", status);
    }
}
