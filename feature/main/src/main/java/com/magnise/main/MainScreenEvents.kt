package com.magnise.main

import com.magnise.domain.models.Instrument

sealed interface MainScreenEvents {
    data class InstrumentSelected(val instrument: Instrument): MainScreenEvents
    data object Subscribe: MainScreenEvents
}