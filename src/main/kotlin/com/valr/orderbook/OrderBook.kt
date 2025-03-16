package com.valr.orderbook

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.PriorityQueue
import java.time.Instant
import java.util.UUID;

class OrderBook {
    private val buyOrders = PriorityQueue<Order>(compareByDescending { it.price }) //Sort Buy orders, Highest Price first
    private val sellOrders = PriorityQueue<Order>(compareBy { it.price }) //Sort Sell orders, Lowest Price first
    private val trades = mutableListOf<Trade>() //Store trades that are executed

    //Method that places orders in the correct queues based on order type
    fun addOrder(order: Order) {
        if (order.type == OrderType.BUY)
        {
            buyOrders.offer(order)
        } else
        {
            sellOrders.offer(order)
        }
        matchOrders()//matches orders immediately after they are placed
    }

    //Method to retrieve orderbook
    fun getOrderBook(): Map<String, List<Map<String, Any>>> {
        val groupedBuyOrders = buyOrders.groupBy { it.price }
            .map { (price, orders) ->
                mapOf(
                    "side" to "buy",
                    "quantity" to formatDecimal(orders.sumOf { it.quantity }, 8), //calculates total quantity on this price level for buy orders
                    "price" to formatDecimal(price, 2),
                    "currencyPair" to orders.first().currencyPair,
                    "orderCount" to orders.size //Order count to show aggregating quantities on the same price levels for buy orders
                )
            }

        val groupedSellOrders = sellOrders.groupBy { it.price }
            .map { (price, orders) ->
                mapOf(
                    "side" to "sell",
                    "quantity" to formatDecimal(orders.sumOf { it.quantity }, 8), //calculates total quantity on this price level for sell orders
                    "price" to formatDecimal(price, 2),
                    "currencyPair" to orders.first().currencyPair,
                    "orderCount" to orders.size //Order count to show aggregating quantities on the same price levels for sell orders
                )
            }

        return mapOf(
            "Asks" to groupedSellOrders,
            "Bids" to groupedBuyOrders
        )
    }

    //Method to retrieve the last 20 executed trades
    fun getRecentTrades(): List<Map<String, Any>> {
        return trades.takeLast(20).map { trade ->
            mapOf(
                "price" to formatDecimal(trade.price, 2),
                "quantity" to formatDecimal(trade.quantity, 8),
                "currencyPair" to trade.currencyPair,
                "tradedAt" to trade.tradedAt.toString(),
                "takerSide" to trade.takerSide,
                "id" to trade.tradeId
            )
        }
    }

    //Method used to match orders, which will execute trades
    private fun matchOrders() {
        //Loop through all orders ensures both sell and buy orders are populated, if not will stop executing
        while (buyOrders.isNotEmpty() && sellOrders.isNotEmpty()) {
            val highestBuy = buyOrders.poll() //Return the highest buy price and remove from queue
            val lowestSell = sellOrders.poll() //Return the lowest sell price and remove from queue

            if (highestBuy.price >= lowestSell.price) {
                val tradeQuantity = minOf(highestBuy.quantity, lowestSell.quantity) //ensures the trade quantity is the smaller quantity
                //If match conditions are met , this logger indicates that a trade was made
                println("Executing Trade: ${lowestSell.price} for $tradeQuantity BTC")
                val takerSide = if (highestBuy.quantity <= lowestSell.quantity) "buy" else "sell"
                val tradedAt = Instant.now()
                val tradeId = UUID.randomUUID().toString()

                //The order match is stored as a trade with the parameters which are similar to the Valr API reference
                trades.add(
                    Trade(
                        price = lowestSell.price,
                        quantity = tradeQuantity,
                        currencyPair = lowestSell.currencyPair,
                        tradedAt = tradedAt,
                        takerSide = takerSide,
                        tradeId =tradeId
                    )
                )
                //Updates the quantity after execution to accommodate for partial orders
                //Ensures no negative big decimal values and rounds to 8 decimal places
                highestBuy.quantity = BigDecimal(highestBuy.quantity - tradeQuantity)
                    .max(BigDecimal.ZERO)
                    .setScale(8, RoundingMode.HALF_UP)//ensures precise calculations
                    .toDouble()

                lowestSell.quantity = BigDecimal(lowestSell.quantity - tradeQuantity)
                    .max(BigDecimal.ZERO)
                    .setScale(8, RoundingMode.HALF_UP)
                    .toDouble()


                //If the new quantities are not zero,they are added back to the queue with updated quantity values
                if (highestBuy.quantity > 0.0)
                {
                    buyOrders.offer(highestBuy)
                    println("Buy order reinserted with ${highestBuy.quantity} BTC left")
                }

                if (lowestSell.quantity > 0.0)
                {
                    sellOrders.offer(lowestSell)
                    println("Sell order reinserted with ${lowestSell.quantity} BTC left")
                }
            } else
            {
                //If no matches/trades occur then the orders will be added back to the queue
                buyOrders.offer(highestBuy)
                sellOrders.offer(lowestSell)
                println("No match found. Orders reinserted.")
                break
            }
        }
    }

    //Method to format decimal values to prevent JSON errors when doing calculations
    private fun formatDecimal(value: Double, decimals: Int): String {
        return BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toPlainString()
    }
}
