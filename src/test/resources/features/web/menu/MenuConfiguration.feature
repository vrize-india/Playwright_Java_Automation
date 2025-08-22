@menu @MenuConfiguration @regression
Feature: Menu Category Management

  Background:
    Given The user navigates to "Configuration" > "Menu Configuration"
    And User is on the "Menu Configuration" page

    @SampleE2E
    Scenario: Add new category in BOH and validate the added category in FOH
      When The user clicks on plus icon to "Add New Category"
      And The user enters value as "Foods" in "Enter category name here" field
      And The user enables the "store-toggle"
      And The user clicks on "Saves the category & redirects" icon
      Then "Category Added Successfully" Pop up message should display
      When The user launches the mobile application with resource id "741"
      And Creates a new order by clicking on QS
      When The user clicks on Swipe Right icon on mobile
      Then User should be able to see added category
