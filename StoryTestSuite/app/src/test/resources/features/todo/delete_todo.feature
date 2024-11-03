Feature: Delete a TODO item

  As a user,
  I would like to delete an existing TODO item,
  So that I only keep necessary tasks organized.

  Scenario: Delete a TODO item (Normal flow)
    Given the TODO item "<title>" exists
    When the user tries to delete the TODO item "<title>"
    Then the TODO item "<title>" is successfully deleted
    And the TODO item "<title>" cannot be found

    Examples:
      | title                      |
      | Finish ECSE 429 assignment |
      | Study for ECSE 331 midterm |

  Scenario: Delete all TODO items (Alternate flow)
    Given the TODO item "<title1>" exists
    And the TODO item "<title2>" exists
    When the user tries to delete all TODO items
    Then all TODO items are successfully deleted
    And the TODO item "<title1>" cannot be found
    And the TODO item "<title2>" cannot be found

    Examples:
      | title1                     | title2           |
      | Finish ECSE 429 assignment | Write lab report |
      | Study for ECSE 331 midterm | Get some sleep   |

  Scenario: Delete a non-existent TODO item (Error flow)
    When the user tries to delete the TODO item with id "<id>"
    Then the user is informed that the TODO item does not exist
    And the TODO item with id "<id>" cannot be found

    Examples:
      | id   |
      | 9999 |
      | -1   |