@regression @menu
Feature: Add New Item functionality in Items screen
  As a user with appropriate permissions
  I want to add a new item via the Items screen
  So that I can manage menu items efficiently

  Background:
    Given The user navigates to "Configuration" > "Items"
    Then The user is on the 'Items' screen
    When The user clicks on the 'Add New' CTA
    Then A new blank row should be added at the top of the table
    And The 'Add New' CTA button should be disabled
    Then The values should be visible in input fields "Add New Item","Add New Sales Group","Add New Modifiers Sets"
    
  @XrayKey=TONIC-7343 @p3
  Scenario: Add New Item and validate UI and error messages
    When The user enters invalid 'SPACE' values in the 'Add New Item' field
    And The user attempts to click on the Save button without entering any values
    Then An error message "Item name is required" should be displayed below the field
    When The user does not select any dropdown values in the "Sales Groups" field
    Then An error message "Sales group is required" should be displayed below the field

  @XrayKey=TONIC-7341 @p1
  Scenario: Add New Item and save with modal options
    And The user enters an item name in the 'Add New Item' field
    And The user chooses a Sales Group
    And The user chooses a Modifier Set
    And The user clicks on Save at the top right corner
    Then The user receives the message 'Successfully added the Item to the list'
    And The user is able to search and view the updated or newly added item using the 'Search here' field
    And The user deletes the updated or newly added item using the 'Search here' field
