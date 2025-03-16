package com.valr.orderbook

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

//Class to store BUY/SELL orders, using JsonProperty to ensure filed names match Json input/output
data class Order(
    @JsonProperty("type") val type: OrderType,
    @JsonProperty("quantity") var quantity: Double, //using var here cause quantity can change when orders are matched
    @JsonProperty("price") val price: Double,
    @JsonProperty("currencyPair") val currencyPair: String
)

//Class to store completed trades for trading history
data class Trade(
    
    val price: Double,
    val quantity: Double,
    val currencyPair: String,
    val tradedAt: Instant,
    val takerSide: String,
    val tradeId: String,
)

//Enum to store valid order types
enum class OrderType {
    BUY, SELL
}
