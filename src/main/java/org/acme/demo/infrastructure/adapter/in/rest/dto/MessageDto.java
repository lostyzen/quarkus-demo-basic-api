package org.acme.demo.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageStatus;

import java.time.LocalDateTime;

/**
 * DTO for REST representation of a message
 * Uses Lombok to reduce boilerplate code
 */
@Data
@NoArgsConstructor
public class MessageDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("status")
    private MessageStatus status;

    @JsonProperty("author")
    private String author;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("publishedAt")
    @JsonInclude(JsonInclude.Include.NON_NULL) // Include only if non-null
    private LocalDateTime publishedAt;

    @JsonProperty("deletedAt")
    @JsonInclude(JsonInclude.Include.NON_NULL) // Include only if non-null
    private LocalDateTime deletedAt;

    // Constructor from domain model
    public MessageDto(Message message) {
        this.id = message.getId().getValue();
        this.content = message.getContent();
        this.status = message.getStatus();
        this.author = message.getAuthor();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
        this.publishedAt = message.getPublishedAt();
        this.deletedAt = message.getDeletedAt();
    }
}
