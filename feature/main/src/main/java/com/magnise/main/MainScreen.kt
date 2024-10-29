package com.magnise.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magnise.domain.models.Instrument
import com.magnise.network.websocket.SocketState
import java.util.Locale


@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()
    MainScreenUI(mainScreenState = viewState,
        onItemSelected = { instrument ->
            viewModel.sendEvent(MainScreenEvents.InstrumentSelected(instrument))
        },
        onSubscribeClick = {
            viewModel.sendEvent(MainScreenEvents.Subscribe)
        }
    )
}


@Composable
@Preview
fun MainScreenUI(mainScreenState: MainScreenState = MainScreenState(),
                 onItemSelected: (instrument: Instrument)-> Unit = {},
                 onSubscribeClick: ()-> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Dropdown(
                modifier = Modifier.weight(1f),
                mainScreenState.instruments, onItemSelected
            )

            Button(modifier = Modifier
                .padding(horizontal = 4.dp),
                shape = MaterialTheme.shapes.medium,
                onClick = onSubscribeClick,
                content = {
                    Text(
                        text = if (mainScreenState.connectionState == SocketState.DISCONNECTED)
                            stringResource(R.string.subscribe) else stringResource(R.string.unsubscribe),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }


        Column {
            Text("Market data:", style = MaterialTheme.typography.labelSmall)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(
                        border = BorderStroke(
                            1.dp,
                            color = Color.Black
                        )
                    )
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MarketDataItem(
                    modifier = Modifier
                        .weight(1f), stringResource(R.string.symbol),
                    mainScreenState.selectedInstrument?.symbol ?: ""
                )

                MarketDataItem(
                    modifier = Modifier
                        .weight(1f), stringResource(R.string.price),
                    String.format(
                        Locale.getDefault(),
                        "%.2f", mainScreenState.marketData?.price ?: 0f
                    )
                )

                MarketDataItem(
                    modifier = Modifier
                        .weight(1f),
                    stringResource(R.string.time),
                    mainScreenState.marketData?.timeFormatted() ?: ""
                )
            }
        }

        Column(modifier = Modifier.heightIn(min = 500.dp, max = 1500.dp)) {
            Text(
                text = stringResource(R.string.charting_data),
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall
            )


            ComposeChart(
                Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(
                            1.dp,
                            color = Color.Black
                        ),
                        shape = RoundedCornerShape(5)
                    )
                    .height(500.dp)
                    .padding(bottom = 16.dp), mainScreenState.countBack
            )
        }
    }
}

@Composable
fun MarketDataItem(modifier: Modifier, caption: String, text: String){
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(text = caption,
            modifier = Modifier.wrapContentSize(),
            color = Color.Unspecified,
            style = MaterialTheme.typography.labelSmall

        )
        Text(text = text,
            modifier = Modifier.wrapContentSize(),
            color = Color.Unspecified,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

