package org.acme.demo.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a message via REST API
 * Uses Lombok to reduce boilerplate code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {

    @JsonProperty("content")
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    @JsonProperty("author")
    @NotBlank(message = "Author cannot be empty")
    private String author;
}
