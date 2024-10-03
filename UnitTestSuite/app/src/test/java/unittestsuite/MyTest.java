package unittestsuite;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

/**
 * This class contains test methods that will be executed in a random order
 */
@TestMethodOrder(MethodOrderer.Random.class)
public class MyTest {
    
    /**
     * This method will be executed before all tests
     */
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://www.google.ca";
    }

    /**
     * Sends a request to google and checks that status code is 200
     */
    @Test
    public void testGoogle() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200);
    }

    /**
     * Sends a search request to google and checks that status code is 200
     */
    @Test
    public void testGoogleSearch() {
        given()
        .when()
            .get("/search?q=hello+world")
        .then()
            .statusCode(200);
    }

}
