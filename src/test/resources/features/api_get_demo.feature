Feature: API GET Demo
  As an API user
  I want to perform a GET request
  So that I can verify the API response

  Scenario: Perform GET API demo
    Given the API endpoint is available
    When the user sends a GET request
    Then the response should be successful 