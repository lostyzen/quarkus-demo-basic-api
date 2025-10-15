package io.lostyzen.demo.infrastructure.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import io.lostyzen.demo.domain.exception.MessageNotFoundException;
import io.lostyzen.demo.domain.model.Message;
import io.lostyzen.demo.domain.model.MessageId;
import io.lostyzen.demo.domain.model.MessageStatus;
import io.lostyzen.demo.domain.service.*;
import io.lostyzen.demo.infrastructure.adapter.in.rest.dto.CreateMessageRequest;
import io.lostyzen.demo.infrastructure.adapter.in.rest.dto.MessageDto;
import io.lostyzen.demo.infrastructure.adapter.in.rest.dto.UpdateMessageRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST Adapter (Adapter In) using domain Use Cases
 * This controller contains NO business logic - everything is delegated to Use Cases
 * Uses Lombok for reducing boilerplate code
 */
@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Messages", description = "Message management API with hexagonal architecture")
public class MessageController {

    private static final Logger LOG = Logger.getLogger(MessageController.class.getName());

    private final CreateMessageUseCase createMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final UpdateMessageUseCase updateMessageUseCase;
    private final PublishMessageUseCase publishMessageUseCase;
    private final DeleteMessageUseCase deleteMessageUseCase;

    @Inject
    public MessageController(CreateMessageUseCase createMessageUseCase,
                           GetMessagesUseCase getMessagesUseCase,
                           UpdateMessageUseCase updateMessageUseCase,
                           PublishMessageUseCase publishMessageUseCase,
                           DeleteMessageUseCase deleteMessageUseCase) {
        this.createMessageUseCase = createMessageUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
        this.updateMessageUseCase = updateMessageUseCase;
        this.publishMessageUseCase = publishMessageUseCase;
        this.deleteMessageUseCase = deleteMessageUseCase;
    }

    @GET
    @Operation(summary = "Retrieve all active messages")
    public List<MessageDto> getAllMessages() {
        LOG.info("GET /api/messages - Retrieving all active messages");

        List<Message> messages = getMessagesUseCase.getAllActive();
        List<MessageDto> result = messages.stream()
            .map(MessageDto::new)
            .toList();

        LOG.info("GET /api/messages - Returning " + result.size() + " message(s)");
        return result;
    }

    @GET
    @Path("/status/{status}")
    @Operation(summary = "Retrieve messages by status")
    public List<MessageDto> getMessagesByStatus(@PathParam("status") String status) {
        LOG.info("GET /api/messages/status/" + status);

        try {
            MessageStatus messageStatus = MessageStatus.valueOf(status.toUpperCase());
            List<Message> messages = getMessagesUseCase.getByStatus(messageStatus);
            return messages.stream()
                .map(MessageDto::new)
                .toList();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @GET
    @Path("/author/{author}")
    @Operation(summary = "Retrieve messages by author")
    public List<MessageDto> getMessagesByAuthor(@PathParam("author") String author) {
        LOG.info("GET /api/messages/author/" + author);

        List<Message> messages = getMessagesUseCase.getByAuthor(author);
        return messages.stream()
            .map(MessageDto::new)
            .toList();
    }

    @POST
    @Operation(summary = "Create a new message")
    public Response createMessage(@Valid CreateMessageRequest request) {
        LOG.info("POST /api/messages - Creating new message");
        LOG.fine("Author: " + request.getAuthor() + ", Content: " + request.getContent());

        try {
            Message message = createMessageUseCase.execute(
                request.getContent().trim(),
                request.getAuthor().trim()
            );

            MessageDto response = new MessageDto(message);
            LOG.info("POST /api/messages - Message created successfully, ID: " + message.getId().getValue());

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();

        } catch (IllegalArgumentException e) {
            LOG.warning("POST /api/messages - Invalid input: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update message content")
    public MessageDto updateMessage(@PathParam("id") String id, @Valid UpdateMessageRequest request) {
        LOG.info("PUT /api/messages/" + id + " - Updating message content");

        try {
            MessageId messageId = MessageId.of(id);
            Message message = updateMessageUseCase.execute(messageId, request.getContent().trim());

            LOG.info("PUT /api/messages/" + id + " - Message updated successfully");
            return new MessageDto(message);

        } catch (MessageNotFoundException e) {
            LOG.warning("PUT /api/messages/" + id + " - Message not found");
            throw new NotFoundException(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOG.warning("PUT /api/messages/" + id + " - Error: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/{id}/publish")
    @Operation(summary = "Publish a message")
    public MessageDto publishMessage(@PathParam("id") String id) {
        LOG.info("POST /api/messages/" + id + "/publish - Publishing message");

        try {
            MessageId messageId = MessageId.of(id);
            Message message = publishMessageUseCase.execute(messageId);

            LOG.info("POST /api/messages/" + id + "/publish - Message published successfully");
            return new MessageDto(message);

        } catch (MessageNotFoundException e) {
            LOG.warning("POST /api/messages/" + id + "/publish - Message not found");
            throw new NotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            LOG.warning("POST /api/messages/" + id + "/publish - Error: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Logically delete a message")
    public Response deleteMessage(@PathParam("id") String id) {
        LOG.info("DELETE /api/messages/" + id + " - Logical deletion of message");

        try {
            MessageId messageId = MessageId.of(id);
            deleteMessageUseCase.execute(messageId);

            LOG.info("DELETE /api/messages/" + id + " - Message deleted successfully");
            return Response.noContent().build();

        } catch (MessageNotFoundException e) {
            LOG.warning("DELETE /api/messages/" + id + " - Message not found");
            throw new NotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            LOG.warning("DELETE /api/messages/" + id + " - Error: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }
}
