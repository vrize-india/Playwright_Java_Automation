@menu @premodifiersDPDisabled @regression

Feature: Workflow for Pre-Modifiers with Dual Price Disabled

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


  @XrayKey=TONIC-14439 @P1 @DualPricingDisabled @E2E
  Scenario: Create a New Pre-Modifier with Dual Price Enabled for Add Amount
    When The user navigates to "Configuration" > "Pre-Modifiers"
    And The user searches for the existing Pre Modifier Group
    When The user clicks on the edit icon for the existing Pre Modifier Group
    When The user click on "Add new" CTA in premodifiers modal
    And The user enters the premodifiers name "Enter Pre-Modifier Name" in the field
    And The user selects "Post" under Pre or Post field
    And The user chooses the "dollar-plus" icon under the Pricing Type header
    And The user enters "1.75" in the Percentage field and "save" the premodifiers
    Then The user click on "Save" button

  #FOH Validation

  #BOH Validation
#    And The user searches for the existing Pre Modifier Group
#    When The user clicks on the edit icon for the existing Pre Modifier Group
#    Then The user deletes the newly created Pre Modifier
#    And The user click on "Save" button