Feature: Delete a Project

  As a user,
  I would like to delete an existing project or its tasks,
  So that I can manage my projects and tasks efficiently.

  Scenario: Delete a project by ID (Normal flow)
    Given the project with ID "<id>" and title "<title>" exists
    When the user tries to delete the project with ID "<id>"
    Then the project "<title>" with ID "<id>" is successfully deleted
    And the project with ID "<id>" cannot be found

    Examples:
      | id | title       |
      | 1  | Office Work |
      | 2  | Team Project|

  Scenario: Delete a task from a project (Alternate flow)
    Given the project with ID "<projectId>" has a task with ID "<taskId>"
    When the user tries to delete the task with ID "<taskId>" from the project "<projectId>"
    Then the task with ID "<taskId>" is successfully deleted from the project "<projectId>"

    Examples:
      | projectId | taskId |
      | 1         | 5      |
      | 2         | 1      |

  Scenario: Delete a project with a non-existent ID (Error flow)
    When the user tries to delete the project with ID "<id>"
    Then the user is informed that the project does not exist
    And the project with ID "<id>" cannot be found

    Examples:
      | id  |
      | 999 |
      | -1  |
