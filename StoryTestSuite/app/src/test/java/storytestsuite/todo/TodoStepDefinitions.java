package storytestsuite.todo;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TodoStepDefinitions {

    public static List<String> todoTitlesToDelete = new ArrayList<>(); // stores todo items to delete after each scenario
    public static Response lastResponse = null; // stores the response of the last request

    @BeforeAll
    public static void before_all() {
        RestAssured.baseURI = "http://localhost:4567/";
    }

    @After
    public void delete_todo_items() {
        for (String title : todoTitlesToDelete) {
            var todoIds = given()
                .queryParam("title", title)
                .get("todos")
                .jsonPath().getList("todos.id");
            for (var id : todoIds) {
                delete("todos/" + id);
            }
        }

        todoTitlesToDelete.clear();
        lastResponse = null;
    }

    @Given("the TODO item {string} exists with status {string} and description {string}")
    public void the_item_exists_with_status_and_description(String title, String doneStatus, String description) {
        given()
            .body("{ \"title\": \"" + title + 
                "\", \"doneStatus\": " + doneStatus + 
                ", \"description\": \"" + description + "\"}")
            .post("todos");
        
        todoTitlesToDelete.add(title);
    }

    @Given("the TODO item {string} exists with status {string}")
    public void the_item_exists_with_status(String title, String doneStatus) {
        the_item_exists_with_status_and_description(title, doneStatus, "");
    }

    @Given("the TODO item {string} exists")
    public void the_item_exists(String title) {
        the_item_exists_with_status_and_description(title, "false", "");
    }

    @Given("the TODO items with the following titles exist:")
    public void the_items_with_the_following_titles_exist(List<String> titles) {
        for (String title : titles) {
            the_item_exists_with_status_and_description(title, "false", "");
        }
    }

    @Given("the TODO items with the following titles and statuses exist:")
    public void the_items_with_the_following_titles_and_statuses_exist(DataTable titlesAndStatuses) {
        var table = titlesAndStatuses.asMaps();
        for (var row : table) {
            the_item_exists_with_status_and_description(row.get("title"), row.get("doneStatus"), "");
        }
    }

    @When("the user tries to create a TODO item with title {string}, status {string} and description {string}")
    public void the_user_tries_to_create_a_todo_item_with_title_donestatus_and_description(String title, String doneStatus, String description) {
        lastResponse = given().when()
            .body("{ \"title\": \"" + title + 
                "\", \"doneStatus\": " + doneStatus + 
                ", \"description\": \"" + description + "\"}")
            .post("todos");
        
        todoTitlesToDelete.add(title);
    }

    @When("the user tries to create a TODO item with title {string}")
    public void the_user_tries_to_create_a_todo_item_with_title(String title) {
        lastResponse = given().when()
            .body("{ \"title\": \"" + title + "\"}")
            .post("todos");

        todoTitlesToDelete.add(title);
    }

    @When("the user tries to create a TODO item with status {string} and description {string}")
    public void the_user_tries_to_create_a_todo_item_with_donestatus_and_description(String doneStatus, String description) {
        lastResponse = given().when().body(
            "{ \"doneStatus\": " + doneStatus + 
            ", \"description\": \"" + description + "\"}").post("todos");
    }

    @When("the user tries to update the TODO item {string} to status {string}")
    public void the_user_tries_to_update_the_todo_item_to_status(String title, String doneStatus) {
        String idString = getTodoId(title);
        lastResponse = given().when()
            .body("{ \"title\": \"" + title + 
                "\", \"doneStatus\": " + doneStatus + "}")
            .put("todos/" + idString);
    }

    @When("the user tries to update the TODO item {string} to title {string}")
    public void the_user_tries_to_update_the_todo_item_to_title(String title, String newTitle) {
        String idString = getTodoId(title);
        lastResponse = given().when()
            .body("{ \"title\": \"" + newTitle + "\"}")
            .put("todos/" + idString);

        todoTitlesToDelete.add(newTitle);
    }

    @When("the user tries to update the TODO item {string} to id {string}")
    public void the_user_tries_to_update_the_todo_item_to_id(String title, String id) {
        String idString = getTodoId(title);
        lastResponse = given().when()
            .body("{ \"title\": \"" + title + 
                "\", \"id\": " + id + "}")
            .put("todos/" + idString);
    }

    @When("the user tries to delete the TODO item {string}")
    public void the_user_tries_to_delete_the_todo_item(String title) {
        String idString = getTodoId(title);
        lastResponse = given().when().delete("todos/" + idString);
    }

    @When("the user tries to delete all TODO items")
    public void the_user_tries_to_delete_all_todo_items() {
        lastResponse = given().when().delete("todos");
    }

    @When("the user tries to delete the TODO item with id {string}")
    public void the_user_tries_to_delete_the_todo_item_with_id(String id) {
        lastResponse = given().when().delete("todos/" + id);
    }

    @When("the user searches for the TODO item {string}")
    @When("the user searches for the TODO item with partial title {string}")
    public void the_user_searches_for_the_todo_item(String title) {
        lastResponse = given().when()
            .queryParam("title", title)
            .get("todos");
    }

    @When("the user tries to get the list of all TODO items")
    public void the_user_tries_to_get_the_list_of_all_todo_items() {
        lastResponse = given().when().get("todos");
    }

    @When("the user tries to get the list of TODO items with status {string}")
    public void the_user_tries_to_get_the_list_of_todo_items_with_status(String doneStatus) {
        lastResponse = given().when()
            .queryParam("doneStatus", doneStatus)
            .get("todos");
    }
    
    @When("the user tries to get the list of TODO items with property {string} set to {string}")
    public void the_user_tries_to_get_the_list_of_todo_items_with_property_set_to(String property, String value) {
        lastResponse = given().when()
            .queryParam(property, value)
            .get("todos");
    }

    @Then("the TODO item {string} is successfully created with status {string} and description {string}")
    public void the_todo_item_is_successfully_created_with_title_donestatus_and_description(String title, String doneStatus, String description) {
        lastResponse.then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo(title))
            .body("doneStatus", equalTo(doneStatus))
            .body("description", equalTo(description));
    }

    @Then("the TODO item {string} can be found with status {string} and description {string}")
    public void the_todo_item_can_be_found_with_status_and_description(String title, String doneStatus, String description) {
        given()
        .when()
            .queryParam("title", title)
            .get("todos")
        .then()
            .statusCode(200)
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", notNullValue())
            .body("todos[0].title", equalTo(title))
            .body("todos[0].doneStatus", equalTo(doneStatus))
            .body("todos[0].description", equalTo(description));
    }

    @Then("the TODO item {string} is successfully created with incomplete status")
    public void the_todo_item_is_successfully_created_with_title_and_incomplete_status(String title) {
        the_todo_item_is_successfully_created_with_title_donestatus_and_description(title, "false", "");
    }

    @Then("the TODO item is not created")
    public void the_todo_item_is_not_created() {
        lastResponse.then().statusCode(400);
    }

    @Then("the TODO item is successfully updated to status {string}")
    public void the_todo_item_is_successfully_updated_to_status(String doneStatus) {
        lastResponse.then()
            .statusCode(200)
            .body("doneStatus", equalTo(doneStatus));
    }

    @Then("the TODO item {string} can be found with status {string}")
    public void the_todo_item_can_be_found_with_status(String title, String doneStatus) {
        given()
        .when()
            .queryParam("title", title)
            .get("todos")
        .then()
            .statusCode(200)
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", notNullValue())
            .body("todos[0].title", equalTo(title))
            .body("todos[0].doneStatus", equalTo(doneStatus));
    }

    @Then("the TODO item is successfully updated to title {string}")
    public void the_todo_item_is_successfully_updated_to_title(String newTitle) {
        lastResponse.then()
            .statusCode(200)
            .body("title", equalTo(newTitle));
    }

    @Then("the TODO item {string} can be found")
    public void the_todo_item_can_be_found(String title) {
        given()
        .when()
            .queryParam("title", title)
            .get("todos")
        .then()
            .statusCode(200)
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", notNullValue())
            .body("todos[0].title", equalTo(title));
    }

    @Then("the TODO item {string} cannot be found")
    public void the_todo_item_cannot_be_found(String title) {
        given()
        .when()
            .queryParam("title", title)
            .get("todos")
        .then()
            .statusCode(200)
            .body("todos.size()", equalTo(0));
    }

    @Then("the TODO item is not updated")
    public void then_the_todo_item_is_not_updated() {
        lastResponse.then().statusCode(400);
    }

    @Then("the TODO item {string} is successfully deleted")
    @Then("all TODO items are successfully deleted")
    public void the_todo_item_is_successfully_deleted(String title) {
        lastResponse.then().statusCode(200);
    }

    @Then("the user is informed that the TODO item does not exist")
    public void the_user_is_informed_that_the_todo_item_does_not_exist() {
        lastResponse.then().statusCode(404);
    }

    @Then("the TODO item with id {string} cannot be found")
    public void the_todo_item_with_id_cannot_be_found(String id) {
        given()
        .when()
            .get("todos/" + id)
        .then()
            .statusCode(404);
    }

    @Then("the TODO item {string} is found")
    public void the_todo_item_is_found(String title) {
        lastResponse.then()
            .statusCode(200)
            .body("todos.size()", equalTo(1))
            .body("todos[0].id", notNullValue())
            .body("todos[0].title", equalTo(title));
    }

    @Then("no TODO item is found")
    public void no_todo_item_is_found() {
        lastResponse.then()
            .statusCode(200)
            .body("todos.size()", equalTo(0));
    }

    @Then("the TODO items with the following titles are found:")
    public void the_todo_items_with_the_following_titles_are_found(List<String> titles) {
        lastResponse.then()
            .statusCode(200)
            .body("todos.size()", greaterThan(0))
            .body("todos.title", hasItems(titles.toArray()));
    }

    @Then("the TODO items with the following titles are not found:")
    public void the_todo_items_with_the_following_titles_are_not_found(List<String> titles) {
        lastResponse.then()
            .statusCode(200)
            .body("todos.title", not(hasItems(titles.toArray())));
    }

    /**
     * Helper method to get a todo item's id from its title.
     * Assumes that there is exactly one todo item with the given title.
     * @param title
     * @return the todo item id as a string
     */
    public String getTodoId(String title) {
        return given().when()
            .queryParam("title", title)
            .get("todos")
            .jsonPath().getString("todos[0].id");
    }

}
