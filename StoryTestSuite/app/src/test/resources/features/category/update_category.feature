Feature: Update a category

  As a user,
  I would like to update an existing category,
  So that I can keep my categories up to date.

  Scenario: Update the description of a category (Normal flow)
    Given the category named "<title>" exists with description "<description>"
    When the user tries to update the description of the category named "<title>" to "<newDescription>"
    Then the category is successfully updated with description "<newDescription>"
    And the modified category named "<title>" can be found with description "<newDescription>"

    Examples:
      | title       | description                                      | newDescription                          |
      | Assignments | Todos related to any course assignments          | Todos related to any course lab reports |
      | Midterms    | Todos realted to any course midterm examinations | Todos realted to any course quizzes     |

  Scenario: Update the title of a category (Alternate flow)
    Given the category named "<title>" exists
    When the user tries to update the category named "<title>" to "<newTitle>"
    Then the category is successfully updated to title "<newTitle>"
    And the category named "<newTitle>" can be found
    And the old category named "<title>" cannot be found

    Examples:
      | title       | newTitle |
      | Assignments | Labs     |
      | Midterms    | Quizzes  |

  Scenario: Update the id of a category (Error flow)
    Given the category named "<title>" exists
    When the user tries to update the category named "<title>" to id "<newId>"
    Then the category is not updated
    And the category named "<title>" can be found

    Examples:
      | title       | newId |
      | Assignments | 1     |
      | Midterms    | 2     |