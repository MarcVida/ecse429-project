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

    private static final String todo2InitialBody = "{" +
        "\"title\": \"file paperwork\",\r\n" +
        "\"doneStatus\": false,\r\n" +
        "\"description\": \"\",\r\n" +
        "\"tasksof\": [\r\n" +
        "    {\r\n" +
        "        \"id\": \"1\"\r\n" +
        "    }\r\n" +
        "]}";

    private static final String dummyTitle = "buy groceries";
    private static final String dummyDescription = "get apples, bananas, oranges";
    private static final String dummyBodyExample = "{ \"title\": \""+dummyTitle+"\", \"doneStatus\": \"false\", \"description\": \""+dummyDescription+"\" }"; // dummy body from example in API documentation
    private static final String dummyBody = "{ \"title\": \""+dummyTitle+"\", \"doneStatus\": false, \"description\": \""+dummyDescription+"\" }"; // dummy body with doneStatus as a boolean
    private static final String dummyBodyDone = "{ \"title\": \""+dummyTitle+"\", \"doneStatus\": true, \"description\": \""+dummyDescription+"\" }"; // dummy body with doneStatus set to true
    private static final String dummyBodyDone2 = "{ \"title\": \""+dummyTitle+"\", \"doneStatus\": true, \"description\": \"get grapes, pineapples, watermelons\" }"; // another dummy body with doneStatus set to true

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
    public void testGetAllJSON() {
        given()
        .when()
            .accept(ContentType.JSON)
            .get("")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", greaterThan(0))
            .body("todos.size()", greaterThan(0));
    }

    @Test
    public void testGetAllXML() {
        given()
        .when()
            .accept(ContentType.XML)
            .get("")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/todos"));
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
    public void testPostFromExample() {
        // Follows the same JSON structure as in the API documentation example
        String idString = given()
        .when()
            .body(dummyBodyExample)
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"))
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testPost() {
        // Sets doneStatus as a boolean
        String idString = given()
        .when()
            .body(dummyBody)
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"))
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostJSON() {
        String idString = given()
        .when()
            .contentType(ContentType.JSON)
            .body("{ \"title\": \"buy food\", \"doneStatus\": false, \"description\": \"get apples, bananas, oranges\" }")
            .post("")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("title", equalTo("buy food"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"))
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostXMLFromExample() {
        // Follows the same XML structure as in the API documentation example
        String idString = given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get apples, bananas, oranges</description><id>null</id><title>buy food 2</title></todo>")
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy food 2"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"))
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostXML() {
        // Does not incude id in the request body
        String idString = given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get apples, bananas, oranges</description><title>buy food 2</title></todo>")
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy food 2"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"))
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostInvalidBody() {
        given()
        .when()
            .contentType(ContentType.JSON)
            .body("<todo><doneStatus>false</doneStatus><description>get apples, bananas, oranges</description><title>buy food 2</title></todo>")
            .post("")
        .then()
            .statusCode(400)
            .body("errorMessages", notNullValue())
            .body(not(containsString("java.lang."))); // The error message should be user friendly
    }

    @Test
    public void testPutJSON() {
        given()
        .when()
            .contentType(ContentType.JSON)
            .body(dummyBody)
            .put("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testPatchAll() {
        given()
        .when()
            .body(dummyBody)
            .patch("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testDeleteAll() {
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
    public void testGetWithIdNotFound() {
        given()
        .when()
            .get("/999")
        .then()
            .statusCode(404)
            .body("errorMessages", notNullValue())
            .body(not(containsString("java.lang."))); // The error message should be user friendly
    }

    @Test
    public void testGetWithIdXML() {
        given()
        .when()
            .accept(ContentType.XML)
            .get("/1")
        .then()
            .statusCode(200)
            .body(hasXPath("/todos"))
            .body(containsString("<id>1</id>"))
            .body(containsString("<title>scan paperwork</title>"))
            .body(containsString("<doneStatus>false</doneStatus>"))
            .body(containsString("<description/>"));
    }

    @Test
    public void testHeadWithIdXML() {
        given()
        .when()
        .accept(ContentType.XML)
            .head("/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML);
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
    public void testPostWithIdFromExample() {
        // Update item 1 using the same JSON structure as in the example in the API documentation
        given()
        .when()
            .body(dummyBodyExample)
            .post("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"));

        // Revert back to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .post("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostWithId() {
        // Update item 1 with doneStatus as a boolean
        given()
        .when()
            .body(dummyBody)
            .post("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"));

        // Revert back to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .post("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostWithIdXML() {
        // Update item 1 with doneStatus as a boolean
        given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get apples, bananas, oranges</description><title>buy food again</title></todo>")
            .post("/1")
        .then()
        .statusCode(200)
        .body("id", equalTo("1"))
        .body("title", equalTo("buy food again"))
        .body("doneStatus", equalTo("false"))
        .body("description", equalTo("get apples, bananas, oranges"));

        // Revert back to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .post("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPostWithIdXMLAcceptXML() {
        given()
        .when()
            .accept(ContentType.XML)
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get apples, bananas, oranges</description><title>buy some food</title></todo>")
            .post("/1")
        .then()
        .statusCode(200)
        .body(containsString("<id>1</id>"))
        .body(containsString("<title>buy some food</title>"))
        .body(containsString("<doneStatus>false</doneStatus>"))
        .body(containsString("<description>get apples, bananas, oranges</description>"));

        // Revert back to initial state
        given()
        .when()
            .body(todo1InitialBody)
            .post("/1")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithIdFromExample() {
        // Update item 2 using the same JSON structure as in the example in the API documentation
        given()
        .when()
            .body("{ \"title\": \"buyd even more food\", \"doneStatus\": \"false\", \"description\": \"get apples, bananas, oranges\" }")
            .put("/2")
        .then()
            .statusCode(200)
            .body("id", equalTo("2"))
            .body("title", equalTo("buyd even more food"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"));

        // Revert back to initial state
        given()
        .when()
            .body(todo2InitialBody)
            .put("/2")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithId() {
        // Update item 2 with doneStatus as a boolean
        given()
        .when()
            .body("{ \"title\": \"buyd even more food\", \"doneStatus\": false, \"description\": \"get apples, bananas, oranges\" }")
            .put("/2")
        .then()
            .statusCode(200)
            .body("id", equalTo("2"))
            .body("title", equalTo("buyd even more food"))
            .body("doneStatus", equalTo("false"))
            .body("description", equalTo("get apples, bananas, oranges"));

        // Revert back to initial state
        given()
        .when()
            .body(todo2InitialBody)
            .put("/2")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithIdXML() {
        given()
        .when()
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get pineapples, bananas, grapes</description><title>go to supermarket</title></todo>")
            .put("/2")
        .then()
        .statusCode(200)
        .body("id", equalTo("2"))
        .body("title", equalTo("go to supermarket"))
        .body("doneStatus", equalTo("false"))
        .body("description", equalTo("get pineapples, bananas, grapes"));

        // Revert back to initial state
        given()
        .when()
            .body(todo2InitialBody)
            .put("/2")
        .then()
            .statusCode(200);
    }

    @Test
    public void testPutWithIdXMLAcceptXML() {
        given()
        .when()
            .accept(ContentType.XML)
            .contentType(ContentType.XML)
            .body("<todo><doneStatus>false</doneStatus><description>get strawberries, mangoes, grapes</description><title>buy good food</title></todo>")
            .put("/2")
        .then()
        .statusCode(200)
        .body(containsString("<id>2</id>"))
        .body(containsString("<title>buy good food</title>"))
        .body(containsString("<doneStatus>false</doneStatus>"))
        .body(containsString("<description>get strawberries, mangoes, grapes</description>"));

        // Revert back to initial state
        given()
        .when()
            .body(todo2InitialBody)
            .put("/2")
        .then()
            .statusCode(200);
    }

    @Test
    public void testDeleteWithIdNotFound() {
        given()
        .when()
            .delete("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteWithId() {
        // create item
        String idString = given()
        .when()
            .body(dummyBody)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);

        // confirm item was deleted
        given()
        .when()
            .get("/" + idString)
        .then()
            .statusCode(404);
    }

    @Test
    public void testHeadWithIdNotFound() {
        given()
        .when()
            .head("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testOptionsWithNonExistingID() {
        given()
        .when()
            .options("/999")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST, PUT, DELETE"));
    }

    @Test
    public void testOptionsWithID() {
        given()
        .when()
            .options("/1")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST, PUT, DELETE"));
    }

    @Test
    public void testPatchWithId() {
        given()
        .when()
            .body(dummyBody)
            .patch("/1")
        .then()
            .statusCode(405);
    }

    @Test
    public void testGetFilteredByDoneStatusTrue() {
        // create item
        String idString = given()
        .when()
            .body(dummyBodyDone)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // get item by filtering on doneStatus
        given()
        .when()
            .queryParam("doneStatus","true")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].doneStatus", equalTo("true"));

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testGetFilteredByDoneStatusFalse() {
        // create item
        String idString = given()
        .when()
            .body(dummyBodyDone)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // get item by filtering on doneStatus
        given()
        .when()
            .queryParam("doneStatus","false")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", greaterThan(0))
            .body("todos[0].doneStatus", equalTo("false"))
            .body(not(containsString(dummyTitle)));

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testGetFilteredByTitle() {
        given()
        .when()
            .queryParam("title","file paperwork")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].title", equalTo("file paperwork"));
    }

    @Test
    public void testGetFilteredByDescription() {
        // create item
        String idString = given()
        .when()
            .body(dummyBody)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");

        // get item by filtering on description
        given()
        .when()
            .queryParam("description",dummyDescription)
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", equalTo(idString))
            .body("todos[0].description", equalTo("get apples, bananas, oranges"));

        // delete item
        given()
        .when()
            .delete("/" + idString)
        .then()
            .statusCode(200);
    }

    @Test
    public void testGetFilteredById() {
        given()
        .when()
            .queryParam("id","2")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", equalTo("2"));
    }

    @Test
    public void testGetFilteredByNonExistingId() {
        given()
        .when()
            .queryParam("id","999")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(0));
    }

    @Test
    public void testGetFilteredByNonExistingTitle() {
        given()
        .when()
            .queryParam("title","non-existing-title")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(0));
    }

    @Test
    public void testGetFilteredByInvalidStatus() {
        given()
        .when()
            .queryParam("doneStatus","invalid")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(0));
    }

    @Test
    public void testGetFilteredByNonExistingDescription() {
        given()
        .when()
            .queryParam("description","non-existing-description")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(0));
    }

    @Test
    public void testGetFilteredByNonExistingAttribute() {
        given()
        .when()
            .queryParam("non-existing-attribute","non-existing-value")
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(0));
    }

    @Test
    public void testGetFilteredByDoneStatusAndDescription() {
        // create items
        String id1String = given()
        .when()
            .body(dummyBodyDone)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");
        
        String id2String = given()
        .when()
            .body(dummyBodyDone2)
            .post("")
        .then()
            .statusCode(201)
            .extract().path("id");
        
        // get item by filtering on doneStatus and description
        given()
        .when()
            .queryParam("doneStatus","true")
            .queryParam("description",dummyDescription)
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", equalTo(id1String));

        // delete items
        given()
        .when()
            .delete("/" + id1String)
        .then()
            .statusCode(200);
        given()
        .when()
            .delete("/" + id2String)
        .then()
            .statusCode(200);
    }

}
