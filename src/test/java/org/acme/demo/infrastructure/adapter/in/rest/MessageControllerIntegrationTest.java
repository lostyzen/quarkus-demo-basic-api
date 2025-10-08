package org.acme.demo.infrastructure.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration de l'API REST
 * Ces tests valident le comportement end-to-end de l'architecture hexagonale
 */
@QuarkusTest
class MessageControllerIntegrationTest {

    @Test
    @DisplayName("Scénario complet : Création, publication et récupération d'un message")
    void should_handle_complete_message_lifecycle() {
        // 1. Création d'un message
        String messageId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "content": "Mon premier message avec architecture hexagonale",
                    "author": "Développeur Java"
                }
                """)
        .when()
            .post("/api/messages")
        .then()
            .statusCode(201)
            .body("content", equalTo("Mon premier message avec architecture hexagonale"))
            .body("author", equalTo("Développeur Java"))
            .body("status", equalTo("DRAFT"))
            .body("id", notNullValue())
            .extract().path("id");

        // 2. Publication du message
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/messages/{id}/publish", messageId)
        .then()
            .statusCode(200)
            .body("id", equalTo(messageId))
            .body("status", equalTo("PUBLISHED"));

        // 3. Récupération de tous les messages
        given()
        .when()
            .get("/api/messages")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("find { it.id == '" + messageId + "' }.status", equalTo("PUBLISHED"));

        // 4. Récupération par statut
        given()
        .when()
            .get("/api/messages/status/PUBLISHED")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("findAll { it.status == 'PUBLISHED' }.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Validation des règles métier via API")
    void should_validate_business_rules_via_api() {
        // Tentative de création avec contenu vide
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "content": "",
                    "author": "Test User"
                }
                """)
        .when()
            .post("/api/messages")
        .then()
            .statusCode(400);

        // Tentative de création avec auteur vide
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "content": "Contenu valide",
                    "author": ""
                }
                """)
        .when()
            .post("/api/messages")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Gestion des erreurs pour ressources inexistantes")
    void should_handle_not_found_errors() {
        String fakeId = "non-existent-id";

        // Tentative de publication d'un message inexistant
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/messages/{id}/publish", fakeId)
        .then()
            .statusCode(404);

        // Tentative de mise à jour d'un message inexistant
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "content": "Nouveau contenu"
                }
                """)
        .when()
            .put("/api/messages/{id}", fakeId)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Filtrage par auteur")
    void should_filter_messages_by_author() {
        String author = "Auteur Test " + System.currentTimeMillis();

        // Création de plusieurs messages du même auteur
        given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "content": "Premier message",
                    "author": "%s"
                }
                """, author))
        .when()
            .post("/api/messages")
        .then()
            .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                {
                    "content": "Deuxième message",
                    "author": "%s"
                }
                """, author))
        .when()
            .post("/api/messages")
        .then()
            .statusCode(201);

        // Récupération par auteur
        given()
        .when()
            .get("/api/messages/author/{author}", author)
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("every { it.author == '" + author + "' }", is(true));
    }
}
