Feature: Login
  As a user
  I want to login to the application
  So that I can access the dashboard

  Scenario: Login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the dashboard should be displayed 