@menu @regression @modifierSet @E2E
Feature: Modifier Set Management
  As a system administrator
  I want to verify the visibility of the add and delete Icon for each row in the Modifier set screen
  So that I can ensure users can add and delete modifier set as needed

  Background:
#Modifiers
    Given The user navigates to "Configuration" > "Modifiers"
    And The user search and add the modifier in the Modifiers page

# Modifier sets
    When The user navigates to "Configuration" > "Modifier Sets"
    And The user search and add the modifier sets in the Modifier sets page

#PreModifiers
    When The user navigates to "Configuration" > "Pre-Modifiers"
    And The user creates a Pre-Modifier
    And The user assigns Modifier Sets To Pre Modifier

#Items
    When The user navigates to "Configuration" > "Items"
    And The user is adding an Item
    And The user is adding Modifier Sets To an item

#Menu Configuration
    When The user navigates to "Configuration" > "Menu Configuration"
    And The user checks and creates a Category
    And The user adds an item to the category

  @XrayKey=TONIC-5970 @p1
  Scenario: Validating the Add new functionality in modifier set
    When The user navigates to "Configuration" > "Modifier Sets"
    And User is on the "Modifier Sets" page
    When The user adds an random modifier sets "New Modifier Set" in the Modifier sets page
    And The user search and add the modifier sets in the Modifier sets page
    Then The user deletes the added modifier sets in the Modifier sets page