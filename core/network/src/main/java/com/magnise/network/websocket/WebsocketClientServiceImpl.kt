package com.magnise.network.websocket

import android.util.Log
import com.magnise.datastore.DataStorePreferenceAPI
import com.magnise.datastore.PreferenceDataStoreConstants.ACCESS_TOKEN_KEY
import com.magnise.network.di.NetworkModule
import com.magnise.network.models.SocketCommand
import com.magnise.network.models.SocketDataNetworkModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebsocketClientServiceImpl @Inject constructor(
    @NetworkModule.WebSocketClient private val client: HttpClient,
    private val dataStorePreferenceAPI: DataStorePreferenceAPI
): WebsocketClientService {

    private val TAG = "WebsocketClientService"

    private val jsonParser = Json { ignoreUnknownKeys = true }

    override var instrumentId: String = ""

    private val mConnectionState = MutableStateFlow(SocketState.DISCONNECTED)
    override val connectionState: StateFlow<SocketState> = mConnectionState

    private val mDataFlow = MutableSharedFlow<SocketDataNetworkModel>()
    override val dataFlow: SharedFlow<SocketDataNetworkModel> = mDataFlow

    private var session: WebSocketSession? = null

    override suspend fun connect(newInstrumentId: String) {
        Log.d(TAG, "connect $newInstrumentId")
        instrumentId = newInstrumentId

        try {
            val token = dataStorePreferenceAPI.getFirstPreference(ACCESS_TOKEN_KEY, "")
            val url = "$baseUrl$token"

            session = client.webSocketSession(url)

            mConnectionState.value = SocketState.CONNECTED

            val command = Json.encodeToString(SocketCommand.getCommand(instrumentId))

            session!!.incoming
                .receiveAsFlow()
                .filterIsInstance<Frame.Text>()
                .filterNotNull()
                .map { data ->
                    jsonParser.decodeFromString<SocketDataNetworkModel>(data.readText())
                }
                .filter { it.type == "l1-update" }
                .onStart {
                    send(command)
                }
                .collect { message ->
                    mDataFlow.emit(message) //tryEmit(message)
                }
        } catch (e: Exception) {
            Log.d(TAG, "catch Exception $e")
            mConnectionState.value = SocketState.DISCONNECTED
        }
    }

    override suspend fun changeInstrument(newInstrumentId: String) {
        Log.d(TAG, "changeInstrument $newInstrumentId")
        if (mConnectionState.value ==  SocketState.CONNECTED){
            stopSession()
            connect(newInstrumentId)
        }

    }

    private suspend fun stopSession(){
        session?.close()
        session?.flush()
        session = null
    }

    override suspend fun stop() {
        Log.d(TAG, "stop")
        stopSession()
        mConnectionState.value = SocketState.DISCONNECTED
    }

    private suspend fun send(message: String) {
        Log.d(TAG, "send $message")
        session?.send(Frame.Text(message))
    }
}

enum class SocketState{
    CONNECTED, DISCONNECTED
}

//private const val RECONNECT_DELAY = 10_000L
private const val baseUrl = "wss://platform.fintacharts.com/api/streaming/ws/v1/realtime?token="