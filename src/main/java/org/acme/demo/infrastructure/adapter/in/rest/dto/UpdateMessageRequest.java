package org.acme.demo.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating message content via REST API
 * Uses Lombok to reduce boilerplate code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageRequest {

    @JsonProperty("content")
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;
}
