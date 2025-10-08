package org.acme.demo.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la mise à jour du contenu d'un message via l'API REST
 */
public class UpdateMessageRequest {

    @JsonProperty("content")
    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(max = 1000, message = "Le contenu ne peut pas dépasser 1000 caractères")
    private String content;

    // Constructeur par défaut pour Jackson
    public UpdateMessageRequest() {}

    public UpdateMessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
