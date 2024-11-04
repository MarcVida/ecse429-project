Feature: Create a Project

  As a user,
  I would like to create a project,
  So that I can manage and organize related tasks efficiently.

  Feature: Create a Project

  Scenario Outline: Create a project with all required fields (Normal flow)
    When the user tries to create a project with title "<title>", completed status "<completed>", active status "<active>", description "<description>", and tasks with IDs "<taskIds>"
    Then the project "<title>" is successfully created with completed status "<completed>", active status "<active>", description "<description>", and task IDs "<taskIds>"
    And the project "<title>" can be found with completed status "<completed>", active status "<active>", description "<description>", and task IDs "<taskIds>"

    Examples:
      | title         | completed | active | description  | taskIds |
      | Team Project  | false     | true   |              | 1, 2    |
      | Team Project2 | true      | false  | new project  | 1, 2    |


  Scenario Outline: Create a project with minimal data (Alternate flow)
  When the user tries to create a project with title "<title>" and active status "<active>"
  Then the project "<title>" is successfully created with default values for other fields

  Examples:
    | title          | active |
    | Simple Project | true   |
    | Big Project    | false  |

  Scenario Outline: Create a project with missing or invalid fields (Error flow)
    When the user tries to create a project with invalid field "<fieldName>" and value "<fieldValue>"
    Then the project is not created

    Examples:
      | fieldName   | fieldValue       |
      | extraField  | true             |  # Invalid extra field
      | invalid     | true             |  # Invalid field

