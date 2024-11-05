Feature: Delete a category

  As a user,
  I would like to delete an existing category,
  So that I only keep necessary categories.

  Scenario: Delete an existing category (Normal flow)
    Given the category named "<title>" exists
    When the user tries to delete the category named "<title>"
    Then the category named "<title>" is successfully deleted
    And the category named "<title>" cannot be found

    Examples:
      | title       |
      | Assignments |
      | Midterms    |

  Scenario: Delete all categories (Alternate flow)
    Given the category named "<title1>" exists
    And the category named "<title2>" exists
    When the user tries to delete all categories
    Then all categories are successfully deleted
    And the category named "<title1>" cannot be found
    And the category named "<title2>" cannot be found

    Examples:
      | title1      | title2 |
      | Assignments | Finals |
      | Midterms    | Labs   |

  Scenario: Delete a non-existent category (Error flow)
    When the user tries to delete the category with id "<id>"
    Then the user is informed that the category does not exist
    And the category with id "<id>" cannot be found

    Examples:
      | id   |
      | 9999 |
      | -1   |