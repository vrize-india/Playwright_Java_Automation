@labor @regression
Feature: Roles Management
  As a system administrator
  I want to manage user roles and their hourly wages
  So that I can maintain accurate compensation information

  @XrayKey=TONIC-7228
  Scenario: Hourly wages - Changing the hourly wage amount
    Given I am on the Roles page
    When I find a user in the roles list
    And get the current hourly wages of specific user
    And I click edit for the user
    And User increasing the hourly wages Amount
    And I save the changes
    And the changes should be saved successfully
    And get the updated hourly wages of specific user
    Then validate hourly wages increased after updated

  @XrayKey=TONIC-7229
  Scenario: Hourly wages - Decreasing the hourly wage amount
    Given I am on the Roles page
    When I find a user in the roles list
    And get the current hourly wages of specific user
    And I click edit for the user
    And User decreasing the hourly wages Amount
    And I save the changes
    And the changes should be saved successfully
    And get the updated hourly wages of specific user
    Then validate hourly wages decreased after updated