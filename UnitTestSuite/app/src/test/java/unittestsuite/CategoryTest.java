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
public class CategoryTest {

    private static final String todo1InitialBody = "{" +
        "\"title\": \"Office\",\n" +
        "\"description\": \"\"" +
        "}";

    private static final String todo2InitialBody = "{" +
        "\"title\": \"Home\",\n" +
        "\"description\": \"\"" +
        "}";

    //dummy variable values were reused across the APIs    
    private static final String dummyTitle = "buy groceries";
    private static final String dummyDescription = "get apples, bananas, oranges";
    private static final String dummyBodyExample = "{ \"title\": \""+dummyTitle+"\", \"description\": \""+dummyDescription+"\" }"; // dummy body 
    private static final String dummyBody = "{ \"title\": \""+dummyTitle+"\", \"description\": \""+dummyDescription+"\" }"; // dummy body 
    private static final String dummyBodyDone = "{ \"title\": \""+dummyTitle+"\", \"description\": \""+dummyDescription+"\" }"; // dummy body 
    private static final String dummyBodyDone2 = "{ \"title\": \""+dummyTitle+"\", \"description\": \"get grapes, pineapples, watermelons\" }"; // another dummy body 

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567/categories";
    }

    @Test
    public void testGetAll() {
        given()
        .when()
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("categories.size()", greaterThan(0));
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
            .body("categories.size()", greaterThan(0));
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
            .body(hasXPath("/categories"));
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
    public void testPost() {
        String idString = given()
        .when()
            .body(dummyBody)
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy groceries"))
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
            .body("{ \"title\": \"buy food\", \"description\": \"get apples, bananas, oranges\" }")
            .post("")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("title", equalTo("buy food"))
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
    public void testPostJSONMalformed() {
        String idString = given()
        .when()
            .contentType(ContentType.JSON)
            .body("{ \"title\": \"\", \"description\": \"get apples, bananas, oranges\" }") 
            .post("")
        .then()
            .statusCode(400) //an empty title should be considered as malformed input from the API
            .body("errorMessages", notNullValue())
            .body(not(containsString("java.lang."))) // The error message should be user friendly
            .extract().path("id");

        if(idString != null){ //only attempt to delete the resource if it was incorrectly created in the first place
            // delete item
            given()
            .when()
                .delete("/" + idString)
            .then()
                .statusCode(200);
        }
    }

    @Test
    public void testPostXMLFromExample() {
        // Follows the same XML structure as in the API documentation example
        String idString = given()
        .when()
            .contentType(ContentType.XML)
            .body("<category><description>get apples, bananas, oranges</description><id>null</id><title>buy food 2</title></category>") //null id field is incorrectly considered as malformed input from the API
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy food 2"))
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
            .body("<category><description>get apples, bananas, oranges</description><title>buy food 2</title></category>")
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("buy food 2"))
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
        // Does not incude id in the request body as shown in the example of the API docs and sent XML when method initally specified to send JSON 
        given()
        .when()
            .contentType(ContentType.JSON)
            .body("<category><description>get apples, bananas, oranges</description><title>buy food 2</title></category>")
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
            .body("categories.size()", equalTo(1))
            .body("categories[0].id", equalTo("1"))
            .body("categories[0].title", equalTo("Office"))
            .body("categories[0].description", equalTo(""));
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
            .body(hasXPath("/categories"))
            .body(containsString("<id>1</id>"))
            .body(containsString("<title>Office</title>"))
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
    public void testPostWithId() {
        given()
        .when()
            .body(dummyBody)
            .post("/1")
        .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("title", equalTo("buy groceries"))
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
        given()
        .when()
            .contentType(ContentType.XML)
            .body("<category><description>get apples, bananas, oranges</description><title>buy food again</title></category>")
            .post("/1")
        .then()
        .statusCode(200)
        .body("id", equalTo("1"))
        .body("title", equalTo("buy food again"))
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
            .body("<category><description>get apples, bananas, oranges</description><title>buy some food</title></category>")
            .post("/1")
        .then()
        .statusCode(200)
        .body(containsString("<id>1</id>"))
        .body(containsString("<title>buy some food</title>"))
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
            .body("{ \"title\": \"\", \"description\": \"get apples, bananas, oranges\" }") 
            .put("/2")
        .then()
            .statusCode(400) //an empty title should be considered as malformed input from the API
            .body("errorMessages", notNullValue())
            .body(not(containsString("java.lang."))); // The error message should be user friendly

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
        given()
        .when()
            .body("{ \"title\": \"buy even more food\", \"description\": \"get apples, bananas, oranges\" }")
            .put("/2")
        .then()
            .statusCode(200)
            .body("id", equalTo("2"))
            .body("title", equalTo("buy even more food"))
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
            .body("<category><description>get pineapples, bananas, grapes</description><title>go to supermarket</title></category>")
            .put("/2")
        .then()
        .statusCode(200)
        .body("id", equalTo("2"))
        .body("title", equalTo("go to supermarket"))
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
            .body("<category><description>get strawberries, mangoes, grapes</description><title>buy good food</title></category>")
            .put("/2")
        .then()
        .statusCode(200)
        .body(containsString("<id>2</id>"))
        .body(containsString("<title>buy good food</title>"))
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
    public void testGetFilteredByTitle() {
        given()
        .when()
            .queryParam("title","Home")  
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("categories.size()", equalTo(1))
            .body("categories[0].title", equalTo("Home")); 
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
            .body("categories.size()", equalTo(1))
            .body("categories[0].id", equalTo(idString))
            .body("categories[0].description", equalTo("get apples, bananas, oranges"));

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
            .body("categories.size()", equalTo(1))
            .body("categories[0].id", equalTo("2"));
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
            .body("categories.size()", equalTo(0));
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
            .body("categories.size()", equalTo(0));
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
            .body("categories.size()", equalTo(0));
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
            .body("categories.size()", equalTo(0)); //the API lists all the categories when it should list none 
    }

    @Test
    public void testGetFilteredByTitleAndDescription() {
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
        
        // get item by filtering on title and description
        given()
        .when()
            .queryParam("title",dummyTitle)
            .queryParam("description",dummyDescription)
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("categories.size()", equalTo(1))
            .body("categories[0].id", equalTo(id1String));

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
