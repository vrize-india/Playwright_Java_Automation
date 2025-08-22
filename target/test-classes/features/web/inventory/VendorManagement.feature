@inventory @regression
Feature: Vendor Management in BOH System
  As a BOH system user
  I want to manage vendors
  So that I can maintain supplier information

  @XrayKey=TONIC-10906
  Scenario: Add a new vendor
    Given the user navigates to "APPS" on the top-level navigation
    When the user clicks on "INVENTORY"
    Then the inventory page should display with information and options
    When the user clicks on the "Vendors" tab
    Then the vendor list should be displayed
    When the user clicks on the "+" icon labeled "Add Vendor" at the top right corner of the page
    Then the "Add Vendor" modal window should appear
    When the user enters all required information in the Name field
    When the user enters all required information in the Address field
    When the user enters all required information in the Phone Number field
    When the user enters all required information in the Contact Number field
    And clicks the Save button on the modal window
    And a confirmation message Vendor added should be displayed