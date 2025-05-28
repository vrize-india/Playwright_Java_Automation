Feature: API POST Create User
  As an API user
  I want to create a new user via POST request
  So that I can verify user creation

  Scenario: Create user with POST API
    Given the API endpoint for user creation is available
    When the user sends a POST request to create a user
    Then the user should be created successfully 