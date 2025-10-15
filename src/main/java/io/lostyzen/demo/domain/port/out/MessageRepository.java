package io.lostyzen.demo.domain.port.out;

import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie (interface) pour la persistance des messages
 * Cette interface est définie dans le domaine et implémentée dans l'infrastructure
 */
public interface MessageRepository {

    /**
     * Sauvegarde un message (création ou mise à jour)
     */
    Message save(Message message);

    /**
     * Trouve un message par son ID
     */
    Optional<Message> findById(MessageId id);

    /**
     * Trouve tous les messages d'un statut donné
     */
    List<Message> findByStatus(MessageStatus status);

    /**
     * Trouve tous les messages d'un auteur donné
     */
    List<Message> findByAuthor(String author);

    /**
     * Trouve tous les messages (sauf supprimés)
     */
    List<Message> findAllActive();

    /**
     * Supprime définitivement un message
     */
    void deleteById(MessageId id);

    /**
     * Compte le nombre de messages par statut
     */
    long countByStatus(MessageStatus status);
}
