Feature: Delete a Project

  As a user,
  I would like to delete an existing project or its tasks,
  So that I can manage my projects and tasks efficiently.

  Scenario: Delete a project by ID (Normal flow)
    Given a project is created for deletion
    When the user tries to delete the created project
    Then the project is successfully deleted

  Scenario: Delete a task from a project (Alternate flow)
    Given a project with tasks is created for deletion
    When the user tries to delete the task with ID "<taskId>" from the project "<projectId>"
    Then the task with ID "<taskId>" is successfully deleted from the project "<projectId>"

    Examples:
      | projectId | taskId |
      | 1         | 1      |
      | 2         | 1      |

  Scenario: Delete a project with a non-existent ID (Error flow)
    When the user tries to delete the project with ID "<id>"
    Then the user is informed that the project does not exist

    Examples:
      | id  |
      | 999 |
      | -1  |
