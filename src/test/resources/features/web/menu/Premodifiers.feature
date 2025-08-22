@menu @premodifiers @regression
Feature: Workflow for Pre-Modifiers

  Background:
    Given The user navigates to "Configuration" > "Pre-Modifiers"
    Then User is on the "Pre-Modifiers" page

  @XrayKey=TONIC-10466 @P2
  Scenario: Add multiple pre-modifiers to a Pre-Modifier Group and save the updates
    When The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    And The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user added "5" new premodifiers and applied "circle-slash", "dollar-plus", "dollar-equals", "percentage", "multiply" make the changes to "Enter Pre-Modifier Name", "Enter Online Ordering Name","Enter Kitchen Name" fields and "save" the premodifiers
    When The user click on "Save" button
    Then The user searches for the newly created group using the "Search here" field that is saved successfully

  @XrayKey=TONIC-10468 @P1 @DualPricingEnabled
  Scenario: Create a New Pre-Modifier with Dual Price Enabled
    And The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    When The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user enters the premodifiers name "Enter Pre-Modifier Name" in the field
    And The user selects "Post" under Pre or Post field
    And The user disables the checkbox under the Online Ordering header
    And The user chooses the "percentage" icon under the Pricing Type header
    And The user enters "1.75" in the Percentage field and "save" the premodifiers
    When The user click on "Save" button
    Then The user searches for the newly created group using the "Search here" field that is saved successfully

  @XrayKey=TONIC-10470 @P2 @DualPricingEnabled
  Scenario: Validate Price text box with dual price enabled
    And The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    When The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user enters the premodifiers name "Enter Pre-Modifier Name" in the field
    And The user chooses the "dollar-plus" icon under the Pricing Type header
    Then The user should be able to see two fields: "Card Price" and "Cash Price"

  @XrayKey=TONIC-10473 @P2
  Scenario: Validations for "Online Ordering Name" field
    And The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    When The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user observes the field "Enter Online Ordering Name"
    Then The field "Enter Online Ordering Name" should have attribute "aria-required" and it should not be "true" so that it verifies field is optional
    And The field "Enter Online Ordering Name" should have a minimum length "minlength" of 0
    And The user enters the premodifiers name "Enter Pre-Modifier Name" in the field and "Enter Online Ordering Name" should support "abc123@#-_!&*()" Characters and "save" the premodifiers

  @XrayKey=TONIC-10471 @P2
  Scenario: Validate columns "Online Ordering Name" and "Kitchen Name" and their placements
    And The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    When The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    Then The user should be presented with columns Pre-Modifiers, Online Ordering Name, Kitchen Name , Pre or Post, "Online Ordering", Pricing Type and actions-header

  @XrayKey=TONIC-14410 @P1
  Scenario: Validation of Cancel workflow
    When The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    And The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user makes changes to fields "Enter Pre-Modifier Name", "Enter Online Ordering Name" and "Enter Kitchen Name" and "save" the premodifiers
    And The user clicks on "cancel" button
    Then The user enters the cancelled pre-modifier group name into the "Search here" field to search and verifies that "No Results Found"

  @XrayKey=TONIC-10474 @P1
  Scenario: Validation of Save workflow
    When The user click on "Add New" CTA and verifies "Configure Pre-Modifier Group" Text is Visible
    And The user Adds "Enter Pre-Modifier Group Name" and click on "Add new" CTA in premodifiers modal
    And The user makes changes to fields "Enter Pre-Modifier Name", "Enter Online Ordering Name" and "Enter Kitchen Name" and "save" the premodifiers
    When The user click on "Save" button
    Then The user searches for the newly created group using the "Search here" field that is saved successfully