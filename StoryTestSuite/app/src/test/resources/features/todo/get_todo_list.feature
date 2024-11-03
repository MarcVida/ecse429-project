Feature: Get TODO list with conditions

  As a user,
  I would like to get a specific list of TODO items,
  So that I have control over which tasks I can see

  Scenario: Get the list of all TODO items (Normal flow)
    Given the TODO items with the following titles exist:
      | <title1> |
      | <title2> |
      | <title3> |
    When the user tries to get the list of all TODO items
    Then the TODO items with the following titles are found:
      | <title1> |
      | <title2> |
      | <title3> |

    Examples:
      | title1                      | title2                     | title3                |
      | Finish ECSE 429 assignment  | Study for ECSE 331 midterm | Get some sleep        |
      | Read MATH 208 lecture notes | Write lab report           | Work on ECSE 428 task |

  Scenario: Get the list of TODO items with a specific status (Alternate flow)
    Given the TODO items with the following titles and statuses exist:
      | title    | doneStatus    |
      | <title1> | <doneStatus1> |
      | <title2> | <doneStatus2> |
      | <title3> | <doneStatus3> |
    When the user tries to get the list of TODO items with status "<doneStatus>"
    Then the TODO items with the following titles are found:
      | <title1> |
      | <title2> |

    Examples:
      | title1     | doneStatus1 | title2       | doneStatus2 | title3       | doneStatus3 | doneStatus |
      | Assignment | false       | Study        | false       | Sleep        | true        | false      |
      | Read       | true        | Write report | true        | work on task | false       | true       |

  Scenario: Get the list of TODO items with a non-existing property (Error flow)
    Given the TODO item "Write lab report" exists
    When the user tries to get the list of TODO items with property "<property>" set to "<value>"
    Then no TODO item is found

    Examples:
      | property    | value |
      | progress    | 50%   |
      | priority    | low   |