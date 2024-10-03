package unittestsuite;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodoTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    @Test
    public void testGetAll() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200);
    }

    // TODO:
    // get all
    // get all XML
    // get all filtered
    // put
    // post
    // post XML
    // post filtered
    // post invalid format
    // delete
    // options
    // head
    // patch

    // get with id
    // get with id XML
    // get with id not found
    // put with id
    // put with id XML
    // put with id not found
    // post with id
    // post with id XML
    // post with id not found
    // delete with id
    // delete with id not found
    // delete with id already deleted
    // options with id
    // head with id
    // patch with id

}
