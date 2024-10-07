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

    private static final String projectBodyXml = "<project>" +
    "<title>New Project</title>" +
    "<description>Description of new project</description>" +
    "</project>";

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
    public void testGetAllProjects_XML() {
        given()
        .accept(ContentType.XML)
        .when()
            .get("")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/projects/project"));
    }

    @Test
    public void testPostProject_XML() {
        String idString = given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .post("")
        .then()
            .statusCode(201)
            .contentType(ContentType.XML)
            .body(hasXPath("/project/id"))
            .body(hasXPath("/project/title[text()='New Project']"))
            .body(hasXPath("/project/description[text()='Description of new project']"))
            .extract()
            .path("project.id");

        // Clean up - delete the created project
        delete("/" + idString);
    }

    @Test
    public void testPutProject_XML() {
        // Assuming a valid project exists with id=5
        given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .put("/5")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/project/title[text()='New Project']"))
            .body(hasXPath("/project/description[text()='Description of new project']"));
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
    public void testPostProjectInvalidFormat_XML() {
        String invalidXmlBody = "<projects>" +
            "<project>" +
            "<title>Invalid Project</title>" +
            "<description>Invalid project format" +  // Missing closing tag for description
            "</project>" +
            "</projects>";

        given()
        .body(invalidXmlBody)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .post("")
        .then()
            .statusCode(400)
            .body("errorMessages", notNullValue());  // Check for error messages indicating bad format
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
    public void testGetSpecificProject_XML() {
        // Test valid project ID
        given()
        .accept(ContentType.XML)
        .when()
            .get("/5")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(hasXPath("/projects/project/id[text()='5']"))  // Check project ID
            .body(hasXPath("/projects/project/title[text()='New Project']"))  // Check project title
            .body(hasXPath("/projects/project/description[text()='Description of new project']"))  // Check project description
            .body(hasXPath("/projects/project/completed[text()='false']"))  // Check project completed status
            .body(hasXPath("/projects/project/active[text()='false']"));  // Check project active status

        // Test invalid project ID
        given()
        .accept(ContentType.XML)
        .when()
            .get("/999")
        .then()
            .statusCode(404);  // No need to check body, just ensure 404 is returned
    }

    @Test
    public void testPutSpecificProject_XML() {
        String projectBodyXml = "<project>" +
            "<title>New Project</title>" +
            "<description>Description of new project</description>" +
            "</project>";

        // Test updating a valid project
        given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .put("/5")
        .then()
            .statusCode(200)
            .body(hasXPath("/project/title[text()='New Project']"))  // Check updated title
            .body(hasXPath("/project/description[text()='Description of new project']"));  // Check updated description

        // Test updating an invalid project ID
        given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .put("/999")
        .then()
            .statusCode(404);  // No need to check body, just ensure 404 is returned
    }

    @Test
    public void testPostToSpecificProject_XML() {
        String projectBodyXml = "<project>" +
            "<title>New Project</title>" +
            "<description>Description of new project</description>" +
            "</project>";

        // Test posting to a specific project
        given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .post("/5")
        .then()
            .statusCode(200)
            .body(hasXPath("/project/title[text()='New Project']"))  // Check that the title is "New Project"
            .body(hasXPath("/project/description[text()='Description of new project']"));  // Check description

        // Test posting to a non-existent project
        given()
        .body(projectBodyXml)
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
            .post("/999")
        .then()
            .statusCode(404);  // No need to check body, just ensure 404 is returned
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
    public void testGetAllTasksForProject_XML() {
        // Test retrieving all tasks (todos) for a specific project in XML format
        given()
        .accept(ContentType.XML)  // Request XML format
        .when()
            .get("/3/tasks")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)  // Ensure the response is in XML format
            .body(hasXPath("/todos/todo"))  // Ensure that there is at least one <todo> element
            .body(hasXPath("/todos/todo/id"))  // Ensure that the first <todo> element has an <id>
            .body(hasXPath("/todos/todo/title"))  // Ensure that the <todo> element has a <title>
            .body(hasXPath("/todos/todo/doneStatus"));  // Ensure that the <todo> element has a <doneStatus>
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
    public void testPostTaskRelationshipForProject_XML() {
        // Test creating a relationship between a project and a task by specifying task details in XML
        String requestBodyXml = "<task>" +
            "<title>new work 3</title>" +
            "<doneStatus>false</doneStatus>" +
            "<description></description>" +
            "<tasksof>" +
                "<task><id>5</id></task>" +  // Related task id
            "</tasksof>" +
        "</task>";

        given()
        .when()
            .body(requestBodyXml)  // Use XML format
            .contentType(ContentType.XML)
            .accept(ContentType.XML)  // Request response in XML
            .post("/5/tasks")  // Assuming project with id=5 exists
        .then()
            .statusCode(201);

        // Test invalid case (Bad Request 400) with incorrect task ID
        String invalidRequestBodyXml = "<task>" +
            "<title>new work 3</title>" +
            "<doneStatus>false</doneStatus>" +
            "<description></description>" +
            "<tasksof>" +
                "<task><id>invalid-id</id></task>" +  // Invalid task id
            "</tasksof>" +
        "</task>";

        given()
        .when()
            .body(invalidRequestBodyXml)  // Use XML format for invalid request
            .contentType(ContentType.XML)
            .accept(ContentType.XML)
            .post("/5/tasks")
        .then()
            .statusCode(404)
            .body(hasXPath("/errorMessages"));  // Check for error messages in the response
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
    public void testGetAllCategoriesForProject_XML() {
        // Test retrieving all categories for a specific project in XML format
        given()
        .accept(ContentType.XML)  // Request XML format
        .when()
            .get("/5/categories")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML)  // Ensure the content type is XML
            .body(hasXPath("/categories/category"));  // Ensure that at least one <category> is returned
    }

    @Test
    public void testHeadForCategoriesForProject_XML() {
        // Test HEAD request for categories related to a specific project in XML format
        given()
        .accept(ContentType.XML)  // Request XML format
        .when()
            .head("/5/categories")
        .then()
            .statusCode(200)
            .contentType(ContentType.XML);  // Ensure the content type is XML
    }

    @Test
    public void testPostCategoryRelationshipForProject_XML() {
        // Test creating a relationship between a project and a category by specifying category title in the body (XML)
        String requestBodyXml = "<category>" +
            "<title>new category</title>" +
        "</category>";

        given()
        .body(requestBodyXml)  // Use XML format
        .contentType(ContentType.XML)
        .accept(ContentType.XML)  // Request response in XML
        .when()
            .post("/5/categories")
        .then()
            .statusCode(201);

        // Test invalid case (Bad Request 400) with incorrect category ID
        String invalidRequestBodyXml = "<category>" +
            "<id>invalid-id</id>" +  // Invalid category id
        "</category>";

        given()
        .body(invalidRequestBodyXml)  // Use XML format for invalid request
        .contentType(ContentType.XML)
        .accept(ContentType.XML)
        .when()
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
