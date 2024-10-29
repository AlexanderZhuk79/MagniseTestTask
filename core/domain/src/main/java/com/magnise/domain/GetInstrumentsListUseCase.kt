package com.magnise.domain

import com.magnise.domain.models.Instrument
import com.magnise.domain.models.toInstrumentList
import com.magnise.network.RemoteRepository
import com.magnise.network.restapi.ApiResponse
import javax.inject.Inject

class GetInstrumentsListUseCase @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(): List<Instrument> {
        return when(val instruments = remoteRepository.getInstruments()){
                    is ApiResponse.Success -> instruments.data.toInstrumentList()
                    is ApiResponse.Failure ->  emptyList()
                }
    }
}
