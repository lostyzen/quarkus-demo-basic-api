package org.acme.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class MessageResourceTest {

    @Test
    public void testGetMessagesInitiallyEmpty() {
        // Test avec le nouveau endpoint de l'architecture hexagonale
        RestAssured.given()
            .when().get("/api/messages")
            .then().statusCode(200)
            .body("size()", is(0));
    }

    @Test
    public void testAddMessageAndGet() {
        // Ajout d'un message avec le nouveau format incluant l'auteur (obligatoire)
        String content = "Hello Test Architecture Hexagonale!";
        String author = "Test Author";

        String id = RestAssured.given()
            .contentType("application/json")
            .body(String.format("""
                {
                    "content": "%s",
                    "author": "%s"
                }
                """, content, author))
            .when().post("/api/messages")
            .then().statusCode(201)  // 201 Created au lieu de 200
            .body("content", equalTo(content))
            .body("author", equalTo(author))
            .body("status", equalTo("DRAFT"))  // Nouveau statut par défaut
            .extract().path("id");

        // Vérification qu'il est bien présent dans la liste
        RestAssured.given()
            .when().get("/api/messages")
            .then().statusCode(200)
            .body("find { it.id == '" + id + "' }.content", equalTo(content))
            .body("find { it.id == '" + id + "' }.author", equalTo(author))
            .body("size()", greaterThan(0));
    }
}
