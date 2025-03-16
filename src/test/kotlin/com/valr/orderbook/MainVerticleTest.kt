package com.valr.orderbook

import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class MainVerticleTest {

    private lateinit var vertx: Vertx
    private lateinit var client: WebClient

    @BeforeEach
    fun setup(vertx: Vertx, testContext: VertxTestContext) {
        this.vertx = vertx
        vertx.deployVerticle(MainVerticle(), testContext.succeeding { _ -> testContext.completeNow() })

        // Initialize HTTP Client
        client = WebClient.create(vertx, WebClientOptions().setDefaultPort(8080))
    }

    @AfterEach
    fun tearDown(testContext: VertxTestContext) {
        vertx.close(testContext.succeeding { testContext.completeNow() })
    }

    @Test //Test Orderbook API
    fun testGetOrderBook(testContext: VertxTestContext) {
        client.get("/api/orderbook").send { response ->
            testContext.verify {
                Assertions.assertEquals(200, response.result().statusCode())
                Assertions.assertNotNull(response.result().bodyAsJsonObject())
                testContext.completeNow()
            }
        }
    }

    @Test //Test Submitting a Buy order with all valid parameters
    fun testSubmitValidBuyOrder(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "BUY")
            .put("price", 154000)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(201, response.result().statusCode())
                    Assertions.assertNotNull(response.result().bodyAsJsonObject())
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting a Sell order with all valid parameters
    fun testSubmitValidSellOrder(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("price", 155000)
            .put("quantity", 1.0)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(201, response.result().statusCode())
                    Assertions.assertNotNull(response.result().bodyAsJsonObject())
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with a negative quantity value
    fun testSubmitInvalidOrderNegativeQuantity(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("price", 153000)
            .put("quantity", -0.5)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with a negative price value
    fun testSubmitInvalidOrderNegativePrice(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("price", -153000)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with the price parameter missing
    fun testSubmitInvalidOrderMissingPrice(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("quantity", 1.0)
            .put("currencyPair", "BTCZAR") // Missing price

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with the quantity parameter missing
    fun testSubmitInvalidOrderMissingQuantity(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("price", 153000)
            .put("currencyPair", "BTCZAR") // Missing price

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with the currency pair parameter missing
    fun testSubmitInvalidOrderMissingCurrencyPair(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "SELL")
            .put("price", 153000)
            .put("quantity", 1.0)

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with an Invalid type parameter
    fun testSubmitInvalidOrderInvalidType(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "INVALID")
            .put("price", 154000)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test Submitting an order with the type parameter missing
    fun testSubmitInvalidOrderMissingType(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("price", 153000)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        client.post("/api/order")
            .sendJson(order) { response ->
                testContext.verify {
                    Assertions.assertEquals(400, response.result().statusCode())
                    Assertions.assertTrue(response.result().bodyAsJsonObject().containsKey("error"))
                    testContext.completeNow()
                }
            }
    }

    @Test //Test get trade history API
    fun testGetTradeHistory(testContext: VertxTestContext) {
        client.get("/api/tradehistory").send { response ->
            testContext.verify {
                Assertions.assertEquals(200, response.result().statusCode())
                Assertions.assertNotNull(response.result().bodyAsJsonObject())
                testContext.completeNow()
            }
        }
    }

    @Test //Test exact order matching removes the orders in the order book and trade history gets populated
    fun testExactMatchOrderExecution(testContext: VertxTestContext) {
        val buyOrder = JsonObject()
            .put("type", "BUY")
            .put("price", 155000)
            .put("quantity", 1.0)
            .put("currencyPair", "BTCZAR")

        val sellOrder = JsonObject()
            .put("type", "SELL")
            .put("price", 155000)
            .put("quantity", 1.0)
            .put("currencyPair", "BTCZAR")

        //Place BUY order and verify it goes through
        client.post("/api/order").sendJson(buyOrder) { buyResponse ->
            testContext.verify {
                Assertions.assertEquals(201, buyResponse.result().statusCode())

                //Place SELL order and verify it goes through
                client.post("/api/order").sendJson(sellOrder) { sellResponse ->
                    testContext.verify {
                        Assertions.assertEquals(201, sellResponse.result().statusCode())

                        //Fetch order book to verify it is empty
                        client.get("/api/orderbook").send { orderBookResponse ->
                            testContext.verify {
                                val orderBook = orderBookResponse.result().bodyAsJsonObject()
                                Assertions.assertTrue(orderBook.getJsonArray("Asks").isEmpty)
                                Assertions.assertTrue(orderBook.getJsonArray("Bids").isEmpty)

                                //Fetch trade history to confirm trade execution
                                client.get("/api/tradehistory").send { tradeResponse ->
                                    testContext.verify {
                                        val tradeHistory = tradeResponse.result().bodyAsJsonObject()
                                        Assertions.assertTrue(tradeHistory.getJsonArray("Trade History").size() > 0)
                                        testContext.completeNow()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test //Test that the orderbook handles partial order completion and dynamically updates
    fun testPartialOrderExecution(testContext: VertxTestContext) {
        val buyOrder = JsonObject()
            .put("type", "BUY")
            .put("price", 155000)
            .put("quantity", 2.0)
            .put("currencyPair", "BTCZAR")

        val sellOrder = JsonObject()
            .put("type", "SELL")
            .put("price", 155000)
            .put("quantity", 1.0)
            .put("currencyPair", "BTCZAR")

        //Send BUY order
        client.post("/api/order").sendJson(buyOrder) { buyResponse ->
            testContext.verify {
                Assertions.assertEquals(201, buyResponse.result().statusCode())
                //Send SELL order
                client.post("/api/order").sendJson(sellOrder) { sellResponse ->
                    testContext.verify {
                        Assertions.assertEquals(201, sellResponse.result().statusCode())

                        //Fetch order book to verify that the quantity is now updated to 1 BTC
                        client.get("/api/orderbook").send { orderBookResponse ->
                            testContext.verify {
                                val orderBook = orderBookResponse.result().bodyAsJsonObject()
                                val bids = orderBook.getJsonArray("Bids")

                                Assertions.assertFalse(bids.isEmpty) //Ensure bids are populated
                                Assertions.assertEquals(1.0, bids.getJsonObject(0).getString("quantity").toDouble()) //verify the new quantity value

                                //Fetch trade history to verify that the executed trade is populated in the trade history
                                client.get("/api/tradehistory").send { tradeResponse ->
                                    testContext.verify {
                                        val tradeHistory = tradeResponse.result().bodyAsJsonObject()
                                        Assertions.assertTrue(tradeHistory.getJsonArray("Trade History").size() > 0)
                                        testContext.completeNow()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test //Test to verify that the submit order api rounds up high precision values to 8 decimal places
    fun testHighPrecisionOrder(testContext: VertxTestContext) {
        val order = JsonObject()
            .put("type", "BUY")
            .put("price", 154000)
            .put("quantity", 0.123456789) //Adding a quantity parameter with a high precision value > 8 decimal points
            .put("currencyPair", "BTCZAR")

        client.post("/api/order").sendJson(order) { response ->
            testContext.verify {
                Assertions.assertEquals(201, response.result().statusCode())

                val responseBody = response.result().bodyAsJsonObject()
                val formattedQuantity = responseBody.getString("quantity").toDouble()

                //Verify that it rounds to 8 decimal places
                Assertions.assertEquals(0.12345679, formattedQuantity)
                testContext.completeNow()
            }
        }
    }

}
