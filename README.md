# Playwright Java Page Object Model Framework

This project is a Test Automation Framework built with Playwright Java and follows the Page Object Model design pattern.


## Author 
Gaurav Purwar
v0.1
Date: 23 May 2025

## Prerequisites

- Java JDK 8 or higher
- Maven
- Allure (for reporting)

## Setup

1. Clone the repository
2. Install dependencies:

   ```bash
   mvn clean install
   ```
3. Install Allure command line tool (if not already installed):
   ```bash
   brew install allure  # For macOS
   ```

## Running Tests

### Run all tests

```bash
mvn clean test
```

### Run a specific test class

```bash
mvn clean test -Dtest=com.qa.tonic.tests.TerminalManagementTest
```

### Run a specific test method

```bash
mvn clean test com.qa.tonic.tests.TerminalManagementTest#loginWithValidCredentials
```

## Run a specific test method with report
```bash
mvn clean test -Dtest=com.tonic.tests.web.LoginTest#loginWithValidCredentials
allure serve target/allure-results
```

### Run tests with a specific browser

The browser can be specified in the TestNG XML file. Edit the `src/test/resources/testrunners/testng_regressions.xml` file:

```xml
<parameter name="browser" value="chromium" />  <!-- Options: chromium, firefox, webkit -->
```

## Generating and Viewing Reports

### Extent Reports

After test execution, Extent Reports can be found in the `test-output/extent` directory.

### Allure Reports

if you have the Allure command-line tool installed:
```bash
allure serve target/allure-results
```

## Project Structure

```
├── src
│   ├── main/java/com/qa/tonic
│   │   ├── factory      # Browser factory and utilities
│   │   ├── pages        # Page objects
│   │   └── utils        # Utility classes
│   └── test/java/com/qa/tonic
│       ├── base         # Test base classes
│       ├── tests        # Test classes
│       └── listeners    # TestNG listeners
├── src/test/resources
│   ├── config           # Configuration properties
│   └── testrunners      # TestNG XML files
└── target               # Generated reports and classes
```

## Debugging Tests

To debug a test:
1. Set breakpoints in your test methods
2. Right-click the test method or class
3. Select "Debug 'TestMethodName'" option

## Adding New Tests

1. Create page objects in the `pages` package
2. Create test classes in the `tests` package
3. Add test methods with appropriate annotations

## CI/CD Integration

This framework can be integrated with CI/CD tools like Jenkins, GitHub Actions, etc. 

## Tips
https://docs.cursor.com/welcome
https://docs.cursor.com/cmdk/overview
https://docs.cursor.com/chat/overview

