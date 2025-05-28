Feature: Terminal Navigation
  As a user
  I want to navigate to the terminals page
  So that I can manage terminals

  Scenario: Navigate to terminals page
    Given the user is logged in
    When the user navigates to the terminals page
    Then the terminals page should be displayed 