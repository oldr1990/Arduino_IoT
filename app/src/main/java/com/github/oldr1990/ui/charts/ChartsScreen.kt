package com.github.oldr1990.ui.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ChartScreen(sensorID: String, viewModel: ChartsViewModel){
Column() {
    Text(text = "This is chart screen. ID = $sensorID")
    Button(onClick = { viewModel.startTestDataInput(sensorID) }) {
        Text(text = "Add Random Data")
    }
}
}