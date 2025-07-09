package com.magpie.qa;

import io.restassured.filter.log.LogDetail;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class QuoteAPITests extends BaseTest {

    // Common constants for addresses and values
    private static final String ARBITRUM_NETWORK = "arbitrum";
    private static final String ETHEREUM_NETWORK = "ethereum";
    private static final String METIS_NETWORK = "metis";
    private static final String ARBITRUM_WETH_ADDRESS = "0x82af49447d8a07e3bd95bd0d56f35241523fbab1";
    private static final String ARBITRUM_USDC_ADDRESS = "0xaf88d065e77c8cc2239327c5edb3a432268e5831";
    private static final String ETHEREUM_WETH_ADDRESS = "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2";
    private static final String ETHEREUM_USDC_ADDRESS = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48";
    private static final String ARB_TOKEN_ADDRESS = "0x912CE59144191C1204E64559FE8253a0e49E6548";
    private static final String ARBITRUM_WBTC_ADDRESS = "0x2f2a2543B76A4166549F7aaB2e75Bef0aefC5B0f";
    private static final String ARBITRUM_USDT_ADDRESS = "0xFd086bC7CD5C481DCC9C85ebE478A1C0b69FCbb9";
    private static final String DEFAULT_SELL_AMOUNT = "1000000000000000000"; // 1 WETH
    private static final String QUOTE_ENDPOINT = "/aggregator/quote";
    private static final String DUMMY_WALLET_ADDRESS = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045";
    private static final String NON_EXISTENT_TOKEN_ADDRESS = "0x1234567890123456789012345678901234567890";
    private static final String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";


    // --- Positive Test Cases ---

    @Test(description = "QUOTE-POS-01: Get a valid quote on Arbitrum")
    public void testGetQuoteValidArbitrum() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-02: Get a valid quote on Ethereum")
    public void testGetQuoteValidEthereum() {
        given()
                .param("network", ETHEREUM_NETWORK)
                .param("fromTokenAddress", ETHEREUM_WETH_ADDRESS)
                .param("toTokenAddress", ETHEREUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-03: Get a quote with the 'gasless' feature enabled")
    public void testGetQuoteGasless() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", true)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-04: Get a quote including optional toAddress and fromAddress")
    public void testGetQuoteWithOptionalAddresses() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("toAddress", DUMMY_WALLET_ADDRESS)
                .param("fromAddress", DUMMY_WALLET_ADDRESS)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("typedData.message.recipient", equalToIgnoringCase(DUMMY_WALLET_ADDRESS));
    }

    @Test(description = "QUOTE-POS-05: Get a quote with affiliate details")
    public void testGetQuoteWithAffiliate() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("affiliateAddress", DUMMY_WALLET_ADDRESS)
                .param("affiliateFeeInPercentage", 0.01)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("typedData.message.affiliate", equalToIgnoringCase(DUMMY_WALLET_ADDRESS));
    }

    @Test(description = "QUOTE-POS-06: Get a quote with RFQ enabled")
    public void testGetQuoteWithRfqEnabled() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("enableRFQ", true)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-07: Get a quote from a specific liquidity source")
    public void testGetQuoteWithSpecificLiquiditySource() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("liquiditySources", "uniswap-v3")
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-08: Get a quote using multiple valid liquidity sources")
    public void testGetQuoteWithMultipleLiquiditySources() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .queryParam("liquiditySources", "uniswap-v3", "sushi") // Pass as multi-value parameter
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-10: Test complex combination of optional parameters")
    public void testGetQuoteWithComplexCombination() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", true)
                .param("affiliateAddress", DUMMY_WALLET_ADDRESS)
                .param("affiliateFeeInPercentage", 0.01)
                .param("enableRFQ", true)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-POS-11: Verify full response schema for a successful quote")
    public void testGetQuoteAndValidateFullSchema() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("id", isA(String.class))
                .body("amountOut", isA(String.class))
                .body("targetAddress", isA(String.class))
                .body("fees", isA(java.util.List.class))
                .body("resourceEstimate.gasLimit", isA(String.class))
                .body("typedData.types", notNullValue())
                .body("typedData.domain.name", isA(String.class))
                .body("typedData.domain.version", isA(String.class))
                .body("typedData.message.router", isA(String.class));
    }

    // --- Negative Test Cases ---

    @Test(description = "QUOTE-NEG-01: Invalid 'network' parameter")
    public void testGetQuoteInvalidNetwork() {
        // The API currently returns 500, but 400 would be more appropriate. Testing for current behavior.
        given()
                .param("network", "invalidchain")
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(500);
    }

    @Test(description = "QUOTE-NEG-02: Invalid 'fromTokenAddress' parameter")
    public void testGetQuoteInvalidFromTokenAddress() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", "invalidtokenaddress")
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-03: Invalid 'toTokenAddress' parameter")
    public void testGetQuoteInvalidToTokenAddress() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", "invalidtokenaddress")
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-04: Invalid 'sellAmount' (non-numeric string)")
    public void testGetQuoteInvalidAmountString() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "not-a-number")
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-05: Missing 'network' parameter")
    public void testGetQuoteMissingNetwork() {
        given()
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-10: Invalid 'slippage' (negative value)")
    public void testGetQuoteNegativeSlippage() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", -0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-12: Empty string for required 'network' parameter")
    public void testGetQuoteWithEmptyNetwork() {
        given()
                .param("network", "")
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-13 (Corrected): Syntactically valid but non-existent 'from' token address")
    public void testGetQuoteWithNonExistentToken() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", NON_EXISTENT_TOKEN_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(404)
                .body("message", equalTo("Couldn't recognize from token"));
    }

    @Test(description = "QUOTE-NEG-14: Invalid data type for 'slippage'")
    public void testGetQuoteWithInvalidSlippageType() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", "high") // String instead of number
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-15: Invalid data type for 'gasless'")
    public void testGetQuoteWithInvalidGaslessType() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", "maybe") // String instead of boolean
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-16: Mix of valid and invalid liquidity sources")
    public void testGetQuoteWithMixedLiquiditySources() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .queryParam("liquiditySources", "uniswap-v3", "invalidsource")
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400); // Should fail if any source is invalid
    }

    @Test(description = "QUOTE-NEG-17: Affiliate fee provided without an affiliate address")
    public void testGetQuoteFeeWithoutAddress() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("affiliateFeeInPercentage", 0.01)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-18: Affiliate address provided without an affiliate fee")
    public void testGetQuoteAddressWithoutFee() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("affiliateAddress", DUMMY_WALLET_ADDRESS)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-NEG-19: Slippage value is above the maximum allowed (100%)")
    public void testGetQuoteWithSlippageAboveMax() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 101)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    // --- Edge Case Test Cases ---

    @Test(description = "QUOTE-EDGE-01: Invalid 'sellAmount' (zero)")
    public void testGetQuoteZeroAmount() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "0")
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-EDGE-02: Zero slippage")
    public void testGetQuoteZeroSlippage() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-EDGE-03: Very large 'sellAmount'")
    public void testGetQuoteLargeAmount() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "100000000000000000000000000") // 1,000,000 WETH
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-EDGE-04: Identical from and to token addresses")
    public void testGetQuoteIdenticalTokens() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200);
    }

    @Test(description = "QUOTE-EDGE-05: Unsupported liquidity source")
    public void testGetQuoteUnsupportedLiquiditySource() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .param("liquiditySources", "invalidsource")
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-EDGE-06 (Corrected): Minimal 'sellAmount' (1 wei) is too low")
    public void testGetQuoteWithMinimalAmount() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", "1")
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-EDGE-07 (Corrected): High slippage (100) is invalid")
    public void testGetQuoteWithHighSlippage() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 100)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400);
    }

    @Test(description = "QUOTE-EDGE-08 (Corrected): Using the null address for a token should resolve to native currency")
    public void testGetQuoteWithNullAddressAsNativeCurrency() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", NULL_ADDRESS) // Represents native ETH on Arbitrum
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    @Test(description = "QUOTE-EDGE-09 (Corrected): Token pair with a non-existent token address")
    public void testGetQuoteWithNoLiquidityPair() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", NON_EXISTENT_TOKEN_ADDRESS)
                .param("toTokenAddress", NULL_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(404)
                .body("message", equalTo("Couldn't recognize from token"));
    }

    @Test(description = "QUOTE-EDGE-10 (Corrected): Test a chain where the token pair has no liquidity")
    public void testGetQuoteOnChainWithNoLiquidityForPair() {
        given()
                .param("network", METIS_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(404)
                .body("message", equalTo("Couldn't recognize from token"));
    }

    @Test(description = "QUOTE-EDGE-11 (Corrected): Provide duplicate liquidity sources")
    public void testGetQuoteWithDuplicateLiquiditySources() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .queryParam("liquiditySources", "sushi", "sushi")
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(400)
                .body("message", equalTo("Wrong parameters: All liquiditySources's elements must be unique"));
    }

    @Test(description = "QUOTE-EDGE-12: Provide an empty array for liquiditySources")
    public void testGetQuoteWithEmptyLiquiditySourcesArray() {
        given()
                .param("network", ARBITRUM_NETWORK)
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .queryParam("liquiditySources", "")
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue());
    }

    // --- Tests Documenting API Bugs ---

    @Test(description = "BUG-01 / QUOTE-POS-09: API does not handle case-insensitive network parameter")
    public void testGetQuoteWithCaseInsensitiveNetwork() {
        given()
                .param("network", "ARBITRUM") // Uppercase
                .param("fromTokenAddress", ARBITRUM_WETH_ADDRESS)
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(500); // Asserting actual (buggy) behavior
    }

    @Test(description = "BUG-02 / QUOTE-POS-12: API does not handle leading/trailing whitespace")
    public void testGetQuoteWithWhitespaceInParams() {
        given()
                .param("network", " " + ARBITRUM_NETWORK + " ")
                .param("fromTokenAddress", " " + ARBITRUM_WETH_ADDRESS + " ")
                .param("toTokenAddress", ARBITRUM_USDC_ADDRESS)
                .param("sellAmount", DEFAULT_SELL_AMOUNT)
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(500); // Asserting actual (buggy) behavior
    }

    /**
     * DataProvider for supplying various asset pairs to test quote generation.
     * Each entry includes a sellAmount appropriate for the 'from' token's decimal precision.
     */
    @DataProvider(name = "assetPairs")
    public Object[][] assetPairs() {
        return new Object[][] {
                // Test Case: Sell 100 USDC (6 decimals) for USDT
                { "Stablecoin to Stablecoin (USDC -> USDT)", ARBITRUM_NETWORK, ARBITRUM_USDC_ADDRESS, ARBITRUM_USDT_ADDRESS, "100000000" },

                // Test Case: Sell 0.01 WBTC (8 decimals) for WETH
                { "Major Asset to Major Asset (WBTC -> WETH)", ARBITRUM_NETWORK, ARBITRUM_WBTC_ADDRESS, ARBITRUM_WETH_ADDRESS, "1000000" },

                // Test Case: Sell 100 ARB (18 decimals) for WETH
                { "Protocol Token to Major Asset (ARB -> WETH)", ARBITRUM_NETWORK, ARB_TOKEN_ADDRESS, ARBITRUM_WETH_ADDRESS, "100000000000000000000" },

                // Test Case: Sell 100 USDC (6 decimals) for WETH
                { "Stablecoin to Major Asset (USDC -> WETH)", ARBITRUM_NETWORK, ARBITRUM_USDC_ADDRESS, ARBITRUM_WETH_ADDRESS, "100000000" },

                // Test Case: Sell 0.1 ETH (18 decimals) for ARB
                { "Native Currency to Protocol Token (ETH -> ARB)", ARBITRUM_NETWORK, NULL_ADDRESS, ARB_TOKEN_ADDRESS, "100000000000000000" }
        };
    }

    /**
     * This test uses a DataProvider to validate that the API can generate quotes for a variety
     * of asset types with appropriate sell amounts, ensuring the aggregator's routing is robust.
     *
     * @param testDescription A human-readable description of the test case.
     * @param network The blockchain network.
     * @param fromToken The address of the token to sell.
     * @param toToken The address of the token to buy.
     * @param sellAmount The amount of the 'from' token to sell, respecting its decimal precision.
     */
    @Test(dataProvider = "assetPairs", description = "QUOTE-ASSETS-01: Validate quote generation for various asset pairs")
    public void testQuoteGenerationForVariousAssets(String testDescription, String network, String fromToken, String toToken, String sellAmount) {
        given()
                .param("network", network)
                .param("fromTokenAddress", fromToken)
                .param("toTokenAddress", toToken)
                .param("sellAmount", sellAmount) // Using the sellAmount from the DataProvider
                .param("slippage", 0.5)
                .param("gasless", false)
                .when()
                .get(QUOTE_ENDPOINT)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(200)
                .body("amountOut", notNullValue())
                .body("amountOut", not(equalTo("0")));
    }
}