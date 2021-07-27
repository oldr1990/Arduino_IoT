package com.github.oldr1990.ui.composes

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import com.github.oldr1990.data.Constants.BottomNavigationLabels.ALL
import com.github.oldr1990.data.Constants.BottomNavigationLabels.HOME
import com.github.oldr1990.data.Constants.BottomNavigationLabels.HUMIDITY
import com.github.oldr1990.data.Constants.BottomNavigationLabels.PRESSURE
import com.github.oldr1990.data.Constants.BottomNavigationLabels.TEMPERATURE

@Composable
fun ArduinoIoTBottomAppBar(
    isItHomePage: Boolean,
    homeOnClick: () -> Unit,
    temperatureOnClick: () -> Unit,
    pressureOnClick: ()-> Unit,
    humidityOnClick: ()-> Unit,
    allOnClick: ()-> Unit,
) {
    BottomAppBar(content = {
        BottomNavigation() {
            BottomNavigationItem(
                label = { Text(HOME) },
                alwaysShowLabel = true,
                icon = { Icon(Icons.Outlined.Home, "") },
                selected = true,
                onClick = homeOnClick)

            BottomNavigationItem(
                label = { Text(ALL) },
                alwaysShowLabel = true,
                icon = { Icon(Icons.Outlined.Build, "") },
                selected = true,
                onClick = allOnClick)

            BottomNavigationItem(
                label = { Text(PRESSURE) },
                alwaysShowLabel = true,
                icon = { Icon(Icons.Outlined.Home, "") },
                selected = true,
                onClick = pressureOnClick)

            BottomNavigationItem(
                label = { Text(HUMIDITY) },
                alwaysShowLabel = true,
                icon = { Icon(Icons.Outlined.Home, "") },
                selected = true,
                onClick = humidityOnClick)
            BottomNavigationItem(
                label = { Text(TEMPERATURE) },
                alwaysShowLabel = true,
                icon = { Icon(Icons.Outlined.Home, "") },
                selected = true,
                onClick = temperatureOnClick)
        }

    })
}