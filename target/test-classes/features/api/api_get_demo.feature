@DemoAPI
Feature: API GET Demo
  As an API user
  I want to retrieve users via GET request
  So that I can verify the response and headers

  Scenario: Get users and verify response headers
    When the user sends a GET request to retrieve users
    Then the response status code should be 200
    And the response header "server" should be "cloudflare"
    And the response header "content-type" should be "application/json; charset=utf-8" 