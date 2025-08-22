@regression @item @menu
Feature: Edit Existing Item functionality in Items screen
  As a user with appropriate permissions
  I want to edit existing items via the Items screen
  So that I can update item details and manage their availability using toggle controls

  Background:
    Given The user navigates to "Configuration" > "Items"
    Then The user is on the 'Items' screen
    When The user clicks on the 'Add New' CTA
    And The user enters an item name in the 'Add New Item' field
    And The user chooses a Sales Group
    And The user chooses a Modifier Set
    And The user clicks on Save at the top right corner
    Then The user receives the message 'Successfully added the Item to the list'
    When The user navigates to 'Home' screen
    And The user navigates to "Configuration" > "Items"
    Then The user is on the 'Items' screen
    
  @XrayKey=TONIC-7348 @p1
  Scenario: Items - Edit functionality
    And The user is able to see the list of items
    And The user searches for an item using the 'Search here' field and clicks the edit icon
    Then The values should be visible in input fields "Add New Item","Add New Sales Group","Add New Modifiers Sets"
    When The user enters an item name in the 'Add New Item' field
    And The user edits the Sales Group
    And The user edits the Modifier Set
    And The user clicks on Save at the top right corner
    Then The user receives the message 'Successfully updated the Item details'
    And The user is able to search and view the updated or newly added item using the 'Search here' field
    And The user deletes the updated or newly added item using the 'Search here' field

  @XrayKey=TONIC-7360 @p2
  Scenario: Verify toggle default state, edit behavior, and visibility in Items configuration
    And Each item row displays a 'Toggle' button
    When The user clicks on the 'Add New' CTA
    And The user enters an item name in the 'Add New Item' field
    And The user chooses a Sales Group
    And The user chooses a Modifier Set
    Then The toggle should be disabled before saving the item
    When The user clicks on Save at the top right corner
    And The user is able to search and view the updated or newly added item using the 'Search here' field
    Then The toggle should be enabled
    When The user toggles the button from 'Enabled' to 'Disabled' during edit
    Then The user receives the message "Successfully disabled"
    And The toggle should be disabled
    When The user toggles the button from 'Disabled' to 'Enabled' during edit
    Then The user receives the message "Successfully enabled"
    And The toggle should be enabled
    When The user is able to search and view the updated or newly added item using the 'Search here' field
    Then The toggle should be enabled
    And The user deletes the updated or newly added item using the 'Search here' field

  @XrayKey=TONIC-7354 @P2
  Scenario: Visibility of Delete Icon for Each Row and Display of Confirmation Popup on Clicking Delete Icon
    And  Each item row displays a 'Delete' button
    When User clicks on the Item Delete Icon using the 'Search here' field
    Then The user receives the message 'Delete Item'
    And The delete confirmation popup should contain Item Name with title 'Delete Item', Trash Icon and Confirmation text 'Do you really want to delete this record?'
    And The delete popup for items have "Yes, Delete" CTA and "Cancel" CTA


  @XrayKey=TONIC-14334  @P2
  Scenario: Deleting the Item  Successfully
    When User clicks on the Item Delete Icon using the 'Search here' field
    Then "Yes, Delete" CTA and "Cancel" CTA should be enabled
    When The user clicks on the 'Yes, Delete' CTA
    Then The user receives the message 'Successfully deleted the item from the list'
    And The deleted item should not be visible when searched using the 'Search here' field

  @XrayKey=TONIC-14335  @P2
  Scenario: Items - Cancelling the delete flow
    And  Each item row displays a 'Delete' button
    When User clicks on the Item Delete Icon using the 'Search here' field
    And The user clicks on the 'Cancel' CTA
    Then The delete confirmation popup should close
    And The user is able to search and view the updated or newly added item using the 'Search here' field
    And The user deletes the updated or newly added item using the 'Search here' field

  @XrayKey=TONIC-5781 @P1 @E2E
  Scenario: Verify Successful Save Operation
    And The user is adding an Item
    When The user navigates to "Configuration" > "Menu Configuration"
    And The user checks and creates a Category
    And The user adds an item to the category
    Then The item should be visible in the category

    #FOH Validation

