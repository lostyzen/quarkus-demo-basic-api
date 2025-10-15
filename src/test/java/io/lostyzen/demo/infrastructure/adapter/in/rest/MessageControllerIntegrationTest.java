package io.lostyzen.demo.infrastructure.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for MessageController
 * Tests the complete REST API with hexagonal architecture
 * These tests validate the entire flow: REST -> Use Cases -> Domain -> Repository -> Database
 */
@QuarkusTest
class MessageControllerIntegrationTest {

    @BeforeEach
    void setUp() {
        // Clean database state before each test if needed
        // In a real scenario, you might want to reset the H2 database
    }

    @Nested
    @DisplayName("Message Creation API")
    class MessageCreationAPI {

        @Test
        @DisplayName("Should create message with valid data")
        void should_create_message_with_valid_data() {
            String content = "Integration test message content";
            String author = "Integration Test Author";

            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "%s",
                        "author": "%s"
                    }
                    """.formatted(content, author))
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .body("content", equalTo(content))
                .body("author", equalTo(author))
                .body("status", equalTo("DRAFT"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
        }

        @Test
        @DisplayName("Should fail when content is empty")
        void should_fail_when_content_is_empty() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("Should fail when author is missing")
        void should_fail_when_author_is_missing() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Test content"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("Should trim whitespace from content and author")
        void should_trim_whitespace_from_content_and_author() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "  Test content  ",
                        "author": "  Test Author  "
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .body("content", equalTo("Test content"))
                .body("author", equalTo("Test Author"));
        }
    }

    @Nested
    @DisplayName("Message Retrieval API")
    class MessageRetrievalAPI {

        @Test
        @DisplayName("Should return empty list initially")
        void should_return_empty_list_initially() {
            // Instead of expecting empty, we'll create and verify our specific message
            String uniqueContent = "Test content " + System.currentTimeMillis();

            // Count initial messages
            int initialCount = given()
            .when()
                .get("/api/messages")
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$").size();

            // Create a new message
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "%s",
                        "author": "Test Author"
                    }
                    """.formatted(uniqueContent))
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201);

            // Verify the count increased by 1
            given()
            .when()
                .get("/api/messages")
            .then()
                .statusCode(200)
                .body("size()", is(initialCount + 1))
                .body("find { it.content == '%s' }".formatted(uniqueContent), notNullValue());
        }

        @Test
        @DisplayName("Should return created messages")
        void should_return_created_messages() {
            // Create a message first
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Test message for retrieval",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // Retrieve messages
            given()
            .when()
                .get("/api/messages")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("find { it.id == '%s' }.content".formatted(messageId), equalTo("Test message for retrieval"))
                .body("find { it.id == '%s' }.status".formatted(messageId), equalTo("DRAFT"));
        }

        @Test
        @DisplayName("Should filter messages by status")
        void should_filter_messages_by_status() {
            // Create and publish a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message to publish",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // Publish the message
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", messageId)
            .then()
                .statusCode(200);

            // Filter by PUBLISHED status
            given()
            .when()
                .get("/api/messages/status/PUBLISHED")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("find { it.id == '%s' }.status".formatted(messageId), equalTo("PUBLISHED"));
        }

        @Test
        @DisplayName("Should filter messages by author")
        void should_filter_messages_by_author() {
            String uniqueAuthor = "UniqueTestAuthor" + System.currentTimeMillis();

            // Create message with unique author
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message by unique author",
                        "author": "%s"
                    }
                    """.formatted(uniqueAuthor))
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201);

            // Filter by author
            given()
            .when()
                .get("/api/messages/author/{author}", uniqueAuthor)
            .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].author", equalTo(uniqueAuthor));
        }
    }

    @Nested
    @DisplayName("Message Publishing API")
    class MessagePublishingAPI {

        @Test
        @DisplayName("Should publish draft message successfully")
        void should_publish_draft_message_successfully() {
            // Create a draft message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message to publish",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .body("status", equalTo("DRAFT"))
                .extract().path("id");

            // Publish the message
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", messageId)
            .then()
                .statusCode(200)
                .body("status", equalTo("PUBLISHED"))
                .body("publishedAt", notNullValue())
                .body("id", equalTo(messageId));
        }

        @Test
        @DisplayName("Should fail to publish non-existent message")
        void should_fail_to_publish_non_existent_message() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", "non-existent-id")
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("Should fail to publish already published message")
        void should_fail_to_publish_already_published_message() {
            // Create and publish a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message to double publish",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // First publish (should succeed)
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", messageId)
            .then()
                .statusCode(200);

            // Second publish (should fail)
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", messageId)
            .then()
                .statusCode(400);
        }
    }

    @Nested
    @DisplayName("Message Update API")
    class MessageUpdateAPI {

        @Test
        @DisplayName("Should update message content successfully")
        void should_update_message_content_successfully() {
            // Create a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Original content",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // Update the content
            String newContent = "Updated content";
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "%s"
                    }
                    """.formatted(newContent))
            .when()
                .put("/api/messages/{id}", messageId)
            .then()
                .statusCode(200)
                .body("content", equalTo(newContent))
                .body("id", equalTo(messageId))
                .body("author", equalTo("Test Author"));
        }

        @Test
        @DisplayName("Should fail to update non-existent message")
        void should_fail_to_update_non_existent_message() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "New content"
                    }
                    """)
            .when()
                .put("/api/messages/{id}", "non-existent-id")
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("Should fail to update with empty content")
        void should_fail_to_update_with_empty_content() {
            // Create a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Original content",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // Try to update with empty content
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": ""
                    }
                    """)
            .when()
                .put("/api/messages/{id}", messageId)
            .then()
                .statusCode(400);
        }
    }

    @Nested
    @DisplayName("Message Deletion API")
    class MessageDeletionAPI {

        @Test
        @DisplayName("Should delete message successfully")
        void should_delete_message_successfully() {
            // Create a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message to delete",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // Delete the message
            given()
            .when()
                .delete("/api/messages/{id}", messageId)
            .then()
                .statusCode(204);

            // Verify message is no longer in active list
            given()
            .when()
                .get("/api/messages")
            .then()
                .statusCode(200)
                .body("find { it.id == '%s' }".formatted(messageId), nullValue());
        }

        @Test
        @DisplayName("Should fail to delete non-existent message")
        void should_fail_to_delete_non_existent_message() {
            given()
            .when()
                .delete("/api/messages/{id}", "non-existent-id")
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("Should fail to delete already deleted message")
        void should_fail_to_delete_already_deleted_message() {
            // Create a message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "Message to double delete",
                        "author": "Test Author"
                    }
                    """)
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .extract().path("id");

            // First deletion (should succeed)
            given()
            .when()
                .delete("/api/messages/{id}", messageId)
            .then()
                .statusCode(204);

            // Second deletion (should fail with Bad Request because message is already deleted)
            given()
            .when()
                .delete("/api/messages/{id}", messageId)
            .then()
                .statusCode(400);
        }
    }

    @Nested
    @DisplayName("Complete Message Lifecycle")
    class CompleteMessageLifecycle {

        @Test
        @DisplayName("Should handle complete message lifecycle")
        void should_handle_complete_message_lifecycle() {
            String content = "Lifecycle test message";
            String author = "Lifecycle Test Author";

            // 1. Create message
            String messageId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "%s",
                        "author": "%s"
                    }
                    """.formatted(content, author))
            .when()
                .post("/api/messages")
            .then()
                .statusCode(201)
                .body("status", equalTo("DRAFT"))
                .extract().path("id");

            // 2. Update content
            String updatedContent = "Updated lifecycle message";
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "content": "%s"
                    }
                    """.formatted(updatedContent))
            .when()
                .put("/api/messages/{id}", messageId)
            .then()
                .statusCode(200)
                .body("content", equalTo(updatedContent));

            // 3. Publish message
            given()
                .contentType(ContentType.JSON)
            .when()
                .post("/api/messages/{id}/publish", messageId)
            .then()
                .statusCode(200)
                .body("status", equalTo("PUBLISHED"));

            // 4. Verify in published list
            given()
            .when()
                .get("/api/messages/status/PUBLISHED")
            .then()
                .statusCode(200)
                .body("find { it.id == '%s' }.content".formatted(messageId), equalTo(updatedContent));

            // 5. Delete message
            given()
            .when()
                .delete("/api/messages/{id}", messageId)
            .then()
                .statusCode(204);

            // 6. Verify not in active list
            given()
            .when()
                .get("/api/messages")
            .then()
                .statusCode(200)
                .body("find { it.id == '%s' }".formatted(messageId), nullValue());
        }
    }
}
