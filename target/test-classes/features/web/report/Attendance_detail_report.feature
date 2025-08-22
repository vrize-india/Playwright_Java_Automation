@regression @Report
Feature: Attendance Detail Report

  @XrayKey=TONIC-11579
  Scenario: Generate and verify Attendance Detail Report
    Given the user navigates to Apps → Attendance → Reports
    And the user clicks on the "Reports" option
    Then the page should update to display a filter for report selection
    When the user selects "Attendance Detail" as the "Select Report Type"
    And chooses "Custom Dates" under "Date Range" section
    And enters a specific period of date
    And clicks on "Filter By" section
    Then three sections titled "Employees", "Group", and "Job Title" with dropdown options should appear
    When the user selects options from each section needed for the report
    And clicks on the "Run" button
    Then the page should display the Attendance Detail Report with data corresponding to the selected filters
    And the Wage, Total Wages, and Rates displayed in the report should be accurate
    Then verify that the values shown in BOH Preview report match exactly with those in the Legacy report for the same filter and date range