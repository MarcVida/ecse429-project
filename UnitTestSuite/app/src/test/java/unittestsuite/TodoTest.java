package unittestsuite;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodoTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567/todos";
    }

    @Test
    public void testGetAll() {
        given()
        .when()
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", greaterThan(0));
    }

    @Test
    public void testGetAllXML() {
        given()
        .when()
            .accept("application/xml")
            .get("")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/todos"));
    }

    @Test
    public void testGetAllFiltered() {
        given()
        .when()
            .queryParam("title", "file paperwork")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].title", equalTo("file paperwork"));
    }

    @Test
    public void testPut() {
        given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .put("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testPost() {
        String idString = given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"))
            .extract().path("id");

        delete("/" + Integer.parseInt(idString));
    }

    @Test
    public void testPostXML() {
        String idString = given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><title>buy groceries</title><description>buy apples, bananas, oranges</description></todo>")
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"))
            .extract().path("id");

        delete("/" + Integer.parseInt(idString));
    }

    // TODO: Check post XML with XML response?

    @Test
    public void testPostFiltered() {
        String idString = given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .queryParam("title", "file paperwork") // the server should ignore this query string
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"))
            .extract().path("id");

        delete("/" + Integer.parseInt(idString));
    }

    @Test
    public void testPostInvalidFormat() {
        given()
        .when()
            .body("[{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}," +
                  " {\"title\": \"prepare for party\", \"description\": \"buy decorations, clean house\"}]")
            .post("")
        .then()
            .statusCode(400);
    }

    // TODO: Add tests for more invalid formats?

    @Test
    public void testDelete() {
        given()
        .when()
            .delete("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testOptions() {
        given()
        .when()
            .options("")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST"));
    }

    @Test
    public void testHead() {
        given()
        .when()
            .head("")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void testPatch() {
        given()
        .when()
            .patch("")
        .then()
            .statusCode(405);
    }

    // TODO:
    // get all DONE
    // get all XML DONE
    // get all filtered DONE
    // put DONE
    // post DONE
    // post XML DONE
    // post filtered DONE
    // post invalid format DONE
    // delete DONE
    // options DONE
    // head DONE
    // patch DONE

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
