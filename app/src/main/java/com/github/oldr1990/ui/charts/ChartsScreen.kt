package com.github.oldr1990.ui.charts

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.MappedBMEData

@Composable
fun ChartScreen(sensorID: String, viewModel: ChartsViewModel){
Column() {
    Text(text = "This is chart screen. ID = $sensorID")
    Button(onClick = { viewModel.getBMEData(0,sensorID) }) {
        Text(text = "Add Random Data")
    }
    viewModel.chartsEvent.collectAsState().value.let { 
        when(it){
            ChartsViewModel.ChartsEvent.Empty -> {
                Text(text = "There is no data!")
            }
            is ChartsViewModel.ChartsEvent.Error -> {Text(text = "Error: ${it.message}!")}
            ChartsViewModel.ChartsEvent.Loading -> {Text(text = "Loading...")}
            is ChartsViewModel.ChartsEvent.Success -> {
                Log.e(LOG_TAG, it.sensorData.toString())
                ListOfData(listOfData = it.sensorData)
            }
        }
    }
}
}

@Composable
fun ListOfData(listOfData: List<MappedBMEData>){
    LazyColumn{
        items(listOfData){dataOfOneSensor->
            Card(elevation = 8.dp) {
                Column {
                    Text(text = dataOfOneSensor.date)
                    Text(text = dataOfOneSensor.humidity.toString())
                    Text(text = dataOfOneSensor.temperature.toString())
                    Text(text = dataOfOneSensor.pressure.toString())
                }
            }
        }
    }
}