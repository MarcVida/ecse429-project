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
public class ProjectTest {

    private static final String projectBody = "{" +
        "\"title\": \"New Project\",\n" +
        "\"description\": \"Description of new project\"\n" +
        "}";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567/projects";
    }

    @Test
    public void testGetAllProjects() {
        given()
        .when()
            .get("")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("projects.size()", greaterThan(0));
    }

    @Test
    public void testPostProject() {
        String idString = given()
        .when()
            .body(projectBody)
            .contentType(ContentType.JSON)
            .post("")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("New Project"))
            .body("description", equalTo("Description of new project"))
            .extract().path("id");

        // Clean up - delete the created project
        delete("/" + Integer.parseInt(idString));
    }

    @Test
    public void testPostProjectInvalidFormat() {
        given()
        .when()
            .body("[{\"title\": \"Invalid Project\", \"description\": \"Invalid project format\"}]")
            .post("")
        .then()
            .statusCode(400)
            .body("errorMessages", notNullValue());
    }

    @Test
    public void testHeadAllProjects() {
        given()
        .when()
            .head("")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void testPutNotAllowed() {
        given()
        .when()
            .body(projectBody)
            .put("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testDeleteNotAllowed() {
        given()
        .when()
            .delete("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testPatchNotAllowed() {
        given()
        .when()
            .patch("")
        .then()
            .statusCode(405);
    }

    @Test
    public void testOptionsProjects() {
        given()
        .when()
            .options("")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST"));
    }

    @Test
    public void testGetSpecificProject() {
        // Test valid project ID
        given()
        .when()
            .get("/5")
        .then()
            .statusCode(200)
            .body("projects[0].id", equalTo("5"))  // Access the first project in the array
            .body("projects[0].title", equalTo("New Project"))
            .body("projects[0].description", equalTo("Description of new project"))
            .body("projects[0].completed", equalTo("false"))
            .body("projects[0].active", equalTo("false"));

        // Test invalid project ID
        given()
        .when()
            .get("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPutSpecificProject() {
        // Test updating a valid project
        given()
        .when()
            .body(projectBody)
            .put("/5")
        .then()
            .statusCode(200)
            .body("title", equalTo("New Project"))
            .body("description", equalTo("Description of new project"));

        // Test updating an invalid project ID
        given()
        .when()
            .body(projectBody)
            .put("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPostToSpecificProject() {
        // Test posting to a specific project
        given()
        .when()
            .body(projectBody)
            .post("/5")
        .then()
            .statusCode(200)
            .body("title", equalTo("New Project"))
            .body("description", equalTo("Description of new project"));

        // Test posting to a non-existent project
        given()
        .when()
            .body(projectBody)
            .post("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testOptionsForSpecificProject() {
        // Test OPTIONS for a specific project
        given()
        .when()
            .options("/1")
        .then()
            .statusCode(200)
            .header("allow", containsString("OPTIONS, GET, HEAD, POST, PUT, DELETE"));

        // Test OPTIONS for a non-existent project
        given()
        .when()
            .options("/999")
        .then()
            .statusCode(200) // Still returns 200 because OPTIONS doesn't check existence
            .header("allow", containsString("OPTIONS, GET, HEAD, POST, PUT, DELETE"));
    }

    @Test
    public void testHeadForSpecificProject() {
        // Test HEAD request for an existing project
        given()
        .when()
            .head("/5")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        // Test HEAD request for a non-existent project
        given()
        .when()
            .head("/999")
        .then()
            .statusCode(404);
    }

    @Test
    public void testPatchMethodNotAllowed() {
        // Test PATCH method (not allowed)
        given()
        .when()
            .patch("/1")
        .then()
            .statusCode(405); // Method not allowed
    }

    @Test
    public void testGetAllTasksForProject() {
        // Test retrieving all tasks (todos) for a specific project
        given()
        .when()
            .get("/3/tasks")
        .then()
            .statusCode(200)
            .body("todos.size()", greaterThan(0))  // Ensure there's at least one todo item
            .body("todos[0].id", notNullValue())   // Check that the first todo has an id
            .body("todos[0].title", notNullValue())  // Ensure the todo has a title
            .body("todos[0].doneStatus", notNullValue()) // Ensure the completion status is present
            .body("todos[0].tasksof.size()", greaterThan(0))  // Ensure there are related tasks
            .body("todos[0].tasksof[0].id", notNullValue());  // Check the related task ID
    }

    @Test
    public void testHeadForTasksForProject() {
        // Test HEAD request for tasks related to a specific project
        given()
        .when()
            .head("/5/tasks")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void testPostTaskRelationshipForProject() {
        // Test creating a relationship between a project and a task by specifying task id in the body
        String requestBody = "{" +
            "\"id\": \"5\"" +
        "}";

        given()
        .when()
            .body(requestBody)
            .contentType(ContentType.JSON)
            .post("/5/tasks")
        .then()
            .statusCode(201);

        // Test invalid case (Bad Request 400)
        String invalidRequestBody = "{" +
            "\"id\": \"invalid-id\"" +  // Invalid task id
        "}";

        given()
        .when()
            .body(invalidRequestBody)
            .contentType(ContentType.JSON)
            .post("/5/tasks")
        .then()
            .statusCode(404)
            .body("errorMessages", notNullValue());  // Check for error messages in the response
    }

    @Test
    public void testDeleteTaskRelationshipForProject() {
        // Test successful deletion of the relationship between project and task
        given()
        .when()
            .delete("/19/tasks/2")
        .then()
            .statusCode(200);

        // Test case where the relationship does not exist (Not Found 404)
        given()
        .when()
            .delete("/5/tasks/999")
        .then()
            .statusCode(404)
            .body("errorMessages", notNullValue());  // Check for error messages in the response
    }

    @Test
    public void testGetAllCategoriesForProject() {
        // Test retrieving all categories for a specific project
        given()
        .when()
            .get("/5/categories")
        .then()
            .statusCode(200);
    }

    @Test
    public void testHeadForCategoriesForProject() {
        // Test HEAD request for categories related to a specific project
        given()
        .when()
            .head("/5/categories")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);  // Ensure the content type is JSON
    }

    @Test
    public void testPostCategoryRelationshipForProject() {
        // Test creating a relationship between a project and a category by specifying category id in the body
        String requestBody = "{" +
            "\"title\": \"new category\"" +
        "}";

        given()
        .when()
            .body(requestBody)
            .contentType(ContentType.JSON)
            .post("/5/categories")
        .then()
            .statusCode(201);
        // Test invalid case (Bad Request 400)
        String invalidRequestBody = "{" +
            "\"id\": \"invalid-id\"" +  // Invalid category id
        "}";

        given()
        .when()
            .body(invalidRequestBody)
            .contentType(ContentType.JSON)
            .post("/5/categories")
        .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteCategory() {
        // Test successful deletion of a category
        given()
        .when()
            .delete("5/categories/17")
        .then()
            .statusCode(200);

        // Test case where the category does not exist (Not Found 404)
        given()
        .when()
            .delete("/categories/999")
        .then()
            .statusCode(404)
            .body("errorMessages", notNullValue());  // Check for error messages in the response
    }
}
