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

    private static final String todo1InitialBody = "{" +
        "\"title\": \"scan paperwork\",\n" +
        "\"doneStatus\": false,\n" +
        "\"description\": \"\"," +
        "\"tasksof\": [\n" +
        "  {\n" + 
        "    \"id\": \"1\"\n" + 
        "  }\n" + 
        "],\n" + 
        "\"categories\": [\n" + 
        "  {\n" + 
        "    \"id\": \"1\"\n" + 
        "  }\n" + 
        "]}";

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
            .statusCode(400)
            .body("errorMessages", notNullValue())
            .body(not(containsString("java.lang."))); // The error message should be user friendly
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

    @Test
    public void testGetWithId() {
        given()
        .when()
            .get("/1")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", equalTo("1"))
            .body("todos[0].title", equalTo("scan paperwork"))
            .body("todos[0].doneStatus", equalTo("false"))
            .body("todos[0].description", equalTo(""));
    }

    @Test
    public void testGetWithIdXML() {
        given()
        .when()
            .accept("application/xml")
            .get("/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/todos"));
        // TODO: Is it okay not to check the todo item ID?
    }

    @Test
    public void testGetWithIdNotFound() {
        given()
        .when()
            .get("/100")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPutWithId() {
        given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .put("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"));

        // Revert to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .put("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithIdXML() {
        given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><title>buy groceries</title><description>buy apples, bananas, oranges</description></todo>")
            .put("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"));

        // Revert to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .put("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithIdNotFound() {
        given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .put("/100")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPostWithId() {
        given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .post("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"));

        // Revert to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .put("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostWithIdXML() {
        given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><title>buy groceries</title><description>buy apples, bananas, oranges</description></todo>")
            .post("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("buy apples, bananas, oranges"));

        // Revert to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .put("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostWithIdNotFound() {
        given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .post("/100")
        .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteWithId() {
        // Create a new todo
        String idString = given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // Delete the todo
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);

        // Confirm the todo was deleted
        given()
        .when()
            .get("/" + idString)
        .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteWithIdNotFound() {
        given()
        .when()
            .delete("/100")
        .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteWithIdAlreadyDeleted() {
        // Create a new todo
        String idString = given()
        .when()
            .body("{\"title\": \"buy groceries\", \"description\": \"buy apples, bananas, oranges\"}")
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // Delete the todo
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);

        // Try to delete the todo again
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(404);
    }

    @Test
    public void testOptionsWithId() {
        given()
        .when()
            .options("/1")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST, PUT, DELETE"));
    }

    @Test
    public void testHeadWithId() {
        given()
        .when()
            .head("/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void testHeadWithIdNotFound() {
        given()
        .when()
            .head("/100")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPatchWithId() {
        given()
        .when()
            .patch("/1")
        .then()
            .statusCode(405);
    }

    // TODO: Remove this list
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

    // get with id DONE
    // get with id XML DONE
    // get with id not found DONE
    // put with id DONE
    // put with id XML DONE
    // put with id not found DONE
    // post with id DONE
    // post with id XML DONE
    // post with id not found DONE
    // delete with id DONE
    // delete with id not found DONE
    // delete with id already deleted DONE
    // options with id DONE
    // head with id DONE
    // head with id not found DONE
    // patch with id DONE

}
