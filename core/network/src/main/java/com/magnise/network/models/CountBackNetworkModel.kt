package com.magnise.network.models

import kotlinx.serialization.Serializable

@Serializable
data class CountBackNetworkModel(
    val data: List<CountBackDataNetworkModel>
)

@Serializable
data class CountBackDataNetworkModel(
    val c: Double,
    val h: Double,
    val l: Double,
    val o: Double,
    val t: String,
    val v: Int
)