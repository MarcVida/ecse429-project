Feature: Get Project(s)

  As a user,
  I would like to get project details by ID or retrieve all projects,
  So that I can view either a specific project or the full list of projects.

  Scenario: Get a project by a valid ID (Normal flow)
    Given the project with ID "<id>" exists with the following details:
      | title       | completed | active | description | taskIds  |
      | <title>     | <completed> | <active> | <description> | <taskIds> |
    When the user tries to get the project with ID "<id>"
    Then the project with ID "<id>" is returned with:
      | title       | completed | active | description | taskIds  |
      | <title>     | <completed> | <active> | <description> | <taskIds> |

    Examples:
      | id | title       | completed | active | description | taskIds   |
      | 1  | Office Work | false     | false  | ""          | "1, 2"    |
      | 2  | Team Project| true      | true   | "Planning"  | "3, 4, 5" |

  Scenario: Get all projects (Alternative flow)
    Given multiple projects exist with the following details:
      | id | title       | completed | active | description | taskIds   |
      | 1  | Office Work | false     | false  | ""          | "1, 2"    |
      | 2  | Team Project| true      | true   | "Planning"  | "3, 4, 5" |
    When the user tries to get all projects
    Then all projects are returned with the following details:
      | id | title       | completed | active | description | taskIds   |
      | 1  | Office Work | false     | false  | ""          | "1, 2"    |
      | 2  | Team Project| true      | true   | "Planning"  | "3, 4, 5" |

  Scenario: Get a project by a non-existing ID (Error flow)
    Given no project with ID "<id>" exists
    When the user tries to get the project with ID "<id>"
    Then no project is found, and an error message is returned

    Examples:
      | id  |
      | 99  |
      | 100 |
