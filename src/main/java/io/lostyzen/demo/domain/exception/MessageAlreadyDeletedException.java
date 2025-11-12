package io.lostyzen.demo.domain.exception;

/**
 * Exception thrown when attempting to delete a message that is already deleted
 */
public class MessageAlreadyDeletedException extends RuntimeException {
    public MessageAlreadyDeletedException(String messageId) {
        super("Message with ID " + messageId + " is already deleted");
    }
}

