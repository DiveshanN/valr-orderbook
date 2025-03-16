package com.valr.orderbook

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.core.json.JsonObject
import java.math.BigDecimal
import java.math.RoundingMode

//Extending AbstractVerticle to allow this verticle to be deployable
class MainVerticle : AbstractVerticle() {

    //create an instance of orderbook to manage buy and sell orders
    private val orderBook = OrderBook()

    //Initialize vertx server and enable routing for HTTP request handling
    override fun start(startPromise: Promise<Void>) {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        // Enable body parsing for post requests required for order api
        router.route().handler(BodyHandler.create())

        //GET request API for retrieving the orderbook
        router.get("/api/orderbook").handler { ctx ->
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(JsonObject.mapFrom(orderBook.getOrderBook()).encodePrettily())
        }

        //POST request API for submitting orders
        router.post("/api/order").handler { ctx ->
            try {
                //Retrieve JSON body
                val body = ctx.body().asJsonObject()

                //Check that Price and Quantity values are not null and less than Zero
                val quantity = body.getDouble("quantity") ?: throw IllegalArgumentException("Quantity is required")
                if (quantity <= 0.0)
                {
                    throw IllegalArgumentException("Order quantity must be greater than zero")
                }
                val price = body.getDouble("price") ?: throw IllegalArgumentException("Price is required")
                if (price <= 0.0)
                {
                    throw IllegalArgumentException("Order price must be greater than zero")
                }
                val currencyPair = body.getString("currencyPair") ?: throw IllegalArgumentException("CurrencyPair is required")

                //Validate that the order type cannot be null or Invalid[Not BUY or SELL]
                val orderType = try
                {
                    OrderType.valueOf(body.getString("type").uppercase())
                } catch (e: Exception)
                {
                    throw IllegalArgumentException("Invalid order type: ${body.getString("type")}")
                }

                //Create an order object
                val order = Order(
                    type = orderType,
                    quantity = quantity,
                    price = price,
                    currencyPair = currencyPair
                )

                //Format the quantity and price to avoid precision issues when doing calculations
                val formattedQuantity = BigDecimal(quantity).setScale(8, RoundingMode.HALF_UP).toPlainString().replace(",", ".")
                val formattedPrice = BigDecimal(order.price).setScale(2, RoundingMode.HALF_UP).toPlainString().replace(",", ".")

                //Add the order to the orderbook
                orderBook.addOrder(order)

                //Returns the structured JSON response as per Valr example API format
                ctx.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject.mapFrom(mapOf(
                        "type" to order.type.name.lowercase(),
                        "quantity" to formattedQuantity,
                        "price" to formattedPrice,
                        "currencyPair" to order.currencyPair
                    )).encodePrettily())

            } catch (e: Exception)
            {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject().put("error", "Invalid order format: ${e.message}").encodePrettily())
            }
        }

        //GET request API to receive Trade history
        router.get("/api/tradehistory").handler { ctx ->
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(JsonObject.mapFrom(mapOf("Trade History" to orderBook.getRecentTrades())).encodePrettily())
        }

        //Start the Server to listen for incoming requests
        server.requestHandler(router).listen(8080) { http ->
            if (http.succeeded())
            {
                startPromise.complete()
                //Added Logger to confirm that the server has started
                println("HTTP server started on port 8080")
            } else
            {
                startPromise.fail(http.cause())
            }
        }
    }
}

//Main method to start the vertx application and deploy this class using vertx
fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle()) { res ->
        if (res.succeeded()) {
            println("Deployment success: ${res.result()}")
        } else {
            println("Deployment failed: ${res.cause().message}")
        }
    }
}
