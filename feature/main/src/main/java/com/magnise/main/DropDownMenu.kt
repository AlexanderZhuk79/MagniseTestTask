package com.magnise.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.magnise.domain.models.Instrument

@Composable
fun Dropdown(modifier: Modifier, items: List<Instrument>, onItemSelected: (instrument: Instrument)-> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    Box(
        modifier = modifier
    ) {
        val text = if (items.isEmpty()) "" else items[selectedIndex].symbol
        Text(
            text = text,
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
                .fillMaxWidth()
                .padding(end = 24.dp)
                .border(
                    border = BorderStroke(
                        1.dp,
                        color = Color.Black
                    )
                )
                .clickable(onClick = { expanded = true })
                .background(Color.White)
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize().background(
                Color.White
            )
        ) {
            items.forEachIndexed { index, s ->
                var itemModifier = Modifier.padding(start = 10.dp, end = 10.dp)
                if (index == selectedIndex) itemModifier = Modifier.background(
                    Color.LightGray, RoundedCornerShape(10.dp)
                )
                DropdownMenuItem(
                    text = {
                        Text(text = s.symbol, color = Color.Black)
                    },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onItemSelected(s)
                    },
                    itemModifier
                )
            }
        }
    }
}