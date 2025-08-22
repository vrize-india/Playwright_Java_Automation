@menu @regression @modifiers
Feature: Modifiers Management
  As a system administrator
  I want to verify the visibility of the Edit Icon for each row in the Modifiers screen
  So that I can ensure users can edit modifiers as needed

  Background:
    Given The user navigates to "Configuration" > "Modifiers"
    And User is on the "Modifiers" page

  @XrayKey=TONIC-7438 @p2
  Scenario: Visibility of Edit Icon for Each Row in Modifiers screen
    And The user should see a list of Modifiers configured in BoH
    Then Each row should have an Edit Icon visible under the Actions column
    When The user clicks on the Edit Icon under the Actions column
    Then The row becomes editable and "Cancel" button is visible
    And The existing values should be visible in input field "Enter Modifier Name","Enter Display Name", "Enter Online Ordering Name", "Multiplier",
    And The icons should change to "Save" and "Cancel"
    When The user enters a value as "FrenchFries" in "Enter Modifier Name" field
    Then The system should validate the field values
    When The user clicks on the "Save" icon
    Then The system should save the updated changes

  @XrayKey=TONIC-7431 @p2
  Scenario: Validating the Add new functionality in modifiers
    When The user should be able to see the "Add New" CTA
    Then The "Add New" CTA should be enabled by default
    When User clicks "Add New" CTA
    Then The "Add New" CTA should be disabled
    When User must Enters "French Fries" in "Enter Modifier Name" and Optional fields "French Fries"  in "Enter Display Name" "yes" as "Enter Online Ordering Name","1" in "Multiplier" "10" in "Preparation Time" and "2" in "Price"
    And The user clicks on the "Save" icon
    Then User new Modifier is added

  @XrayKey=TONIC-7420 @p2
  Scenario: Interaction with Edit Icon - Cancel CTA
    And The user should see a list of Modifiers configured in BoH
    Then Each row should have an Edit Icon visible under the Actions column
    When The user clicks on the Edit Icon under the Actions column
    And The row becomes editable and "Cancel" button is visible
    And The existing values should be visible in input field "Enter Modifier Name","Enter Display Name", "Enter Online Ordering Name", "Multiplier",
    Then The icons should change to "Save" and "Cancel"
    When The user enters a value as "FrenchFries" in "Enter Modifier Name" field
    And The system should validate the field values
    And The user clicks on the "Cancel" icon
    Then The system should not save the updated changes


  @XrayKey=TONIC-14236 @p2
  Scenario: Modifier sets - Save CTA Edit functionality
    And The user should see a list of Modifiers configured in BoH
    Then Each row should have an Edit Icon visible under the Actions column
    When The user clicks on the Edit Icon under the Actions column
    Then The "Add New" CTA should be disabled
    Then The row becomes editable and "Cancel" button is visible
    When The existing values should be visible in input field "Enter Modifier Name","Enter Display Name", "Enter Online Ordering Name", "Multiplier",
    Then The icons should change to "Save" and "Cancel"
    When The user enters a value as "FrenchFries" in "Enter Modifier Name" field
    Then The system should validate the field values
    When The user clicks on the "Save" icon
    Then The system should save the updated changes

  @XrayKey=TONIC-14237 @p2
  Scenario: Visibility of Edit Icon for Each Row in Modifiers screen
    And The user should see a list of Modifiers configured in BoH
    Then Each row should have an Edit Icon visible under the Actions column
    When The user clicks on the Edit Icon under the Actions column
    Then The row becomes editable and "Cancel" button is visible
    And The existing values should be visible in input field "Enter Modifier Name","Enter Display Name", "Enter Online Ordering Name", "Multiplier",
    And The icons should change to "Save" and "Cancel"
    When The user enters a value as "FrenchFries" in "Enter Modifier Name" field
    Then The system should validate the field values
    When The user clicks on the "Save" icon
    Then The system should save the updated changes
    Then The icon should revert to the Pencil icon
