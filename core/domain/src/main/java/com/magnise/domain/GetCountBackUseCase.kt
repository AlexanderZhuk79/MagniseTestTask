package com.magnise.domain

import com.magnise.domain.models.CountBackNormalized
import com.magnise.domain.models.toCountBackDataList
import com.magnise.network.RemoteRepository
import com.magnise.network.restapi.ApiResponse
import javax.inject.Inject

class GetCountBackUseCase  @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(instrumentId: String): CountBackNormalized {
        return  when(val countBackNetworkModel = remoteRepository.getCountBack(instrumentId)){
                    is ApiResponse.Success -> countBackNetworkModel.data.toCountBackDataList()
                    is ApiResponse.Failure ->  CountBackNormalized()
                }
        }
    }
