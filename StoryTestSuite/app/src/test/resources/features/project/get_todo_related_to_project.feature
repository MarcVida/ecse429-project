Feature: Get TODO Items Related to a Project

  As a user,
  I would like to retrieve all TODO items related to a specific project,
  So that I can see the tasks associated with the project.

  Scenario: Get all TODO items related to a project with tasks (Normal flow)
    Given the project with ID exists with related TODO items
    When the user tries to get all TODO items related to the project with ID "<projectId>"
    Then the projectID is present under the tasks of array of TODO item

    Examples:
      | projectId |
      | 1         |

  Scenario: Get TODO items from a project with no tasks (Alternate flow)
    Given the project with ID exists but has no related TODO items
    When the user tries to get all TODO items related to the project with ID "<projectId>"
    Then the projectID is not present under the tasks of array of TODO item

    Examples:
      | projectId |
      | 2         |

  Scenario: Get TODO items for a non-existent project (Error flow)
    When the user tries to get TODO items related to a non-existent project with ID "<projectId>"
    Then an error message is returned stating the project does not exist

    Examples:
      | projectId |
      | 999       |
      | -1        |
