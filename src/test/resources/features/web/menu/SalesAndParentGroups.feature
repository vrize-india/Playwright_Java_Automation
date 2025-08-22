@menu @regression
Feature: Create Sales Group in Sales & Parent Groups screen
  As an authorized user
  I want to add a new Sales Group from the Sales & Parent Groups section
  So that I can organize and manage sales groups effectively

  Background:
    Given The user navigates to "Configuration" > "Sales Groups & Parent Groups"
    And User is on the "Sales Groups & Parent Groups" page
    And User should see a list of Sales And Parent Groups displayed

  @XrayKey=TONIC-7367 @P2
  Scenario: Create and save a new Sales Group
    When User clicks "Add New" CTA
    And User should see the inline editor enabled to add information
    Then The "Add New" CTA should be disabled
    When User enters the new sales group in the "Add New Sales Group" field
    And User chooses a Parent Group from the "Select Parent Group" dropdown
    And User checks the dialog box for Use for Seat Count
    And User clicks on Save CTA
    Then User should see that the new sales group is saved successfully and validated using the "Search here" field

  @XrayKey=TONIC-7380 @P2
  Scenario: Edit a Sales Group and verify persistence after refresh
    Then User should see the Edit Icon under the Actions column for each row
    When User clicks on the edit icon
    And User edit the existing sales group name in "Add New Sales Group" field
    And User clicks on Save CTA
    And User receives the message 'Successfully updated the Sales Group details to this list'
    Then User should see edited sales group is visible

  @XrayKey=TONIC-10488 @P2
  Scenario: Enable Inline Editor on Clicking Edit Icon
    Then User should see the Edit Icon under the Actions column for each row
    When User clicks on the edit icon
    Then The "Add New" CTA should be disabled
    And User should see the inline editor enabled to add information
    And User should see the existing values visible in input field "Add New Sales Group","Select Parent Group",Use for Seat Count
    And User should see the icons change to Save and Cancel
    When User clicks on the Cancel icon should close the inline editor
    Then User is on the "Sales Groups & Parent Groups" page
    And User should see a list of Sales And Parent Groups displayed

  @XrayKey=TONIC-7379 @P2
  Scenario: Display Confirmation Popup on Clicking Delete Icon for Sales Groups & Parents Group Page
    Then User should see the Delete Icon under the Actions column for each row
    When User clicks on Delete Icon
    Then User should see a Delete confirmation popup
    And User should see the delete confirmation popup containing the "Close" icon "Trash" icon and Confirmation Text
    And User should see the delete popup containing "Yes, Delete" CTA and "Cancel" CTA
    When User clicks on "Close" Icon
    Then User should see popup get closed

  @XrayKey=TONIC-14248 @P2
  Scenario: Delete a sales group from Sales Groups & Parents Group
    #Precondition
    When User clicks "Add New" CTA
    When User enters the new sales group in the "Add New Sales Group" field
    And User chooses a Parent Group from the "Select Parent Group" dropdown
    And User checks the dialog box for Use for Seat Count
    And User clicks on Save CTA
    Then User should see that the new sales group is saved successfully and validated using the "Search here" field

    When User deletes the newly added sales group from the list
    Then User should see a Delete confirmation popup
    When User clicks on "Yes, Delete" CTA
    And User should see a display message 'Successfully deleted the Sales Group from the list'
    Then User should see that the respective Sales Group is deleted from the system

  @XrayKey=TONIC-14247 @P2
  Scenario: Cancel the delete from Delete popup
    When User clicks on Delete Icon
    Then User should see a Delete confirmation popup
    When User clicks on "Cancel" CTA in delete popup
    Then User should see popup get closed
    And User should see a list of Sales And Parent Groups displayed
