# Playwright Java Page Object Model Framework

This project is a Test Automation Framework built with Playwright Java with TestNG and follows the Page Object Model design pattern.


## Author 
- Gaurav Purwar
- v0.1
- Date: 23 May 2025

## Capabilities
- Web Automation
- API Automation
- Mobile Automation (appium)
- Executable from Command line to implement CI/CD
- Allure (for reporting)
- TestNG
- Extent Report
- JIRA Integration
- Parallel Execution
- Multiple Browser

## Prerequisites
- Java JDK 8 or higher
- Maven
- Allure (for reporting)
- TestNG
- Extent Report

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

### Run Mobile tests


```bash
mvn clean test -Dtest=com.tonic.tests.mobile.AccountTest -Dplatform=mobile -Ddevice=android -Drunmode=local
```

### Run a specific test class with test methods

```bash
mvn clean test -Dtest=com.qa.tonic.tests.web.LoginTest
```

## Run a specified tests in testng.xml
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Run tests with a specific browser

The browser can be specified in the TestNG XML file. Edit the `src/test/resources/testrunners/testng.xml` file:

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
│   ├── main
│   │   └── java/com/tonic
│   │       ├── api           # API utilities and data
│   │       ├── annotations   # Custom annotations
│   │       ├── constants     # Framework constants
│   │       ├── driver        # Driver management (Driver, DriverManager)
│   │       ├── enums         # Enum types
│   │       ├── exceptions    # Custom exceptions
│   │       ├── factory       # Browser factory and related utilities
│   │       ├── healthCheck   # Health check utilities
│   │       ├── listeners     # TestNG listeners
│   │       └── utils         # Utility classes (logging, reporting, etc.)
│   └── test
│       └── java/com/tonic
│           ├── actions       # User actions/flows
│           ├── pages
│           │   ├── web       # Web page objects (POM)
│           │   └── app       # Mobile app page objects (if any)
│           └── tests
│               ├── web       # Web test classes
│               ├── mobile    # Mobile test classes
│               └── api
│                   ├── GET   # API GET tests
│                   ├── POST  # API POST tests
│                   ├── PUT   # API PUT tests
│                   └── DELETE# API DELETE tests
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
- https://docs.cursor.com/welcome
- https://docs.cursor.com/cmdk/overview
- https://docs.cursor.com/chat/overview

