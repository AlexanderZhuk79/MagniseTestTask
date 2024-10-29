package com.magnise.network

import com.magnise.network.models.CountBackNetworkModel
import com.magnise.network.models.InstrumentsNetworkModel
import com.magnise.network.restapi.ApiResponse

interface RemoteRepository {
    suspend fun getInstruments(): ApiResponse<InstrumentsNetworkModel>
    suspend fun getCountBack(instrumentId: String): ApiResponse<CountBackNetworkModel>
}