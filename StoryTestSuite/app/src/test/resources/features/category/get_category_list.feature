Feature: Get a list of categories based off of conditions

  As a user,
  I would like to get a specific list of categories,
  So that I can manage all the different categories of todos

  Scenario: Get a list of all categories (Normal flow)
    Given the categories with the following titles:
      | <title1> |
      | <title2> |
      | <title3> |
    When the user tries to get a list of all categories
    Then the categories with the following titles are found:
      | <title1> |
      | <title2> |
      | <title3> |

    Examples:
      | title1      | title2  | title3     |
      | Assignments | Labs    | Finals     |
      | Midterms    | Quizzes | Interviews |

  Scenario: Get a list of categories with the same description (Alternate flow)
    Given the categories with the following titles and descriptions exist:
      | title    | description    |
      | <title1> | <description1> |
      | <title2> | <description2> |
      | <title3> | <description3> |
    When the user tries to get a list of categories with descriptions of "<description>"
    Then the categories with the following titles are found by description:
      | <title1> |
      | <title2> |

    Examples:
      | title1      | description1       | title2   | description2       | title3 | description3              | description        |
      | Assignments | Course Assesments  | Projects | Course Assesments  | Labs   | Lab reports and findings  | Course Assesments  |
      | Papers      | Course Evaluations | Quizzes  | Course Evaluations | Finals | Final course examinations | Course Evaluations |

  Scenario: Get the list of categories with a non-existing property (Error flow)
    Given the category named "Assignments" exists
    When the user tries to get the list of categories with non-existent property "<property>" set to "<value>"
    Then no category is found

    Examples:
      | property    | value |
      | progress    | 50%   |
      | priority    | low   |