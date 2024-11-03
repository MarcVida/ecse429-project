Feature: Search for TODO items

  As a user,
  I would like to search for an existing TODO item,
  So that I can find a task I'm interested in.

  Scenario: Search for a TODO item by full title (Normal flow)
    Given the TODO item "<title>" exists
    When the user searches for the TODO item "<title>"
    Then the TODO item "<title>" is found

    Examples:
      | title                      |
      | Finish ECSE 429 assignment |
      | Study for ECSE 331 midterm |

  Scenario: Search for a TODO item by partial title (Alternate flow)
    Given the TODO item "<title>" exists
    When the user searches for the TODO item with partial title "<partialTitle>"
    Then the TODO item "<title>" is found

    Examples:
      | title                      | partialTitle |
      | Finish ECSE 429 assignment | ECSE 429     |
      | Study for ECSE 331 midterm | midterm      |

  Scenario: Search for a non-existing TODO item (Error flow)
    When the user searches for the TODO item "<title>"
    Then no TODO item is found

    Examples:
      | title                       |
      | Read MATH 208 lecture notes |
      | Get some sleep              |