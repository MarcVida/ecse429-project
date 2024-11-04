package storytestsuite.category;

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

public class CategoryStepDefinitions {

    public static List<String> categoryTitlesToDelete = new ArrayList<>(); // Stores categories to delete after each scenario
    public static Response lastResponse = null; // Stores the response of the last request

    @BeforeAll
    public static void before_all() {
        RestAssured.baseURI = "http://localhost:4567/";
    }

    @After
    public void delete_category_items() {
        for (String title : categoryTitlesToDelete) {
            var categoryIds = given()
                .queryParam("title", title)
                .get("categories")
                .jsonPath().getList("categories.id");
            for (var id : categoryIds) {
                delete("categories/" + id);
            }
        }

        categoryTitlesToDelete.clear();
        lastResponse = null;
    }

    // Create Category Feature Step Definitions

    @When("the user tries to create a new category with title {string}, and description {string}")
    public void createCategoryWithTitleAndDescription(String title, String description) {
        lastResponse = given()
            .body("{ \"title\": \"" + title + "\", \"description\": \"" + description + "\"}")
            .post("categories");

        categoryTitlesToDelete.add(title);
    }

    @Then("the category named {string} is successfully created with description {string}")
    public void verifyCategoryCreatedWithDescription(String title, String description) {
        lastResponse.then()
            .statusCode(201)
            .body("title", equalTo(title))
            .body("description", equalTo(description));
    }

    @When("the user tries to create a new category with title {string}")
    public void createCategoryWithTitleOnly(String title) {
        lastResponse = given()
            .body("{ \"title\": \"" + title + "\"}")
            .post("categories");

        categoryTitlesToDelete.add(title);
    }

    @Then("the category named {string} is successfully created with an empty description")
    public void verifyCategoryCreatedWithEmptyDescription(String title) {
        lastResponse.then()
            .statusCode(201)
            .body("title", equalTo(title))
            .body("description", isEmptyOrNullString());
    }

    @When("the user tries to create a new category with description {string}")
    public void createCategoryWithDescriptionOnly(String description) {
        lastResponse = given()
            .body("{ \"description\": \"" + description + "\"}")
            .post("categories");
    }

    @Then("the new category is not created")
    public void verifyCategoryNotCreated() {
        lastResponse.then().statusCode(400);
    }

    // Get Category List Feature Step Definitions

    @Given("the categories with the following titles:")
    public void givenCategoriesWithTitles(DataTable titles) {
        titles.asList().forEach(title -> {
            createCategoryWithTitleAndDescription(title, "");
        });
    }

    @When("the user tries to get a list of all categories")
    public void getAllCategories() {
        lastResponse = given().get("categories");
    }

    @Then("the categories with the following titles are found:")
    public void verifyCategoriesFound(DataTable titles) {
        lastResponse.then()
            .statusCode(200)
            .body("categories.title", hasItems(titles.asList().toArray()));
    }

    @Given("the categories with the following titles and descriptions exist:")
    public void givenCategoriesWithTitlesAndDescriptions(DataTable titlesAndDescriptions) {
        titlesAndDescriptions.asMaps().forEach(row -> {
            createCategoryWithTitleAndDescription(row.get("title"), row.get("description"));
        });
    }

    @When("the user tries to get a list of categories with descriptions of {string}")
    public void getCategoriesWithDescription(String description) {
        lastResponse = given()
            .queryParam("description", description)
            .get("categories");
    }

    @When("the user tries to get the list of categories with non-existent property {string} set to {string}")
    public void getCategoriesWithNonExistentProperty(String property, String value) {
        lastResponse = given()
            .queryParam(property, value)
            .get("categories");
    }

    @Then("no category is found")
    public void verifyNoCategoryFound() {
        lastResponse.then()
            .statusCode(200)
            .body("categories.size()", equalTo(0));
    }

    // Update Category Feature Step Definitions

    @Given("the category named {string} exists with description {string}")
    public void givenCategoryExistsWithDescription(String title, String description) {
        createCategoryWithTitleAndDescription(title, description);
    }

    @When("the user tries to update the description of the category named {string} to {string}")
    public void updateCategoryDescription(String title, String newDescription) {
        String id = getCategoryId(title);
        lastResponse = given()
            .body("{ \"description\": \"" + newDescription + "\"}")
            .put("categories/" + id);
    }

    @Then("the category is successfully updated with description {string}")
    public void verifyCategoryUpdatedWithDescription(String newDescription) {
        lastResponse.then()
            .statusCode(200)
            .body("description", equalTo(newDescription));
    }

    @When("the user tries to update the category named {string} to {string}")
    public void updateCategoryTitle(String title, String newTitle) {
        String id = getCategoryId(title);
        lastResponse = given()
            .body("{ \"title\": \"" + newTitle + "\"}")
            .put("categories/" + id);

        categoryTitlesToDelete.add(newTitle); // Track the new title for cleanup
    }

    @When("the user tries to update the category named {string} to id {string}")
    public void updateCategoryId(String title, String newId) {
        String id = getCategoryId(title);
        lastResponse = given()
            .body("{ \"id\": \"" + newId + "\"}")
            .put("categories/" + id);
    }

    @Then("the category is not updated")
    public void verifyCategoryNotUpdated() {
        lastResponse.then().statusCode(400);
    }

    // Helper method to get a category ID based on its title
    public String getCategoryId(String title) {
        return given()
            .queryParam("title", title)
            .get("categories")
            .jsonPath().getString("categories[0].id");
    }
}
