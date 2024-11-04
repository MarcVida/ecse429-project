Feature: Create a Project

  As a user,
  I would like to create a project,
  So that I can manage and organize related tasks efficiently.

  Scenario: Create a project with all required fields (Normal flow)
    When the user tries to create a project with title "<title>", completed status "<completed>", active status "<active>", description "<description>", and tasks with IDs "<taskIds>"
    Then the project "<title>" is successfully created with completed status "<completed>", active status "<active>", description "<description>", and task IDs "<taskIds>"
    And the project "<title>" can be found with completed status "<completed>", active status "<active>", description "<description>", and task IDs "<taskIds>"

    Examples:
      | title        | completed | active | description | taskIds     |
      | Office Work  | false     | false  | ""          | "2, 1"      |
      | Team Project | false     | true   | "Planning"  | "3, 4, 5"   |

  Scenario: Create a project with minimal data (Alternate flow)
    When the user tries to create a project with title "<title>" and active status "<active>"
    Then the project "<title>" is successfully created with default values for other fields

    Examples:
      | title        | active |
      | Office Work  | false  |
      | Team Project | true   |

  Scenario: Create a project with missing mandatory fields (Error flow)
    When the user tries to create a project without a title
    Then the project is not created

    Examples:
      | title |
      |       |
