Feature: Get TODO items related to a Project

  As a user,
  I would like to retrieve all TODO items related to a specific project,
  So that I can see the tasks associated with the project.

  Scenario: Get all TODO items related to a project by ID (Normal flow)
    Given the project with ID "<projectId>" exists with related TODO items
    When the user tries to get all TODO items related to the project with ID "<projectId>"
    Then the following TODO items are returned:
      | id | title          | doneStatus | description |
      | <id1> | <title1>     | <doneStatus1> | <description1> |
      | <id2> | <title2>     | <doneStatus2> | <description2> |

    Examples:
      | projectId | id1 | title1        | doneStatus1 | description1 | id2 | title2        | doneStatus2 | description2 |
      | 3         | 1   | scan paperwork| false       | ""           | 2   | file paperwork| false       | ""           |
      | 4         | 1   | scan paperwork| false       | ""           | 2   | file paperwork| false       | ""           |

  Scenario: Get TODO items when the project has no tasks (Alternative flow)
    Given the project with ID "<projectId>" exists but has no related TODO items
    When the user tries to get all TODO items related to the project with ID "<projectId>"
    Then an empty list of TODO items is returned as:
      """
      {
        "todos": []
      }
      """

    Examples:
      | projectId |
      | 1         |

  Scenario: Get TODO items for a non-existent project (Error flow)
    When the user tries to get TODO items related to a non-existent project with ID "<projectId>"
    Then an error message is returned stating the project does not exist
    And no TODO items are returned

    Examples:
      | projectId |
      | 999       |
      | -1        |
