package unittestsuite;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

public class ShutdownTest {

    @Test
    public void testShutdown() {
        RestAssured.baseURI = "http://localhost:4567/";
        
        // Check that server is running
        given()
        .when()
            .get("")
        .then()
            .statusCode(200);

        // Shutdown server
        try {
            given()
            .when()
                .get("shutdown")
            .then();
        }
        catch (Exception e) { }

        // Check that server is not running
        try {
            given()
            .when()
                .get("")
            .then();
            fail("Server did not shutdown");
        }
        catch (Exception e) { }
    }
}
