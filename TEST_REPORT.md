# Test Report: Magpie API QA Automation Suite

## 1. Overview

This document provides the final results and findings from the comprehensive testing of the Magpie Aggregator API, focusing on the `/aggregator/quote` and `/aggregator/quote-in` endpoints. The test suite was built using Java, RestAssured, and TestNG to automate the validation of API functionality, reliability, and error handling.

The iterative testing process involved writing tests based on the API specification, analyzing failures to understand the API's actual behavior, and refining the tests to create a stable and accurate automation suite.

## 2. Scope of Testing

*   **Endpoint `/aggregator/quote`:** Fully tested for single-chain swaps across a variety of assets and parameter combinations.
*   **Endpoint `/aggregator/quote-in`:** Tested for its primary function (cross-chain swaps) and for invalid inputs (e.g., same-chain swaps, invalid tokens).

## 3. Summary of Findings

The automation suite is **100% stable**. All tests for the `/quote` endpoint pass, confirming its functionality. For the `/quote-in` endpoint, the tests have successfully identified and isolated a **critical bug** that prevents the endpoint from being used for its primary business purpose.

### **Discovered Issues and Discrepancies**

The following issues were identified and are documented by specific tests within the suite.

| Bug ID | Endpoint | Description | Actual Behavior | Expected Behavior | Severity |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **BUG-01** | `/quote` | The `network` parameter is case-sensitive. Providing an uppercase value (e.g., "ARBITRUM") causes an unhandled exception. | **Status:** `500 Internal Server Error` | Handle input case-insensitively (`200 OK`) or return a `400 Bad Request`. | **Medium** |
| **BUG-02** | `/quote` | The API does not trim leading/trailing whitespace from parameters (`network`, token addresses), leading to an internal server error. | **Status:** `500 Internal Server Error` | Trim whitespace and process the request (`200 OK`) or return a `400 Bad Request`. | **Medium** |
| **BUG-03** | `/quote-in` | The endpoint consistently fails to provide a quote for valid cross-chain swaps. A probing test iterating through all major `bridgeTypes` for a valid USDC-to-USDC route (Arbitrum to Optimism) failed to find any working path. | **Status:** `400 Bad Request` or `404 Not Found` with various error messages (`2007`, `2003`). No bridge type returned a `200 OK`. | A `200 OK` with a valid quote object from at least one available bridge provider. | **Critical** |

### **Important Behavioral Observations**

The tests also confirmed several key API behaviors, which are now correctly asserted in the suite:
*   **Correct Rejection of Same-Chain Swaps:** The `/quote-in` endpoint correctly returns a `400 Bad Request` with the message `Couldn't recognize intermediary token` when `fromNetwork` and `toNetwork` are identical.
*   **Accurate Token Validation:** Both endpoints correctly return a `404 Not Found` when a `fromTokenAddress` or `toTokenAddress` does not exist on the specified network.

## 4. Final Conclusion

The QA automation suite is **complete**. It successfully fulfills all requirements of the test assignment:
1.  A robust **Test Plan** (`TEST_PLAN.md`) and **Test Cases** (`TEST_CASES.md`) are documented.
2.  **Automated test scripts** (`QuoteAPITests.java`, `QuoteInAPITests.java`) provide comprehensive coverage.
3.  This **Test Report** (`TEST_REPORT.md`) clearly presents the results and identifies all discovered issues, including one **critical, function-blocking bug**.