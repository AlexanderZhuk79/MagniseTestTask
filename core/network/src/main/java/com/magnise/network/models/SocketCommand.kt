package com.magnise.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SocketCommand(
    val id: String,
    val instrumentId: String,
    val kinds: List<String>,
    val provider: String,
    val subscribe: Boolean,
    val type: String
){
    companion object {
        fun getCommand(instrumentId: String): SocketCommand{
            return SocketCommand(
                id = "1",
                instrumentId = instrumentId,
                provider = "simulation",
                subscribe = true,
                kinds = listOf("ask", "bid", "last"),
                type = "l1-subscription"
            )
        }
    }
}