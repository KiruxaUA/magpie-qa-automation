# Magpie API Testing Framework

This project contains an automated API testing framework for the Magpie APIs, as documented at [https://api.magpiefi.xyz/swagger](https://api.magpiefi.xyz/swagger).

## Technologies Used

*   **Java:** Programming Language
*   **RestAssured:** For API automation
*   **TestNG:** Testing framework
*   **Maven:** Build and dependency management tool

## Prerequisites

*   Java Development Kit (JDK) 8 or higher installed and configured.
*   Apache Maven installed and configured.

## How to Set Up the Environment

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd magpie-qa-assignment
    ```
3.  **Install dependencies:**
    ```bash
    mvn clean install
    ```

## How to Run the Tests

To execute the automated tests, run the following Maven command from the project root directory:

```bash
mvn test
```

## How to Generate and View the Test Report

To generate and view the HTML test report, run the following Maven commands:

```bash
mvn surefire-report:report-only
mvn site
```

After the commands have been executed, the test report will be available at `target/site/surefire-report.html`.

## How to Interpret the Results

The test report will provide a summary of the test run, including:

*   Number of tests passed, failed, and skipped.
*   Detailed information about any failures or errors.
*   Execution time.
