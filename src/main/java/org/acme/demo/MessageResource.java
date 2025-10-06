package org.acme.demo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    private static final List<Message> messages = new ArrayList<>();

    @GET
    public List<Message> getAll() {
        return messages;
    }

    @POST
    public Message add(Message message) {
        message.setId(UUID.randomUUID().toString());
        messages.add(message);
        return message;
    }
}
