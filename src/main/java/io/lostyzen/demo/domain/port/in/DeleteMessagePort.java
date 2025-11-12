package io.lostyzen.demo.domain.port.in;

import io.lostyzen.demo.domain.model.MessageId;

/**
 * Input Port (Use Case Interface) for deleting messages
 */
public interface DeleteMessagePort {

    /**
     * Marks a message as deleted
     *
     * @param messageId the message ID
     */
    void execute(MessageId messageId);
}

