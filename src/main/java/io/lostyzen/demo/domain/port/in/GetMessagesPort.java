package io.lostyzen.demo.domain.port.in;

import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageStatus;

import java.util.List;

/**
 * Input Port (Use Case Interface) for retrieving messages
 */
public interface GetMessagesPort {

    /**
     * Retrieves all active messages
     *
     * @return list of active messages
     */
    List<Message> getAllActive();

    /**
     * Retrieves messages by status
     *
     * @param status the message status
     * @return list of messages with the given status
     */
    List<Message> getByStatus(MessageStatus status);

    /**
     * Retrieves messages by author
     *
     * @param author the author name
     * @return list of messages from the given author
     */
    List<Message> getByAuthor(String author);
}

