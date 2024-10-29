package com.magnise.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstrumentsNetworkModel(
    @SerialName("data")
    val data: List<Data>,
    val paging: Paging
)

@Serializable
data class Data(
    val baseCurrency: String,
    val currency: String,
    val description: String,
    val id: String,
    val kind: String,
    //val mappings: Mappings,
    val profile: Profile,
    val symbol: String,
    val tickSize: Double
)

@Serializable
data class Paging(
    val items: Int,
    val page: Int,
    val pages: Int
)

@Serializable
data class Mappings(
    @SerialName("active-tick")
    val activeTick: ActiveTick,
    val dxfeed: Dxfeed,
    val oanda: Oanda,
    val simulation: Simulation
)

@Serializable
data class Profile(
    @Transient
    val gics: Gics,
    val name: String
)

@Serializable
data class ActiveTick(
    val defaultOrderSize: Int,
    val exchange: String,
    val symbol: String
)

@Serializable
data class Dxfeed(
    val defaultOrderSize: Int,
    val exchange: String,
    val symbol: String
)

@Serializable
data class Oanda(
    val defaultOrderSize: Int,
    val exchange: String,
    val symbol: String
)

@Serializable
data class Simulation(
    val defaultOrderSize: Int,
    val exchange: String,
    val symbol: String
)

@Serializable
class Gics