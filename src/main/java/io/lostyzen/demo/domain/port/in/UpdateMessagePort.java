package io.lostyzen.demo.domain.port.in;

import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;

/**
 * Input Port (Use Case Interface) for updating messages
 */
public interface UpdateMessagePort {

    /**
     * Updates the content of an existing message
     *
     * @param messageId the message ID
     * @param newContent the new content
     * @return the updated message
     */
    Message execute(MessageId messageId, String newContent);
}

