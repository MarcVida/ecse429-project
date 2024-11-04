Feature: Update a Project

  As a user,
  I would like to update an existing project,
  So that I can modify its details to reflect any changes.

  Scenario: Update the status of a project (Normal flow)
    Given the project with ID "<id>" exists with completed status "<completed>" and active status "<active>"
    When the user tries to update the project "<id>" to completed status "<newCompleted>" and active status "<newActive>"
    Then the project is successfully updated with completed status "<newCompleted>" and active status "<newActive>"

    Examples:
    | id | completed | active | newCompleted | newActive |
    | 1  | false     | true   | true         | false     |
    | 2  | true      | false  | false        | true      |

  Scenario: Update the title of a project (Alternate flow)
    Given the project with ID "<id>" exists with title "<title>"
    When the user tries to update the project "<id>" to title "<newTitle>"
    Then the project is successfully updated to title "<newTitle>"
    And the project "<newTitle>" can be found with ID "<id>"

    Examples:
      | id | title       | newTitle        |
      | 1  | Office Work | Office Work 3   |
      | 2  | Team Project| Updated Project |

  Scenario: Update a non-modifiable field of a project (Error flow)
    Given the project with ID "<id>" and title "<title>" exists
    When the user tries to update the project "<id>" to ID "<newId>"
    Then the project is not updated
    And the project with ID "<id>" and title "<title>" can still be found

    Examples:
      | id | title       | newId |
      | 1  | Office Work | 99    |
      | 2  | Team Project| 100   |
