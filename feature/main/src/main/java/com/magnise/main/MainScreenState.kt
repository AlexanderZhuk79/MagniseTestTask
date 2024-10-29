package com.magnise.main

import com.magnise.domain.models.CountBackNormalized
import com.magnise.domain.models.Instrument
import com.magnise.domain.models.MarketData
import com.magnise.network.websocket.SocketState

data class MainScreenState(
    val selectedInstrument: Instrument? = null,
    val connectionState: SocketState = SocketState.DISCONNECTED,
    val instruments: List<Instrument> = emptyList(),
    val seriesData: SeriesData? = null,
    val countBack: CountBackNormalized = CountBackNormalized(),
    val marketData: MarketData? = null
)

data class SeriesData(
    val priceType: PriceType,
    val timeStamp: String,
    val price: Float
)

enum class PriceType{
    ASK, BID, LAST
}