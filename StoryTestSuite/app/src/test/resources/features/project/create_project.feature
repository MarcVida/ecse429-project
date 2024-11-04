Feature: Create a Project

  As a user,
  I would like to create a project,
  So that I can manage and organize related tasks efficiently.

  Scenario: Create a project with all required fields (Normal flow)
    When the user tries to create a project with title "Team Project", completed status "false", active status "true", description "", and tasks with IDs "1, 2"
    Then the project "Team Project" is successfully created with completed status "false", active status "true", description "", and task IDs "1, 2"
    And the project "Team Project" can be found with completed status "false", active status "true", description "", and task IDs "1, 2"

  Scenario: Create a project with minimal data (Alternate flow)
    When the user tries to create a project with title "Simple Project" and active status "true"
    Then the project "Simple Project" is successfully created with default values for other fields

  Scenario: Create a project with missing mandatory fields (Error flow)
    When the user tries to create a project without a title
    Then the project is not created
