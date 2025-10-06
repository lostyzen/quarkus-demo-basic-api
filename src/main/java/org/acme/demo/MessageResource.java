package org.acme.demo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;
import java.util.logging.Logger;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    private static final Logger LOG = Logger.getLogger(MessageResource.class.getName());
    private static final List<Message> messages = new ArrayList<>();

    @GET
    public List<Message> getAll() {
        LOG.info("GET /messages - Récupération de tous les messages");
        LOG.fine("Nombre de messages actuels: " + messages.size());

        List<Message> result = new ArrayList<>(messages);

        LOG.info("GET /messages - Retour de " + result.size() + " message(s)");
        return result;
    }

    @POST
    public Message add(Message message) {
        LOG.info("POST /messages - Ajout d'un nouveau message");
        LOG.fine("Contenu du message reçu: " + (message != null ? message.getContent() : "null"));

        if (message == null || message.getContent() == null || message.getContent().trim().isEmpty()) {
            LOG.warning("POST /messages - Tentative d'ajout d'un message vide ou null");
            throw new BadRequestException("Le contenu du message ne peut pas être vide");
        }

        message.setId(UUID.randomUUID().toString());
        messages.add(message);

        LOG.info("POST /messages - Message ajouté avec succès, ID: " + message.getId());
        LOG.fine("Contenu du message ajouté: " + message.getContent());
        LOG.fine("Nombre total de messages: " + messages.size());

        return message;
    }
}
