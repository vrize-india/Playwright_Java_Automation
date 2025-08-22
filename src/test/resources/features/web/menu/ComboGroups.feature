@regression @menu @combo
Feature: Add and Edit Combo functionality in Combo group screen
  As a user
  I want to add and edit  a new Combo group via the Combo Group screen
  So that I can organize menu items effectively

  Background:
    Given The user navigates to "Configuration" > "Combo Groups"
    Then User is on the "Combo Groups" page

  @XrayKey=TONIC-7302 @P1
  Scenario: Add a new combo group
    When User clicks "Add New" CTA
    Then The "Add New" CTA should be disabled
    And The input fields "Add new Combo Group" and "Add new options" should be visible
    When User Enters the Combo Group Name in "Add new Combo Group"
    And User need to choose the Available Options using field "Add new options"
    And The user clicks on the "Save" icon
    And The user is able to search and view the newly added Combo using the 'Search here' field
    And The user deletes the newly added combo using the 'Search here' field

  @XrayKey=TONIC-7324 @P2
  Scenario: Edit combo group Created
    Then Each combo row displays a 'Edit' button
    When User clicks on the Combo Group Edit Icon under the Actions column
    And User need to Edit Combo Group name in "Add new Combo Group"
    And User need to choose the Available Options using field "Add new options"
    And The user clicks on the "Save" icon
    And The user is able to search and view the updated  Combo using the 'Search here' field
    And The user deletes the updated combo using the 'Search here' field

  @XrayKey=TONIC-7322 @P2
  Scenario: Visibility of Delete Icon for Each Row and Display of Confirmation Popup on Clicking Delete Icon
    Then Each combo row displays a 'Delete' button
    When User clicks on the Combo Group Delete Icon under the Actions column
    Then The system should display a Delete confirmation popup
    And The delete confirmation popup should contain "Close" icon "Trash" icon and Confirmation Text
    And The delete popup should have "Yes, Delete" CTA and "Cancel" CTA
    When User clicks on "Close" Icon
    Then User should see popup get closed

  @XrayKey=TONIC-14245 @P2
  Scenario: Deleting the Combo Group  Successfully
    When User clicks "Add New" CTA
    And User Enters the Combo Group Name in "Add new Combo Group"
    And User need to choose the Available Options using field "Add new options"
    And The user clicks on the "Save" icon
    When User deletes the newly added combo from the list
    Then The system should display a Delete confirmation popup
    And User clicks "Yes, Delete" CTA
    And The system should display the message 'Successfully deleted the Combo Group from the list'
    Then The deleted combo should not be visible when searched using the 'Search here' field


  @XrayKey=TONIC-14246 @P2
  Scenario: Canceling the Deleting the Combo Group  Successfully
    When User clicks on the Combo Group Delete Icon under the Actions column
    Then The system should display a Delete confirmation popup
    And User clicks "Cancel" CTA
    Then User should see the list of Combo Groups

  @XrayKey=TONIC-7305 @P2
  Scenario: Add New Combo and validate UI and error messages
    When User clicks "Add New" CTA
    Then A new blank row should be added at the top of the Combo table
    And The "Add New" CTA should be disabled
    And The input fields "Add new Combo Group" and "Add new options" should be visible
    When User enters invalid 'SPACE' values in the 'Add new Combo Group' field
    And User attempts to click on the Save button without entering any values
    Then An error message "Name is required" should be displayed below the field
