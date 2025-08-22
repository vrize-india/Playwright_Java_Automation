@menu @MenuConfiguration @regression
Feature: Menu Category Management

  Background:
    Given The user navigates to "Configuration" > "Menu Configuration"
    And User is on the "Menu Configuration" page

  @XrayKey=TONIC-7243 @P2
  Scenario: Add new category, new item in the category, delete item in the category and delete the category in the Menu Configuration
    When The user clicks on plus icon to "Add New Category"
    And The user enters value as "Foods" in "Enter category name here" field
    And The user clicks on "Saves the category & redirects" icon
    Then "Category Added Successfully" Pop up message should display
    When The user clicks on the "add-new-item" button
    And The user selects any item from the dropdown
    And The user clicks on "Save" icon
    Then "New Item Added Successfully" Pop up message should display
    When The user clicks on delete icon for item
    And The user clicks on "Yes, Delete" CTA
    Then "Successfully deleted the item" Pop up message should display
    And The user clicks on the close icon
    When The user selects the created category for deletion
    Then "Do you want to delete" confirmation modal should be displayed
    When The user correctly types category name in the confirmation text box
    And The user clicks on "Yes" CTA
    Then "deleted" Pop up message should display
    And Deleted category should be removed from the Menu Configuration
    And Category Count should be same after create and delete of the category



  @XrayKey=TONIC-8016 @P1 @E2E
  Scenario:Menu category - Save CTA Workflow
    When The user clicks on plus icon to "Add New Category"
    And The user enters value as "Mercury" in "Enter category name here" field
    And The user clicks on "Saves the category & redirects" icon
    Then "Category Added Successfully" Pop up message should display

    #FOH Validation


  @XrayKey=TONIC-14288 @P2 @DualPricingEnabled @E2E
  Scenario: Verify the functionality of Add New CTA Workflow in Category sub tab of Additional Charges
    When The user navigates to 'Home' screen
    And The user navigates to "Configuration" > "Menu Configuration"
    When The user clicks on plus icon to "Add New Category"
    And The user enters value as "Foods" in "Enter category name here" field
    And The user enables the Store and Online ordering option
    And The user clicks on "Saves the category & redirects" icon
    Then "Category Added Successfully" Pop up message should display
    When The user clicks on "Additional Charges" CTA
    And The user clicks on the "add-new-additional-charge" button
    And The Column Headers should be visible
    And The "Save","Cancel" CTA should be visible under the Action column

    @SampleE2E
    Scenario: Add new category in BOH and validate the added category in FOH
#      When The user clicks on plus icon to "Add New Category"
#      And The user enters value as "Foods" in "Enter category name here" field
#      And The user enables the "store-toggle"
#      And The user clicks on "Saves the category & redirects" icon
#      Then "Category Added Successfully" Pop up message should display
      When The user launches the mobile application with resource id "741"
      And Creates a new order by clicking on QS
      When The user clicks on Swipe Right icon on mobile
      Then User should be able to see added category

  @XrayKey=TONIC-8024 @P1 @E2E
  Scenario:Menu category - Save & Continue CTA Workflow
    When The user clicks on plus icon to "Add New Category"
    And The user enters value as "Drinks" in "Enter category name here" field
    And The user enables the Store and Online ordering option
    And The user clicks on "Saves the category & redirects" icon
    Then "Category Added Successfully" Pop up message should display
    And The 'Add Menu Items' dialog should open for adding items to the category
    When The user clicks on the close icon
    And The user selects the created category for deletion
    And The user correctly types category name in the confirmation text box
    And The user clicks on "Yes" CTA
    Then "deleted" Pop up message should display
