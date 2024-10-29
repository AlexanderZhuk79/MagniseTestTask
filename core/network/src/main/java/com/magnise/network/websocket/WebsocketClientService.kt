package com.magnise.network.websocket

import com.magnise.network.models.SocketDataNetworkModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface WebsocketClientService {

    val connectionState: StateFlow<SocketState>

    val dataFlow: SharedFlow<SocketDataNetworkModel>

    var instrumentId: String

    suspend fun connect(newInstrumentId: String)

    suspend fun stop()

    suspend fun changeInstrument(newInstrumentId: String)


}