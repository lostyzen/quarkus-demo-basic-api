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
package io.lostyzen.demo.domain.port.in;

import io.lostyzen.demo.domain.model.Message;

/**
 * Input Port (Use Case Interface) for creating messages
 * This interface defines the contract that the adapter depends on.
 * In hexagonal architecture, adapters depend on ports (interfaces), not on implementations.
 */
public interface CreateMessagePort {

    /**
     * Creates a new message with the given content and author
     *
     * @param content the message content
     * @param author the message author
     * @return the created message
     */
    Message execute(String content, String author);
}

