package com.magnise.network

import com.magnise.network.models.CountBackNetworkModel
import com.magnise.network.models.InstrumentsNetworkModel
import com.magnise.network.restapi.ApiResponse
import com.magnise.network.restapi.RestService
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val restService: RestService
): RemoteRepository {

    override suspend fun getInstruments(): ApiResponse<InstrumentsNetworkModel> {
        return restService.getInstruments()
    }

    override suspend fun getCountBack(instrumentId: String): ApiResponse<CountBackNetworkModel> {
        return restService.getCountBack(instrumentId)
    }

}