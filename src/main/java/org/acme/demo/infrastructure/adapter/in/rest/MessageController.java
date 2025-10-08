package org.acme.demo.infrastructure.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.demo.domain.exception.MessageNotFoundException;
import org.acme.demo.domain.model.Message;
import org.acme.demo.domain.model.MessageId;
import org.acme.demo.domain.model.MessageStatus;
import org.acme.demo.domain.service.*;
import org.acme.demo.infrastructure.adapter.in.rest.dto.CreateMessageRequest;
import org.acme.demo.infrastructure.adapter.in.rest.dto.MessageDto;
import org.acme.demo.infrastructure.adapter.in.rest.dto.UpdateMessageRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.logging.Logger;

/**
 * Adaptateur REST (Adapter In) utilisant les Use Cases du domaine
 * Ce contrôleur ne contient AUCUNE logique métier - tout est délégué aux Use Cases
 */
@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Messages", description = "API de gestion des messages avec architecture hexagonale")
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
    @Operation(summary = "Récupère tous les messages actifs")
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
    @Operation(summary = "Récupère les messages par statut")
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
    @Operation(summary = "Récupère les messages par auteur")
    public List<MessageDto> getMessagesByAuthor(@PathParam("author") String author) {
        LOG.info("GET /api/messages/author/" + author);

        List<Message> messages = getMessagesUseCase.getByAuthor(author);
        return messages.stream()
            .map(MessageDto::new)
            .toList();
    }

    @POST
    @Operation(summary = "Crée un nouveau message")
    public Response createMessage(@Valid CreateMessageRequest request) {
        LOG.info("POST /api/messages - Creating new message");
        LOG.fine("Author: " + request.getAuthor() + ", Content: " + request.getContent());

        try {
            Message message = createMessageUseCase.execute(request.getContent(), request.getAuthor());
            MessageDto responseDto = new MessageDto(message);

            LOG.info("POST /api/messages - Message created successfully, ID: " + message.getId().getValue());
            return Response.status(Response.Status.CREATED).entity(responseDto).build();

        } catch (IllegalArgumentException e) {
            LOG.warning("POST /api/messages - Validation error: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Met à jour le contenu d'un message")
    public MessageDto updateMessage(@PathParam("id") String id, @Valid UpdateMessageRequest request) {
        LOG.info("PUT /api/messages/" + id + " - Updating message content");

        try {
            MessageId messageId = MessageId.of(id);
            Message message = updateMessageUseCase.execute(messageId, request.getContent());

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
    @Operation(summary = "Publie un message")
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
    @Operation(summary = "Supprime logiquement un message")
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
