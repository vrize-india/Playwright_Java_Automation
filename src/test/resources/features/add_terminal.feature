Feature: Add Terminal
  As an admin
  I want to add a new terminal
  So that I can expand the system

  Scenario: Open add terminal dialog
    Given the user is logged in
    When the user navigates to the terminals page
    And the user clicks the add terminal button
    Then the add terminal dialog should be visible 