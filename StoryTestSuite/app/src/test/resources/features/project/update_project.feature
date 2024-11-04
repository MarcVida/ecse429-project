Feature: Update a Project

  As a user,
  I would like to update an existing project,
  So that I can modify its details to reflect any changes.

  Scenario: Update the status of a project (Normal flow)
    Given the project with ID "<id>" exists
    When the user tries to update the project "<id>" to completed status "<newCompleted>" and active status "<newActive>"
    Then the project is successfully updated with completed status "<newCompleted>" and active status "<newActive>"

    Examples:
      | id | newCompleted | newActive |
      | 1  | true         | false     |

  Scenario: Update the title of a project (Alternate flow)
    Given the project about to be change title with ID "<id>" exists
    When the user tries to update the project "<id>" to title "<newTitle>"
    Then the project is successfully updated to title "<newTitle>"
    And the project "<newTitle>" can be found with ID "<id>"

    Examples:
      | id | newTitle   |
      | 1  | New Work   |

  Scenario: Attempt to update a project with an invalid field (Error flow)
    Given the project with ID "<id>" exists
    When the user tries to update the project "<id>" with an invalid field
    Then the project is not updated and an error is returned

    Examples:
      | id |
      | 1  |
