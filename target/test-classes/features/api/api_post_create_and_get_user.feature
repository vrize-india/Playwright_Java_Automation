@DemoAPI
Feature: API POST Create and GET User
  As an API user
  I want to create a new user via POST request and verify via GET
  So that I can ensure user creation and retrieval works

  Scenario: Create user and verify with GET API
    When the user sends a POST request to create a user with random email
    Then the user should be created successfully with status code 201
    And the user can be retrieved by id with status code 200
    And the response should contain the created user's id and email 