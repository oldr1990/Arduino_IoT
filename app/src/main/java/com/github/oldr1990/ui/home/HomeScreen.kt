package com.github.oldr1990.ui.home


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.MappedSensor
import com.github.oldr1990.ui.composes.ArduinoIoTTopAppBar

@ExperimentalMaterialApi
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigateAuthScreenAfterLogout: ()-> Unit, navigateFromHomeToSensor: (String) -> Unit) {

    var listOfSensors by remember { mutableStateOf(listOf(MappedSensor("","","",""))) }
    val eventHandler = viewModel.homeScreenEvent.collectAsState()
    eventHandler.value.let { event ->
        listOfSensors = viewModel.listOfSensor.value
        when (event) {
            HomeViewModel.HomeScreenEvent.Empty -> {
                Log.i(LOG_TAG, event.toString())
            }
            is HomeViewModel.HomeScreenEvent.Error -> {
                Log.i(LOG_TAG, event.message)
            }
            HomeViewModel.HomeScreenEvent.Loading -> {
                Log.i(LOG_TAG, event.toString())
            }
            HomeViewModel.HomeScreenEvent.NotAuthorized -> {
                navigateAuthScreenAfterLogout()
            }
            is HomeViewModel.HomeScreenEvent.Success -> {
                listOfSensors = event.sensorFirebases
                Log.i(LOG_TAG, event.sensorFirebases.toString())
            }
        }
    }
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ArduinoIoTTopAppBar {} },
        content = {
            LazyColumn {
                items(listOfSensors) { sensor ->
                    val onClickHandler: () -> Unit = {
                       navigateFromHomeToSensor(sensor.id)
                    }
                    SensorItem(sensor, onClickHandler)
                }
            }
        },
        /*   bottomBar = {
               ArduinoIoTBottomAppBar(
                   isItHomePage = true,
                   homeOnClick = { },
                   temperatureOnClick = { },
                   pressureOnClick = { },
                   humidityOnClick = { }, allOnClick = { })
           }*/
    )
    Log.i(LOG_TAG, "Homescreen init")
    viewModel.init()
}

@ExperimentalMaterialApi
@Composable
fun SensorItem(sensorFirebase: MappedSensor, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler,colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        modifier = Modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(CenterVertically)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(15.dp),
        ) {
            Text(text = sensorFirebase.name, fontSize = 32.sp, color = Color.DarkGray)
            Text(text = sensorFirebase.description, fontSize = 24.sp, color = Color.Gray)
        }
    }
}