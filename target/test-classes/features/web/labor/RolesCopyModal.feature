@regression @labor
Feature: Roles Copy Modal
  As a user
  I want to copy a role and see the correct modal fields
  So that I can verify the UI and default values
  @XrayKey=TONIC-4797
  Scenario: Copy role and verify modal fields are displayed
    Given the user is on the modal screen with a duplicate icon
    When the user taps on the duplicate icon
    Then the screen expands
    And the following fields are displayed:
      | Field                   | Placeholder Text | Default Value         |
      | Store Name Field        | -               | Selected Store Name   |
      | Inherit Permissions From| "Select"        | -                     |


  @XrayKey=TONIC-6653
  Scenario: Duplicate roles are displayed
    Given the user is on the modal screen
    Then the duplicate icon is displayed next to the close button