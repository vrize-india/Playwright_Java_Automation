@Mobile
Feature: Mobile Account
  As a mobile user
  I want to demo the mobile app
  So that I can verify basic mobile functionality

  Scenario: Mobile app demo
    Given the mobile app is launched with host "https://legacy-rc.nonprod.tonicpos.com/rest" store id "1212" and auth code "1818"
    When the user performs a demo action
    Then the expected result should be displayed 