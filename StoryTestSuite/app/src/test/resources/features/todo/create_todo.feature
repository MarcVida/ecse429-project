Feature: Create a TODO item

  As a user,
  I would like to create a TODO item,
  So that I can track my daily tasks.

  Scenario: Create a TODO item for a daily task (Normal flow)
    When the user tries to create a TODO item with title "<title>", status "<doneStatus>" and description "<description>"
    Then the TODO item "<title>" is successfully created with status "<doneStatus>" and description "<description>"
    And the TODO item "<title>" can be found with status "<doneStatus>" and description "<description>"

    Examples:
      | title                      | doneStatus | description                          |
      | Finish ECSE 429 assignment | false      | Story tests, written report and demo |
      | Study for ECSE 331 midterm | false      | Practice problems                    |

  Scenario: Create a TODO item for a daily task with minimal data (Alternate flow)
    When the user tries to create a TODO item with title "<title>"
    Then the TODO item "<title>" is successfully created with incomplete status

    Examples:
      | title                      |
      | Finish ECSE 429 assignment |
      | Study for ECSE 331 midterm |

  Scenario: Create a TODO item with missing mandatory fields (Error flow)
    When the user tries to create a TODO item with status "<doneStatus>" and description "<description>"
    Then the TODO item is not created

    Examples:
      | doneStatus | description                          |
      | false      | Story tests, written report and demo |
      | true       | Practice problems                    |