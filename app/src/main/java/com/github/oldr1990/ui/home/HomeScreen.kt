package com.github.oldr1990.ui.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.oldr1990.model.Sensor
import com.github.oldr1990.ui.composes.ArduinoIoTBottomAppBar
import com.github.oldr1990.ui.composes.ArduinoIoTTopAppBar

@Composable
fun HomeScreen() {
    val listOfSensors: List<Sensor> =
        listOf(Sensor("Name of Sensor", "", "Description of sensor: this is some sensor", ""))
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ArduinoIoTTopAppBar {} },
        content = {
            LazyColumn{
                items(listOfSensors) { sensor ->
                    SensorItem(sensor = sensor)
                }
            }
        },
        bottomBar = {
            ArduinoIoTBottomAppBar(
                isItHomePage = true,
                homeOnClick = { },
                temperatureOnClick = { },
                pressureOnClick = { },
                humidityOnClick = { }, allOnClick = { })
        }
    )
}

@Composable
fun SensorItem(sensor: Sensor) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(CenterVertically)
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(15.dp)
        ) {
            Text(text = sensor.name, fontSize = 32.sp, color = Color.DarkGray)
            Text(text = sensor.description, fontSize = 24.sp, color = Color.Gray)
        }
    }
}