Feature: Update a TODO item

  As a user,
  I would like to update an existing TODO item,
  So that I can keep my tasks organized and up to date.

  Scenario: Update the status of a TODO item (Normal flow)
    Given the TODO item "<title>" exists with status "<doneStatus>"
    When the user tries to update the TODO item "<title>" to status "<newDoneStatus>"
    Then the TODO item is successfully updated to status "<newDoneStatus>"
    And the TODO item "<title>" can be found with status "<newDoneStatus>"

    Examples:
      | title                      | doneStatus | newDoneStatus |
      | Finish ECSE 429 assignment | false      | true          |
      | Study for ECSE 331 midterm | true       | false         |

  Scenario: Update the title of a TODO item (Alternate flow)
    Given the TODO item "<title>" exists
    When the user tries to update the TODO item "<title>" to title "<newTitle>"
    Then the TODO item is successfully updated to title "<newTitle>"
    And the TODO item "<newTitle>" can be found
    And the TODO item "<title>" cannot be found

    Examples:
      | title                      | newTitle                |
      | Finish ECSE 429 assignment | Finish assignment       |
      | Study for ECSE 331 midterm | Study for ECSE 331 exam |

  Scenario: Update the id of a TODO item (Error flow)
    Given the TODO item "<title>" exists
    When the user tries to update the TODO item "<title>" to id "<newId>"
    Then the TODO item is not updated
    And the TODO item "<title>" can be found

    Examples:
      | title                      | newId |
      | Finish ECSE 429 assignment | 1     |
      | Study for ECSE 331 midterm | 2     |