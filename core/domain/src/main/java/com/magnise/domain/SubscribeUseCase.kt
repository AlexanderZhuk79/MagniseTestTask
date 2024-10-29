package com.magnise.domain

import com.magnise.network.websocket.WebsocketClientService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscribeUseCase @Inject constructor(
    private val webSocketClientService: WebsocketClientService) {

    fun getDataFlow() = webSocketClientService.dataFlow
    fun getConnectionStateFlow() = webSocketClientService.connectionState

    fun connect(viewModelScope: CoroutineScope, instrumentId: String) {
        viewModelScope.launch {
            webSocketClientService.connect(instrumentId)
        }
    }

    fun stop(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            webSocketClientService.stop()
        }
    }

    suspend fun changeInstrument(newInstrumentId: String) =
        webSocketClientService.changeInstrument(newInstrumentId)
}