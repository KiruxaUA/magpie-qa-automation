package com.magpie.qa;

import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class QuoteInAPITests extends BaseTest {

    // Common constants for networks and addresses
    private static final String ARBITRUM_NETWORK = "arbitrum";
    private static final String POLYGON_NETWORK = "polygon";
    private static final String OPTIMISM_NETWORK = "optimism";
    private static final String ARBITRUM_USDC_ADDRESS = "0xaf88d065e77c8cc2239327c5edb3a432268e5831";
    private static final String OPTIMISM_USDC_ADDRESS = "0x0b2c639c533813f4aa9d7837caf626538d76b516";
    private static final String POLYGON_USDC_ADDRESS = "0x2791Bca1f2de4661ED88A30C99A7a9449Aa84174";
    private static final String ARBITRUM_WETH_ADDRESS = "0x82af49447d8a07e3bd95bd0d56f35241523fbab1";
    private static final String OPTIMISM_WETH_ADDRESS = "0x4200000000000000000000000000000000000006";
    private static final String QUOTE_IN_ENDPOINT = "/aggregator/quote-in";
    private static final String STARGATE_BRIDGE = "1"; // Stargate bridge type
    private static final String DUMMY_WALLET_ADDRESS = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045";

    // --- Positive Test Cases ---

    @Test(description = "QUOTE-IN-POS-01: Find a working cross-chain route by probing different bridge types")
    public void testFindWorkingCrossChainRoute() {
        // This test probes for a working bridge for a common route (Arbitrum USDC -> Optimism USDC).
        // It uses the correct destination token address and iterates through bridges.
        List<String> bridgeTypesToTest = Arrays.asList("0", "1", "2", "3", "4"); // Wormhole, Stargate, Celer, CCTP, Symbiosis
        boolean success = false;

        for (String bridgeType : bridgeTypesToTest) {
            System.out.println("Probing cross-chain route with bridgeType: " + bridgeType);

            Response response = given()
                    .param("fromNetwork", ARBITRUM_NETWORK)
                    .param("toNetwork", OPTIMISM_NETWORK)
                    .param("fromTokenAddress", ARBITRUM_USDC_ADDRESS)
                    .param("toTokenAddress", OPTIMISM_USDC_ADDRESS) // Using the CORRECT address for the destination chain
                    .param("sellAmount", "10000000") // 10 USDC
                    .param("slippageIn", 0.5)
                    .param("slippageOut", 0.5)
                    .param("gasless", false)
                    .param("bridgeTypes", bridgeType)
                    .param("fromAddress", DUMMY_WALLET_ADDRESS)
                    .param("toAddress", DUMMY_WALLET_ADDRESS)
                    .when()
                    .get(QUOTE_IN_ENDPOINT);

            if (response.getStatusCode() == 200) {
                System.out.println("SUCCESS: Found a working route with bridgeType: " + bridgeType);
                response.then()
                        .log().body()
                        .body("amountOut", notNullValue())
                        .body("amountOut", not(equalTo("0")));
                success = true;
                break; // Exit the loop on the first success
            } else {
                System.out.println("INFO: bridgeType " + bridgeType + " failed with status " + response.getStatusCode() + " and message: " + response.getBody().asString());
            }
        }

        if (!success) {
            // This assertion now correctly represents a critical bug if it fails.
            Assert.fail("Critical Bug: Could not find any working cross-chain bridge route for a valid USDC -> USDC request.");
        }
    }

    // --- Negative Test Cases ---

    @Test(description = "QUOTE-IN-NEG-01: Verify that same-chain swaps are correctly rejected")
    public void testSameChainQuoteIsInvalid() {
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "100000000000000000") // 0.1 WETH
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("Couldn't recognize intermediary token"));
    }

    @Test(description = "QUOTE-IN-NEG-02: Verify requests with an unrecognized 'to token' are rejected")
    public void testCrossChainQuoteFailsWithUnrecognizedToken() {
        // This test verifies that using a token address on the wrong destination chain is correctly rejected.
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", OPTIMISM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS) // Using Arbitrum's USDC on Optimism chain
                .param("sellAmount", "10000000") // 10 USDC
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .param("bridgeTypes", STARGATE_BRIDGE)
                .param("fromAddress", DUMMY_WALLET_ADDRESS)
                .param("toAddress", DUMMY_WALLET_ADDRESS)
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(404) // Correctly asserting the 404 status
                .body("message", equalTo("Couldn't recognize to token"))
                .body("code", equalTo(2003));
    }

    @Test(description = "QUOTE-IN-NEG-03: Invalid 'bridgeType' parameter")
    public void testGetQuoteInInvalidBridgeType() {
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", POLYGON_NETWORK)
                .param("fromTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("toTokenAddress", POLYGON_USDC_ADDRESS)
                .param("sellAmount", "10000000")
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .param("bridgeTypes", 99) // Invalid bridge type
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-IN-NEG-04: Verify same-chain swaps are correctly rejected")
    public void testGetQuoteInSameChainIsInvalid() {
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "100000000000000000")
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400)
                .body("message", equalTo("Couldn't recognize intermediary token"));
    }

    // --- Edge Cases ---

    @Test(description = "QUOTE-IN-EDGE-01: Zero 'sellAmount'")
    public void testGetQuoteInZeroAmount() {
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "0")
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    // --- Test Documenting API Issue ---

    @Test(description = "BUG-03 / QUOTE-IN-FAIL-01: Verify that a valid cross-chain request fails with a generic transaction error")
    public void testCrossChainQuoteFailsWithGenericError() {
        // This test uses the Arbitrum -> Polygon route, which is known to fail with a 2042 error.
        given()
                .param("fromNetwork", ARBITRUM_NETWORK)
                .param("toNetwork", POLYGON_NETWORK)
                .param("fromTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("toTokenAddress", POLYGON_USDC_ADDRESS)
                .param("sellAmount", "10000000") // 10 USDC
                .param("slippageIn", 0.5)
                .param("slippageOut", 0.5)
                .param("gasless", false)
                .param("bridgeTypes", STARGATE_BRIDGE)
                .param("fromAddress", DUMMY_WALLET_ADDRESS)
                .param("toAddress", DUMMY_WALLET_ADDRESS)
                .when()
                .get(QUOTE_IN_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400)
                .body("message", equalTo("Something went wrong in transaction, please try again"))
                .body("code", equalTo(2042));
    }
}