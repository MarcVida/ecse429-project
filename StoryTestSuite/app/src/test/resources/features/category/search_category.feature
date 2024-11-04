Feature: Search for category

  As a user,
  I would like to search for an existing category,
  So that I can find a category that I'm interested in.

  Scenario: Search for a category by full title (Normal flow)
    Given the category named "<title>" exists
    When the user searches for the category named "<title>"
    Then the category named "<title>" is found

    Examples:
      | title       |
      | Assignments |
      | Midterms    |

  Scenario: Search for a category by partial title (Alternate flow)
    Given the category named "<title>" exists
    When the user searches for the category with partial title "<partialTitle>"
    Then the category named "<title>" is found

    Examples:
      | title       | partialTitle |
      | Assignments | Assign       |
      | Midterms    | Mid          |

  Scenario: Search for a non-existing category (Error flow)
    When the user searches for a non-existing category named "<title>"
    Then no category is found

    Examples:
      | title  |
      | Labs   |
      | Finals |