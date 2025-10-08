package org.acme.demo.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object représentant l'identifiant d'un message
 */
public class MessageId {
    private final String value;

    public MessageId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("MessageId ne peut pas être vide");
        }
        this.value = value;
    }

    public static MessageId generate() {
        return new MessageId(UUID.randomUUID().toString());
    }

    public static MessageId of(String value) {
        return new MessageId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageId messageId = (MessageId) o;
        return Objects.equals(value, messageId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "MessageId{" + value + "}";
    }
}
