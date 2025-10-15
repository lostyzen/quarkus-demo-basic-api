package io.lostyzen.demo.domain.model;

/**
 * Enumeration representing the status of a message
 */
public enum MessageStatus {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived"),
    DELETED("Deleted");

    private final String displayName;

    MessageStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(MessageStatus newStatus) {
        return switch (this) {
            case DRAFT -> newStatus == PUBLISHED || newStatus == DELETED;
            case PUBLISHED -> newStatus == ARCHIVED || newStatus == DELETED;
            case ARCHIVED -> newStatus == PUBLISHED || newStatus == DELETED;
            case DELETED -> false; // A deleted message cannot change status
        };
    }
}
