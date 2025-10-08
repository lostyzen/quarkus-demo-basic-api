package org.acme.demo.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création d'un message via l'API REST
 */
public class CreateMessageRequest {

    @JsonProperty("content")
    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(max = 1000, message = "Le contenu ne peut pas dépasser 1000 caractères")
    private String content;

    @JsonProperty("author")
    @NotBlank(message = "L'auteur ne peut pas être vide")
    private String author;

    // Constructeur par défaut pour Jackson
    public CreateMessageRequest() {}

    public CreateMessageRequest(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
