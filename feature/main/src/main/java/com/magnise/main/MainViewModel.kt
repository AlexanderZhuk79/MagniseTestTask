package com.magnise.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnise.domain.GetCountBackUseCase
import com.magnise.domain.GetInstrumentsListUseCase
import com.magnise.domain.SubscribeUseCase
import com.magnise.domain.models.Instrument
import com.magnise.domain.models.toMarketData
import com.magnise.network.websocket.SocketState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscribeUseCase: SubscribeUseCase,
    private val getInstrumentsListUseCase: GetInstrumentsListUseCase,
    private val getCountBackUseCase: GetCountBackUseCase
): ViewModel() {

    private val mStateFlow: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState())
    val stateFlow = mStateFlow.asStateFlow()
        .onCompletion {
            subscribeUseCase.stop(viewModelScope)
        }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MainScreenState())

    private val eventChannel = Channel<MainScreenEvents>(Channel.UNLIMITED)
    private val eventFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            eventFlow.collect { event ->
                processEvent(event)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val instrumentList = getInstrumentsListUseCase()
                mStateFlow.value = mStateFlow.value.copy(instruments = instrumentList)

                if (mStateFlow.value.selectedInstrument == null &&
                    instrumentList.isNotEmpty()) setSelectedInstrument(instrumentList[0])

        }

        viewModelScope.launch {
            subscribeUseCase.getConnectionStateFlow().collect {
                mStateFlow.value = mStateFlow.value.copy(connectionState = it)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            subscribeUseCase.getDataFlow().collect { data ->
                mStateFlow.value = mStateFlow.value.copy(marketData = data.toMarketData())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscribeUseCase.stop(viewModelScope)
    }

    fun sendEvent(event : MainScreenEvents){
        eventChannel.trySend(event)
            .getOrNull()
    }

    private fun processEvent(event: MainScreenEvents) {
        when (event) {
            is MainScreenEvents.InstrumentSelected -> {
                setSelectedInstrument(event.instrument)
            }
            is MainScreenEvents.Subscribe -> {
                if (mStateFlow.value.connectionState == SocketState.CONNECTED)
                    subscribeUseCase.stop(viewModelScope)
                else  mStateFlow.value.selectedInstrument?.let {
                    subscribeUseCase.connect(viewModelScope, it.id)
                }
            }
        }
    }

    private fun setSelectedInstrument(instrument: Instrument){
        mStateFlow.value = mStateFlow.value.copy(selectedInstrument = instrument)

        viewModelScope.launch(Dispatchers.IO) {

            subscribeUseCase.changeInstrument(instrument.id)

            mStateFlow.value = mStateFlow.value.copy(
                countBack = getCountBackUseCase(instrument.id))
        }
    }
}