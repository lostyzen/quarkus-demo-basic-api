package org.acme.demo.domain.exception;

/**
 * Exception levée quand un message n'est pas trouvé
 */
public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String messageId) {
        super("Message non trouvé avec l'ID : " + messageId);
    }
}
