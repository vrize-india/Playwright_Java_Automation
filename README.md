# Tonic AI Based Playwright Cucumber Automation Framework

This project is a Test Automation Framework built with **Playwright Java**, **Serenity**, and **TestNG**, following the Page Object Model (POM) design pattern. It supports Web, Mobile, and API testing, and is designed for extensibility and maintainability.

---

## Author

- Gaurav Purwar
- v1.0
- Updated on: 1 June 2025

---

## Prerequisites

- Java JDK 11 or higher
- Maven
- Allure (for reporting)
- Cucumber
- Node.js (for Playwright browser installation, if not already present)

---

## Setup

1. **Clone the repository**
2. **Install dependencies:**
   ```bash
   mvn clean install
   ```
3. **Install Allure command line tool (if not already installed):**
   ```bash
   brew install allure  # For macOS
   ```
4. **Install Playwright Browsers (if not already installed):**
   ```bash
   mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
   ```

---

## Running Tests


### Run a specific Cucumber feature via TestRunner

```bash
mvn clean test -Dcucumber.filter.tags="@regression" -Ddataproviderthreadcount=2
mvn clean test -Dtest=com.vrize.runners.TestRunner
mvn clean test -Dcucumber.filter.tags="@TONIC7343
```

### Run a specific TestNG test class

```bash
mvn clean test -Dtest=com.vrize.healthCheckTestngTests.web.LoginTest
```

### Run with a specific TestNG XML suite

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testrunners/testng.xml
```

---

## Running with a Visible Browser

By default, Playwright runs in headless mode.  
To see the browser UI, set `setHeadless(false)` in `src/test/java/com/vrize/hooks/Hooks.java`:

```java
browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
```

---

## Generating and Viewing Reports

### Extent Reports

After test execution, Extent Reports can be found in the `build/` directory.

### Allure Reports

If you have the Allure command-line tool installed:
```bash
allure serve target/allure-results
```

---

## Project Structure

```
src
├── main
│   └── java
│       └── com
│           └── vrize
│               ├── annotations/      # Custom annotations
│               ├── constants/        # Framework and app constants
│               ├── driver/           # WebDriver and Playwright driver management
│               ├── enums/            # Enum types
│               ├── exceptions/       # Custom exceptions
│               ├── factory/          # Browser and wait factories
│               ├── healthCheck/      # Health check utilities/tests
│               ├── listeners/        # TestNG/Allure/Extent listeners
│               └── utils/            # Utility classes
├── test
│   ├── java
│   │   └── com
│   │       └── vrize
│   │           ├── actions/                # Custom action classes
│   │           ├── hooks/                  # Cucumber hooks (Playwright setup/teardown)
│   │           ├── pageObjects/            # Page Object Model classes
│   │           │   ├── web/*
│   │           │   ├── mobile/
│   │           │   └── api/
│   │           ├── runners/                # TestRunner classes (Cucumber/TestNG)
│   │           ├── stepDefinitions/*       # Step definitions for Cucumber features
│   │           └── healthCheckTestngTests/ # TestNG-based tests (web, mobile, api)
│   └── resources
│       ├── features/
│       │   ├── web/*
│       │   ├── mobile/
│       │   └── api/
│       └── testrunners/                    # TestNG XML suite files
build/                                      # Extent and other reports
target/                                     # Maven build output
pom.xml
README.md
```

---

## Adding New Tests

1. **Create page objects** in the appropriate `pageObjects` subpackage.
2. **Create step definitions** in `stepDefinitions` for Cucumber features.
3. **Add feature files** in `src/test/resources/features/`.
4. **Add or update TestNG test classes** in `healthCheckTestngTests` for direct TestNG tests.

---

## Debugging Tests

- Set breakpoints in your test or step definition classes.
- Use your IDE's debug feature to run the test in debug mode.

---

## CI/CD Integration

This framework can be integrated with CI/CD tools like Jenkins, GitHub Actions, etc.  
Use the provided `Jenkinsfile` as a starting point.

---

## Tips

- To run browsers in headed mode for debugging, set `setHeadless(false)` in `Hooks.java`.
- For more info, see:
  - [Serenity BDD Documentation](https://serenity-bdd.github.io/docs/)
  - [Playwright Java Docs](https://playwright.dev/java/)
  - [Allure Reporting](https://docs.qameta.io/allure/)

