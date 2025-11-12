package io.lostyzen.demo.domain.port.in;

import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;

/**
 * Input Port (Use Case Interface) for publishing messages
 */
public interface PublishMessagePort {

    /**
     * Publishes a draft message
     *
     * @param messageId the message ID
     * @return the published message
     */
    Message execute(MessageId messageId);
}

