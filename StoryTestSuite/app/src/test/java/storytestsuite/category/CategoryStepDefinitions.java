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

    @Then("the category named {string} can be found with description {string}")
    public void verifyCategoryCanBeFoundWithDescription(String title, String description) {
        given().queryParam("title", title)
            .when().get("categories")
            .then()
            .statusCode(200)
            .body("categories.find { it.title == '" + title + "' }.description", equalTo(description));
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

    // Get Category List Feature Step Definitions

    @Given("the categories with the following titles:")
    public void givenCategoriesWithOnlyTitles(DataTable titles) {
        titles.asList().forEach(title -> {
            createCategoryWithTitleAndDescription(title, "");
        });
    }

    @When("the user tries to get a list of all categories")
    public void getAllCategoriesList() {
        lastResponse = given().get("categories");
    }

    @Then("the categories with the following titles are found:")
    public void verifyCategoriesTitlesFound(DataTable titles) {
        lastResponse.then()
            .statusCode(200)
            .body("categories.title", hasItems(titles.asList().toArray()));
    }

    @Given("the categories with the following titles and descriptions exist:")
    public void givenCategoriesWithSpecificTitlesAndDescriptions(DataTable titlesAndDescriptions) {
        titlesAndDescriptions.asMaps().forEach(row -> {
            createCategoryWithTitleAndDescription(row.get("title"), row.get("description"));
        });
    }

    @When("the user tries to get a list of categories with descriptions of {string}")
    public void getCategoriesByDescription(String description) {
        lastResponse = given()
            .queryParam("description", description)
            .get("categories");
    }

    @Then("the categories with the following titles are found by description:")
    public void verifyCategoriesWithMatchingDescription(DataTable titles) {
        // Extract the list of titles to compare with the response
        List<String> expectedTitles = titles.asList();
        lastResponse.then()
            .statusCode(200)
            .body("categories.size()", equalTo(expectedTitles.size())) // Check number of categories matches
            .body("categories.title", hasItems(expectedTitles.toArray())); // Verify titles are present in response
    }

    @When("the user tries to get the list of categories with non-existent property {string} set to {string}")
    public void getCategoriesByNonExistentProperty(String property, String value) {
        lastResponse = given()
            .queryParam(property, value)
            .get("categories");
    }

    @Then("no category is found by non-existent property")
    public void verifyNoCategoryFoundForNonExistentProperty() {
        lastResponse.then()
            .statusCode(200)
            .body("categories.size()", equalTo(0)); // Confirm no categories are returned
    }



    // Delete Category Feature Step Definitions
    @Given("the category named {string} exists")
    public void the_category_named_exists(String title) {
        createCategoryWithTitleAndDescription(title, "");
    }

    @When("the user tries to delete the category named {string}")
    public void the_user_tries_to_delete_the_category_named(String title) {
        String idString = getCategoryID(title);
        if (idString != null) {
            lastResponse = given().when().delete("categories/" + idString);
        }
    }

    @When("the user tries to delete all categories")
    public void the_user_tries_to_delete_all_categories() {
        lastResponse = given().when().delete("categories");
        if (lastResponse.statusCode() == 200) {
            categoryTitlesToDelete.clear(); // Clear titles since all categories are deleted
        }
    }

    @When("the user tries to delete the category with id {string}")
    public void the_user_tries_to_delete_the_category_with_id(String id) {
        lastResponse = given().when().delete("categories/" + id);
    }

    @Then("the category named {string} is successfully deleted")
    public void the_category_named_is_successfully_deleted(String title) {
        lastResponse.then().statusCode(200);
    }

    @Then("the category named {string} cannot be found")
    public void the_category_named_cannot_be_found(String title) {
        given().when().queryParam("title", title).get("categories")
            .then().statusCode(200).body("categories.size()", equalTo(0));
    }

    @Then("all categories are successfully deleted")
    public void all_categories_are_successfully_deleted() {
        given().when().get("categories")
            .then().statusCode(200).body("categories.size()", equalTo(0));
    }

    @Then("the user is informed that the category does not exist")
    public void the_user_is_informed_that_the_category_does_not_exist() {
        lastResponse.then().statusCode(404);
    }

    @Then("the category with id {string} cannot be found")
    public void the_category_with_id_cannot_be_found(String id) {
        given().when().get("categories/" + id)
            .then().statusCode(404);
    }


    // Update Category Feature Step Definitions
    @Given("the category named {string} exists with description {string}")
    public void givenCategoryExistsWithDescription(String title, String description) {
        createCategoryWithTitleAndDescription(title, description);
    }

    @When("the user tries to update the description of the category named {string} to {string}")
    public void updateCategoryDescription(String title, String newDescription) {
        String id = getCategoryID(title);
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

    // This method updates the title of a category
    @When("the user tries to update the category named {string} to {string}")
    public void updateCategoryTitle(String title, String newTitle) {
        String id = getCategoryID(title);
        lastResponse = given()
            .body("{ \"title\": \"" + newTitle + "\"}")
            .put("categories/" + id);
        categoryTitlesToDelete.add(newTitle); // Track the new title for cleanup
    }

    @Then("the category is not updated")
    public void verifyCategoryNotUpdated() {
        lastResponse.then().statusCode(400);
    }

    // This method checks if the updated category can be found
    @Then("the modified category named {string} can be found with description {string}")
    public void verifyUpdatedCategoryByDescription(String title, String newDescription) {
        // Ensure we are querying the correct title after it might have been updated
        lastResponse = given().queryParam("title", title)
            .when().get("categories");

        lastResponse.then()
            .statusCode(200)
            .body("categories.find { it.title == '" + title + "' }.description", equalTo(newDescription));
    }

    // This method checks if the old category cannot be found
    @Then("the old category named {string} cannot be found")
    public void verifyOldCategoryCannotBeFound(String title) {
        lastResponse = given().queryParam("title", title)
            .when().get("categories");

        lastResponse.then()
            .statusCode(200)
            .body("categories.size()", equalTo(0));
    }


    // Helper method to get a category ID based on its title
    private String getCategoryID(String title) {
        List<String> ids = given()
            .queryParam("title", title)
            .get("categories")
            .jsonPath().getList("categories.id");
        return ids.isEmpty() ? null : ids.get(0);
    }
}
