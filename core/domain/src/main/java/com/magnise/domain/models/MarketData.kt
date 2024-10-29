package com.magnise.domain.models


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class MarketData (
    val price: Float,
    val time: LocalDateTime
){
    fun timeFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a")
        return time.format(formatter)
    }
}
