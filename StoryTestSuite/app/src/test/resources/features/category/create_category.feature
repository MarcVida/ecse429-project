Feature: Create a category

  As a user,
  I would like to create a new category of todos,
  So that I can group related todos together.

  Scenario: Create a category for a new group  of todos (Normal flow)
    When the user tries to create a new category with title "<title>", and description "<description>"
    Then the category named "<title>" is successfully created with description "<description>"
    And the category named "<title>" can be found with description "<description>"

    Examples:
      | title       | description                                      |
      | Assignments | Todos related to any course assignments          |
      | Midterms    | Todos realted to any course midterm examinations |

  Scenario: Create a new category with minimal data (Alternate flow)
    When the user tries to create a new category with title "<title>"
    Then the category named "<title>" is successfully created with an empty description

    Examples:
      | title       |
      | Assignments |
      | Midterms    |

  Scenario: Create a category item with a missing mandatory field (Error flow)
    When the user tries to create a new category with description "<description>"
    Then the new category is not created

    Examples:
      | description                                      | 
      | Todos related to any course assignments          | 
      | Todos realted to any course midterm examinations |