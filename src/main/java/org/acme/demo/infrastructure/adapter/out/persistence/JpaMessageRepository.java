package org.acme.demo.infrastructure.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.model.MessageStatus;
import org.acme.demo.domain.port.out.MessageRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation JPA du repository de messages
 * Cette classe fait partie de la couche infrastructure et implémente le port défini dans le domaine
 */
@ApplicationScoped
@Transactional
public class JpaMessageRepository implements MessageRepository {

    @Override
    public Message save(Message message) {
        MessageEntity entity = MessageEntity.findById(message.getId().getValue());

        if (entity == null) {
            // Création d'un nouveau message
            entity = new MessageEntity(message);
            entity.persist();
        } else {
            // Mise à jour d'un message existant
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
