package com.magnise.domain.models

import com.magnise.network.models.CountBackNetworkModel
import com.magnise.network.models.InstrumentsNetworkModel
import com.magnise.network.models.SocketDataNetworkModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun InstrumentsNetworkModel.toInstrumentList() =
    this.data.map { Instrument(it.id, it.symbol) }

fun CountBackNetworkModel.toCountBackDataList(): CountBackNormalized {
    var min = 0.0
    var max = 0.0

    if (this.data.isNotEmpty()) {
        min = this.data[0].o
        max = this.data[0].o
        this.data.forEach {
            if (it.o > max) max = it.o
            if (it.c > max) max = it.c
            if (it.h > max) max = it.h
            if (it.l > max) max = it.l

            if (it.o < min) min = it.o
            if (it.c < min) min = it.c
            if (it.h < min) min = it.h
            if (it.l < min) min = it.l
        }
    }

    return CountBackNormalized(
        this.data.map { CountBackData(c = it.c,h = it.h, l= it.l, o = it.o,
            timeStamp = LocalDateTime.parse(it.t, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        ) }, min, max
    )
}

fun SocketDataNetworkModel.toMarketData(): MarketData {
    var price = 0f
    var time = LocalDateTime.now()
    this.ask?.let{
        price = it.price
        time = LocalDateTime.parse(it.timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }
    this.bid?.let{
        price = it.price
        time = LocalDateTime.parse(it.timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }
    this.last?.let{
        price = it.price
        time = LocalDateTime.parse(it.timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }
    return MarketData(price, time)
}
