package io.lostyzen.demo.domain.exception;

/**
 * Exception thrown when a message is not found
 */
public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String messageId) {
        super(messageId);
    }
}
