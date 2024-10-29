package com.magnise.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SocketDataNetworkModel(
    val ask: Ask?= null,
    val bid: Bid?= null,
    val last: Last?= null,
    val instrumentId: String = "",
    val provider: String = "",
    val type: String = "",
    val sessionId: String? = null,
    val requestId: String? = null
    )

@Serializable
data class Ask(
    val price: Float,
    val timestamp: String,
    val volume: Int
)

@Serializable
data class Bid(
    val price: Float,
    val timestamp: String,
    val volume: Int
)

@Serializable
data class Last(
    val price: Float,
    val timestamp: String,
    val volume: Int
)