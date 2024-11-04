Feature: Get Project(s)

  As a user,
  I would like to get project details by ID or retrieve all projects,
  So that I can view either a specific project or the full list of projects.

   Scenario: Get a project by a valid ID (Normal flow)
    Given the project with specific ID exists
    When the user tries to get the project with ID
    Then the project with ID is returned with:
      | title        | completed | active | description |
      | Team Project | false     | true   | ""          |

    Examples:
      | id |
      | <dynamically created ID> |

  Scenario: Get all projects (Alternative flow)
    Given multiple projects exist
    When the user tries to get all projects
    Then the number of projects returned is greater than 0

  Scenario: Get a project by a non-existing ID (Error flow)
    Given no project with ID "<id>" exists
    When the user tries to get the project with non-existed ID "<id>"
    Then no project is found, and an error message is returned

    Examples:
      | id   |
      | 999  |
      | 1009 |
