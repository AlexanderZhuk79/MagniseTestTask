package com.magnise.main

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.magnise.domain.models.CountBackNormalized
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Legend
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Locale


private val colors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Blue )
private val names = listOf("c", "l", "h", "o")

@Composable
fun ComposeChart(modifier: Modifier, countBack: CountBackNormalized) {

    val rangeProviderCustom = getRangeProvider(countBack)

    val configuration = LocalConfiguration.current

    val labelsRotation = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        90f else 0f

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(countBack) {
        withContext(Dispatchers.Default) {
            if (countBack.listCountBackData.isEmpty()) return@withContext
            modelProducer.runTransaction {
                /* Learn more:
                https://patrykandpatrick.com/vico/wiki/cartesian-charts/layers/line-layer#data. */
                lineSeries {
                    series( countBack.listCountBackData
                        .map { it.timeStamp.hour * 100 + it.timeStamp.minute},
                        countBack.listCountBackData.map { it.c } )

                    series( countBack.listCountBackData
                        .map { it.timeStamp.hour * 100 + it.timeStamp.minute  },
                        countBack.listCountBackData.map { it.l } )

                    series( countBack.listCountBackData
                        .map { it.timeStamp.hour * 100 + it.timeStamp.minute  },
                        countBack.listCountBackData.map { it.h } )

                    series( countBack.listCountBackData
                        .map { it.timeStamp.hour * 100 + it.timeStamp.minute  },
                        countBack.listCountBackData.map { it.o } )
                }
            }
        }
    }

    val marker = rememberMarker()
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    colors.map {
                        LineCartesianLayer.rememberLine(
                            remember { LineCartesianLayer.LineFill.single(fill(it)) }
                        )
                    }
                )
                ,rangeProvider = rangeProviderCustom
            ),
            startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(color = Color.Black),
                itemPlacer = remember { VerticalAxis.ItemPlacer.count({5}) },
                valueFormatter = remember { getValueFormatter()  },
            ),
            bottomAxis =
            HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(color = Color.Black),
                guideline = null,
                labelRotationDegrees = labelsRotation,
                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
                valueFormatter = remember { getHorizontalLabelsValueFormatter()  },
            ),
            marker = marker,
            layerPadding = cartesianLayerPadding(scalableStart = 0.dp, scalableEnd = 0.dp),
            legend = rememberLegend(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        //runInitialAnimation = false,
        //animationSpec = null,
        scrollState = rememberVicoScrollState(),
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}

@Composable
private fun rememberLegend(): Legend<CartesianMeasuringContext, CartesianDrawingContext> {
    val labelComponent = rememberTextComponent(Color.Black)
    return rememberHorizontalLegend(
        items = rememberExtraLambda {
            colors.forEachIndexed { index, color ->
                add(
                    LegendItem(
                        icon = shapeComponent(color, CorneredShape.Pill),
                        labelComponent = labelComponent,
                        label = names[index],
                    )
                )
            }
        },
        padding = dimensions(top = 4.dp, start = 48.dp),
    )
}

fun getRangeProvider(countBack: CountBackNormalized): CartesianLayerRangeProvider =
    CartesianLayerRangeProvider.fixed(minY = countBack.minY / 1.00003 , maxY = countBack.maxY * 1.00003)

fun getValueFormatter(): CartesianValueFormatter {
    return CartesianValueFormatter.decimal(DecimalFormat("###.#####;−###.#####"))
}

fun getHorizontalLabelsValueFormatter(): CartesianValueFormatter  =
    CartesianValueFormatter { _, value, _ ->
        var s = String.format(Locale.getDefault(),"%.2f", value / 100)
        s = s.replace(".",":")
        s.replace(",",":")
    }
