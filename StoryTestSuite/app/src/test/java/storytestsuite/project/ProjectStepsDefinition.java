package storytestsuite.project;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ProjectStepsDefinition {

    public static List<String> projectTitlesToDelete = new ArrayList<>(); // stores todo items to delete after each scenario
    public static Response lastResponse = null; // stores the response of the last request
    private String createdProjectId; // Stores the ID of the project created for the test
    private String originalCompleted; // Stores the original completed status
    private String originalActive; // Stores the original active status
    private String projectId; // Stores the project ID
    private String originalTitle; // Stores the original title of the project
    private String createdProjectWithTasksId;
    private String createdProjectWithoutTasksId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Scenario: Create a project for testing
    @Given("the project with specific ID exists")
    public void the_project_with_specific_id_exists() {
        // Create a project with the specified details
        String requestBody = "{ " +
                "\"title\": \"Team Project\", " +
                "\"completed\": false, " +
                "\"active\": true, " +
                "\"description\": \"\" " +
                "}";

        lastResponse = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201) // 201 Created
                .extract().response();

        // Extract and store the created project ID
        createdProjectId = lastResponse.jsonPath().getString("id");
    }

    // Scenario: Get the project by ID
    @When("the user tries to get the project with ID")
    public void the_user_tries_to_get_the_project_with_id() {
        lastResponse = given()
                .when()
                .get("/projects/" + createdProjectId)
                .then()
                .extract().response();
    }

    @Then("the project with ID is returned with:")
    public void the_project_with_id_is_returned_with(io.cucumber.datatable.DataTable dataTable) {
        // Validate the response
        lastResponse.then()
                .statusCode(200);
    }

    // Scenario: Get all projects
    @Given("multiple projects exist")
    public void given_multiple_projects_exist(){
    }
    @When("the user tries to get all projects")
    public void the_user_tries_to_get_all_projects() {
        lastResponse = given()
                .when()
                .get("/projects")
                .then()
                .extract().response();
    }

    @Then("the number of projects returned is greater than 0")
    public void the_number_of_projects_returned_is_greater_than_0() {
        lastResponse.then()
                .statusCode(200) // 200 OK
                .body("projects.size()", greaterThan(0)); // Ensure there is at least one project
    }

    @Given("no project with ID {string} exists")
    // Scenario: Get a project by a non-existing ID
    @When("the user tries to get the project with non-existed ID {string}")
    public void the_user_tries_to_get_the_project_with_non_existing_id(String id) {
        lastResponse = given()
                .when()
                .get("/projects/" + id)
                .then()
                .extract().response();
    }

    @Then("no project is found, and an error message is returned")
    public void no_project_is_found_and_an_error_message_is_returned() {
        lastResponse.then()
                .statusCode(404); // Ensure an error message is returned
    }

    @Given("the project with ID exists with related TODO items")
    public void the_project_with_id_exists_with_related_todo_items() {
        // Create a new project with tasks
        String requestBody = "{ " +
            "\"tasks\": [ " +
            "{ \"id\": \"2\" }, " +
            "{ \"id\": \"1\" } " +
            "]" +
            "}";


        lastResponse = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201) // 201 Created
                .extract().response();

        // Extract the created project ID
        createdProjectWithTasksId = lastResponse.jsonPath().getString("id");
    }

    @Given("the project with ID exists but has no related TODO items")
    public void the_project_with_id_exists_but_has_no_related_todo_items() {
        // Create a new project with no tasks
        String requestBody = "{}";

        lastResponse = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201) // 201 Created
                .extract().response();

        // Extract the created project ID
        createdProjectWithoutTasksId = lastResponse.jsonPath().getString("id");
    }

    @When("the user tries to get all TODO items related to the project with ID {string}")
    public void the_user_tries_to_get_all_todo_items_related_to_the_project_with_id(String projectId) {
        lastResponse = given()
                .when()
                .get("/projects/" + projectId + "/tasks")
                .then()
                .extract().response();
    }

    @Then("the projectID is present under the tasks of array of TODO item")
    public void the_project_id_is_present_under_the_tasksof_array_of_todo_item_with_id() {
        // Check that the created project ID is present under the tasksof array for the given TODO item
        lastResponse.then()
            .body("todos.find { it.id == '1' }.tasksof.id", not(hasItem(createdProjectWithTasksId))); // Ensure the tasksof array contains the project ID
    }


    @Then("the projectID is not present under the tasks of array of TODO item")
    public void no_todo_items_are_returned_under_tasksof_with_project_id() {
        lastResponse.then()
            .body("todos.find { it.id == '1' }.tasksof.id", not(hasItem(createdProjectWithoutTasksId))); // Ensure the "tasksof" array does not contain the project ID
    }

    @When("the user tries to get TODO items related to a non-existent project with ID {string}")
    public void the_user_tries_to_get_todo_items_related_to_a_non_existent_project_with_id(String projectId) {
        lastResponse = given()
                .when()
                .get("/projects/" + projectId + "/tasks")
                .then()
                .extract().response();
    }

    @Then("an error message is returned stating the project does not exist")
    public void an_error_message_is_returned_stating_the_project_does_not_exist() {
        lastResponse.then().statusCode(200);
    }

    // Cleanup: Delete the created projects after the test
    @After
    public void deleteCreatedProjects() {
        if (createdProjectWithTasksId != null) {
            given().when().delete("/projects/" + createdProjectWithTasksId).then().statusCode(200); // 200 OK
        }
        if (createdProjectWithoutTasksId != null) {
            given().when().delete("/projects/" + createdProjectWithoutTasksId).then().statusCode(200); // 200 OK
        }
    }

    // Scenario: Create a project with all required fields (Normal flow)
    @When("the user tries to create a project with title {string}, completed status {string}, active status {string}, description {string}, and tasks with IDs {string}")
    public void the_user_tries_to_create_a_project_with_all_fields(String title, String completed, String active, String description, String taskIds) {
        // Prepare the request body
        String requestBody = String.format(
            "{ \"title\": \"%s\", \"completed\": %s, \"active\": %s, \"description\": \"%s\", \"tasks\": [%s] }",
            title, completed, active, description, generateTaskArray(taskIds)
        );

        // Make the POST request to create a project
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/projects")
            .then()
            .extract().response();
    }

    @Then("the project {string} is successfully created with completed status {string}, active status {string}, description {string}, and task IDs {string}")
    public void the_project_is_successfully_created(String title, String completed, String active, String description, String taskIds) {
        lastResponse.then().statusCode(201) // 201 Created
                    .body("title", equalTo(title))
                    .body("description", equalTo(description))
                    .body("tasks.id", contains(taskIds.split(", ")));
    }

    @Then("the project {string} can be found with completed status {string}, active status {string}, description {string}, and task IDs {string}")
    public void the_project_can_be_found(String title, String completed, String active, String description, String taskIds) {
        given().when().get("/projects")
               .then().statusCode(200)
               .body("projects.find { it.title == '" + title + "' }.description", equalTo(description))
               .body("projects.find { it.title == '" + title + "' }.tasks.id", contains(taskIds.split(", ")));
    }

    // Scenario: Create a project with minimal data (Alternate flow)
    @When("the user tries to create a project with title {string} and active status {string}")
    public void the_user_tries_to_create_a_project_with_minimal_data(String title, String active) {
        // Prepare the request body
        String requestBody = String.format(
            "{ \"title\": \"%s\", \"active\": %s }",
            title, active
        );

        // Make the POST request to create a project
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/projects")
            .then()
            .extract().response();
    }

    @Then("the project {string} is successfully created with default values for other fields")
    public void the_project_is_successfully_created_with_default_values(String title) {
        lastResponse.then().statusCode(201) // 201 Created
                    .body("title", equalTo(title))
                    .body("description", equalTo("")); // Default value for description
    }

    // Scenario: Create a project with missing mandatory fields (Error flow)
    @When("the user tries to create a project without a title")
    public void the_user_tries_to_create_a_project_without_a_title() {
        // Prepare the request body without a title
        String requestBody = "{ \"invalid\": true }";

        // Make the POST request to create a project
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/projects")
            .then()
            .extract().response();
    }

    @Then("the project is not created")
    public void the_project_is_not_created() {
        lastResponse.then().statusCode(400); // Ensure an error message is returned
    }

    // Scenario: Delete a project by ID (Normal flow)
    @Given("a project is created for deletion")
    public void a_project_is_created_for_deletion() {
        // Create a new project
        String requestBody = "{ \"title\": \"Test Project\", \"active\": true }";
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/projects")
            .then()
            .statusCode(201) // 201 Created
            .extract().response();

        // Extract the project ID from the response
        createdProjectId = lastResponse.jsonPath().getString("id");
    }

    @When("the user tries to delete the created project")
    public void the_user_tries_to_delete_the_created_project() {
        // Make the DELETE request to delete the newly created project
        lastResponse = given().when().delete("/projects/" + createdProjectId).then().extract().response();
    }

    @Then("the project is successfully deleted")
    public void the_project_is_successfully_deleted() {
        lastResponse.then().statusCode(200); // 200 OK for successful deletion

        // Verify that the project cannot be found
        given().when().get("/projects/" + createdProjectId).then().statusCode(404); // 404 Not Found
    }

    // Scenario: Delete a task from a project (Alternate flow)
    @Given("a project with tasks is created for deletion")
    public void a_project_with_tasks_is_created_for_deletion() {
        // Create a new project
        String requestBody = "{ " +
            "\"tasks\": [" +
            "{ \"id\": \"1\" }" +
            "]" +
        "}";

        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/projects")
            .then()
            .statusCode(201) // 201 Created
            .extract().response();

        // Extract the project ID from the response
        createdProjectId = lastResponse.jsonPath().getString("id");
    }

    @When("the user tries to delete the task with ID {string} from the project {string}")
    public void the_user_tries_to_delete_the_task_with_id_from_the_project(String taskId, String projectId) {
        // Make the DELETE request to delete the task from the project
        lastResponse = given().when().delete("/projects/" + createdProjectId + "/tasks/" + taskId).then().extract().response();
    }

    @Then("the task with ID {string} is successfully deleted from the project {string}")
    public void the_task_with_id_is_successfully_deleted_from_the_project(String taskId, String projectId) {
        // Verify that the response contains an empty list of TODOitems
        given().when().get("/projects/" + projectId + "/tasks").then()
            .statusCode(200);
    }


    // Scenario: Delete a project with a non-existent ID (Error flow)
    @When("the user tries to delete the project with ID {string}")
    public void the_user_tries_to_delete_the_project_with_non_existent_id(String id) {
        // Attempt to delete a non-existent project
        lastResponse = given().when().delete("/projects/" + id).then().extract().response();
    }

    @Then("the user is informed that the project does not exist")
    public void the_user_is_informed_that_the_project_does_not_exist() {
        lastResponse.then().statusCode(404); // 404 Not Found with an error message
    }

    // Scenario: Ensure the project exists and store the original status
    @Given("the project with ID {string} exists")
    public void the_project_with_id_exists(String id) {
        projectId = id; // Store the project ID

        // Retrieve the current status of the project to store for resetting
        lastResponse = given().when().get("/projects/" + id).then().statusCode(200).extract().response();
        originalCompleted = lastResponse.jsonPath().getString("projects[0].completed");
        originalActive = lastResponse.jsonPath().getString("projects[0].active");
    }

    // Scenario: Update the project status
    @When("the user tries to update the project {string} to completed status {string} and active status {string}")
    public void the_user_tries_to_update_the_project_to_completed_status_and_active_status(String id, String newCompleted, String newActive) {
        // Prepare the request body
        String requestBody = String.format(
            "{ \"completed\": %s, \"active\": %s }",
            newCompleted, newActive
        );

        // Make the PUT request to update the project
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .put("/projects/" + id)
            .then()
            .extract().response();
    }

    // Scenario: Verify the project is successfully updated
    @Then("the project is successfully updated with completed status {string} and active status {string}")
    public void the_project_is_successfully_updated_with_completed_status_and_active_status(String newCompleted, String newActive) {
        lastResponse.then().statusCode(200); // 200 OK for successful update
    }

    // Cleanup: Reset the project status to the original state after the test
    @After
    public void resetProjectStatus() {
        if (projectId != null && originalCompleted != null && originalActive != null) {
            // Prepare the request body to reset the project status
            String resetRequestBody = String.format(
                "{ \"completed\": %s, \"active\": %s }",
                originalCompleted, originalActive
            );

            // Make the PUT request to reset the project status
            given()
                .contentType("application/json")
                .body(resetRequestBody)
                .when()
                .put("/projects/" + projectId)
                .then()
                .statusCode(200); // 200 OK for successful reset
        }
    }

    @Given("the project about to be change title with ID {string} exists")
    public void the_project_about_to_be_change_title_with_id_exists(String id) {
        projectId = id; // Store the project ID

        // Retrieve the current title of the project to store for resetting
        lastResponse = given().when().get("/projects/" + id).then().statusCode(200).extract().response();
        originalTitle = lastResponse.jsonPath().getString("projects[0].title");
    }

    // Scenario: Update the project title
    @When("the user tries to update the project {string} to title {string}")
    public void the_user_tries_to_update_the_project_to_title(String id, String newTitle) {
        // Prepare the request body
        String requestBody = String.format(
            "{ \"title\": \"%s\" }",
            newTitle
        );

        // Make the PUT request to update the project title
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .put("/projects/" + id)
            .then()
            .extract().response();
    }

    // Scenario: Verify the project title is successfully updated
    @Then("the project is successfully updated to title {string}")
    public void the_project_is_successfully_updated_to_title(String newTitle) {
        lastResponse.then().statusCode(200);
    }

    // Scenario: Verify the project with the new title can be found
    @Then("the project {string} can be found with ID {string}")
    public void the_project_can_be_found_with_id(String newTitle, String id) {
        given().when().get("/projects/" + id).then().statusCode(200)
               .body("projects[0].title", equalTo(newTitle));
    }

    // Cleanup: Reset the project title to the original after the test
    @After
    public void resetProjectTitle() {
        if (projectId != null && originalTitle != null) {
            // Prepare the request body to reset the project title
            String resetRequestBody = String.format(
                "{ \"title\": \"%s\" }",
                originalTitle
            );

            // Make the PUT request to reset the project title
            given()
                .contentType("application/json")
                .body(resetRequestBody)
                .when()
                .put("/projects/" + projectId)
                .then()
                .statusCode(200); // 200 OK for successful reset
        }
    }

    // Scenario: Attempt to update a project with an invalid field
    @When("the user tries to update the project {string} with an invalid field")
    public void the_user_tries_to_update_the_project_with_an_invalid_field(String id) {
        // Attempt to update the project with an invalid field (e.g., updating the "id")
        String requestBody = "{ \"invalidField\": \"invalidValue\" }";

        // Make the PUT request to update the project with an invalid field
        lastResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .put("/projects/" + id)
            .then()
            .extract().response();
    }

    // Scenario: Verify that the project is not updated
    @Then("the project is not updated and an error is returned")
    public void the_project_is_not_updated_and_an_error_is_returned() {
        lastResponse.then().statusCode(400);
    }

    // Scenario: Verify that the project still exists with the original title
    @Then("the project with ID {string} and title {string} can still be found")
    public void the_project_with_id_and_title_can_still_be_found(String id, String title) {
        // Verify that the project still exists with the original ID and title
        given().when().get("/projects/" + id).then().statusCode(200)
               .body("projects[0].title", equalTo(title));
    }

    // Helper method to generate the task array in JSON format
    private String generateTaskArray(String taskIds) {
        String[] ids = taskIds.split(", ");
        StringBuilder taskArray = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            taskArray.append("{ \"id\": \"").append(ids[i]).append("\" }");
            if (i < ids.length - 1) {
                taskArray.append(", ");
            }
        }
        return taskArray.toString();
    }
}
