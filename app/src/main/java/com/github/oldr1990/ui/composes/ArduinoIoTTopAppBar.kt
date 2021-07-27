package com.github.oldr1990.ui.composes

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.github.oldr1990.data.Constants.APPLICATION_NAME

@Composable
fun ArduinoIoTTopAppBar(navigationIcon: @Composable () -> Unit) {
    TopAppBar(
        title = { Text(text = APPLICATION_NAME) },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        elevation = 4.dp,
        navigationIcon = navigationIcon
    )
}