package org.acme.demo.domain.model;

/**
 * Énumération représentant le statut d'un message
 */
public enum MessageStatus {
    DRAFT("Brouillon"),
    PUBLISHED("Publié"),
    ARCHIVED("Archivé"),
    DELETED("Supprimé");

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
            case DELETED -> false; // Un message supprimé ne peut pas changer d'état
        };
    }
}
