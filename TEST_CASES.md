# Test Cases: Magpie API

This document provides a detailed summary of all automated test cases for the `/aggregator/quote` and `/aggregator/quote-in` endpoints. The test cases are aligned with the final implementation in the QA automation framework and reflect all discovered API behaviors and bugs.

---

## Endpoint: `/aggregator/quote`

This endpoint is for fetching quotes for single-chain swaps.

### **1. Positive Scenarios**

These tests verify that the API returns a successful response for valid requests.

| Test Case ID | Description | Key Parameters | Expected Result |
| :--- | :--- | :--- | :--- |
| **QUOTE-POS-01** | Get a valid quote on Arbitrum. | `network=arbitrum`, WETH -> USDC | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-02** | Get a valid quote on Ethereum. | `network=ethereum`, WETH -> USDC | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-03** | Use the `gasless=true` flag. | `gasless=true` | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-04** | Include optional `toAddress` and `fromAddress`. | `toAddress` and `fromAddress` provided | Status `200 OK`, `recipient` in response matches `toAddress`. |
| **QUOTE-POS-05** | Include affiliate details. | `affiliateAddress`, `affiliateFeeInPercentage` | Status `200 OK`, `affiliate` in response matches `affiliateAddress`. |
| **QUOTE-POS-06** | Enable the `RFQ` feature. | `enableRFQ=true` | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-07** | Specify a single valid `liquiditySources`. | `liquiditySources=uniswap-v3` | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-08** | Specify multiple valid `liquiditySources`. | `liquiditySources=uniswap-v3,sushi` | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-10** | Use a complex combination of all features. | `gasless`, `affiliate`, `RFQ` enabled | Status `200 OK`, valid `amountOut`. |
| **QUOTE-POS-11**| Validate the full JSON response schema. | Standard valid request | Status `200 OK` and response body matches the documented schema. |
| **QUOTE-ASSETS-01** | **Data-Driven:** Get quotes for various asset types (Stable-Stable, Major-Major, etc.) | Various asset pairs with decimal-appropriate amounts. | Status `200 OK` and a valid, non-zero `amountOut` for all pairs. |

### **2. Negative Scenarios**

These tests verify that the API returns appropriate error codes and messages for invalid, malformed, or incomplete requests.

| Test Case ID | Description | Key Parameters | Expected Result |
| :--- | :--- | :--- | :--- |
| **QUOTE-NEG-02** | Malformed `fromTokenAddress`. | `fromTokenAddress=invalidtoken` | Status `400 Bad Request`. |
| **QUOTE-NEG-03** | Malformed `toTokenAddress`. | `toTokenAddress=invalidtoken` | Status `400 Bad Request`. |
| **QUOTE-NEG-04** | Invalid `sellAmount` (non-numeric). | `sellAmount=not-a-number` | Status `400 Bad Request`. |
| **QUOTE-NEG-05** | Missing a required parameter (`network`). | `network` omitted | Status `400 Bad Request`. |
| **QUOTE-NEG-10** | Negative `slippage` value. | `slippage=-0.5` | Status `400 Bad Request`. |
| **QUOTE-NEG-12** | Empty string for required parameter (`network`).| `network=""` | Status `400 Bad Request`. |
| **QUOTE-NEG-13** | Non-existent but validly formatted token address. | `fromTokenAddress=0x123...` | Status `404 Not Found`, message: "Couldn't recognize from token". |
| **QUOTE-NEG-14**| Invalid data type for `slippage`. | `slippage="high"` | Status `400 Bad Request`. |
| **QUOTE-NEG-15**| Invalid data type for `gasless`. | `gasless="maybe"` | Status `400 Bad Request`. |
| **QUOTE-NEG-16** | Mix of valid and invalid `liquiditySources`. | `liquiditySources=sushi,invalidsource` | Status `400 Bad Request`. |
| **QUOTE-NEG-17** | `affiliateFeeInPercentage` without `affiliateAddress`. | `affiliateFeeInPercentage` provided | Status `400 Bad Request`. |
| **QUOTE-NEG-18** | `affiliateAddress` without `affiliateFeeInPercentage`. | `affiliateAddress` provided | Status `400 Bad Request`. |
| **QUOTE-NEG-19** | `slippage` is greater than 100. | `slippage=101` | Status `400 Bad Request`. |

### **3. Edge Case Scenarios & Discovered Bugs**

These tests validate the API's behavior at the boundaries of valid inputs and document any unexpected behavior.

| Test Case ID | Description | Key Parameters | Expected Result |
| :--- | :--- | :--- | :--- |
| **QUOTE-EDGE-01** | `sellAmount` is "0". | `sellAmount=0` | Status `400 Bad Request`. |
| **QUOTE-EDGE-02** | `slippage` is 0. | `slippage=0` | Status `200 OK`. |
| **QUOTE-EDGE-03** | `sellAmount` is excessively large. | `sellAmount=10000000...` | Status `400 Bad Request`. |
| **QUOTE-EDGE-04** | Identical `fromTokenAddress` and `toTokenAddress`.| WETH -> WETH | Status `200 OK` (API behavior confirmed). |
| **QUOTE-EDGE-05** | Unsupported `liquiditySources`. | `liquiditySources=invalidsource` | Status `400 Bad Request`. |
| **QUOTE-EDGE-06** | `sellAmount` is minimal (1 wei). | `sellAmount=1` | Status `400 Bad Request`. |
| **QUOTE-EDGE-08** | `fromTokenAddress` is the null address. | `fromTokenAddress=0x0...0` | Status `200 OK` (Interpreted as native currency). |
| **BUG-01** | `network` parameter is case-sensitive. | `network=ARBITRUM` | Status `500 Internal Server Error`. |
| **BUG-02** | API does not handle leading/trailing whitespace. | `network=" arbitrum "` | Status `500 Internal Server Error`. |

---

## Endpoint: `/aggregator/quote-in`

This endpoint is for fetching quotes for cross-chain swaps.

### **Negative Scenarios & Discovered Bugs**

*(Note: No positive test case for a successful cross-chain swap is currently passing, which is documented in BUG-03.)*

| Test Case ID | Description | Key Parameters | Expected Result |
| :--- | :--- | :--- | :--- |
| **QUOTE-IN-NEG-01** | Attempt a same-chain swap. | `fromNetwork=arbitrum`<br>`toNetwork=arbitrum` | Status `400 Bad Request`, message: "Couldn't recognize intermediary token". |
| **QUOTE-IN-NEG-02** | Use a token address not valid on the destination chain. | `toNetwork=optimism`<br>`toTokenAddress`=<Arbitrum_Address> | Status `404 Not Found`, message: "Couldn't recognize to token". |
| **QUOTE-IN-NEG-03** | Use an invalid `bridgeTypes` value. | `bridgeTypes=99` | Status `400 Bad Request`. |
| **QUOTE-IN-EDGE-01**| `sellAmount` is "0" for a cross-chain request. | `sellAmount=0` | Status `400 Bad Request`. |
| **BUG-03** | **(Critical Bug)** A valid cross-chain request fails to generate a quote. | Arbitrum USDC -> Polygon USDC (with all required params) | Status `400 Bad Request`, message: "Something went wrong in transaction, please try again". |