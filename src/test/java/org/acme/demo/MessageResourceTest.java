package org.acme.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class MessageResourceTest {

    @Test
    public void testGetMessagesInitiallyEmpty() {
        RestAssured.given().when().get("/messages").then().statusCode(200).body("size()", is(0));
    }

    @Test
    public void testAddMessageAndGet() {
        // Ajout d'un message
        String content = "Hello Test!";
        String id = RestAssured.given().contentType("application/json").body("{\"content\":\"" + content + "\"}").when()
                .post("/messages").then().statusCode(200).body("content", equalTo(content)).extract().path("id");

        // Vérification qu'il est bien présent
        RestAssured.given().when().get("/messages").then().statusCode(200).body("content", hasItem(content)).body("id",
                hasItem(id));
    }
}
