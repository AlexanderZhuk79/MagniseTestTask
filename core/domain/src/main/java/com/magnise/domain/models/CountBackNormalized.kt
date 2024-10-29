package com.magnise.domain.models

data class CountBackNormalized (
    val listCountBackData: List<CountBackData> = emptyList(),
    val minY: Double = 0.0,
    val maxY: Double = 0.0
)