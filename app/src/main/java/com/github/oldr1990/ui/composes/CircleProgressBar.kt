package com.github.oldr1990.ui.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.oldr1990.ui.theme.HalfTransparent


@Composable
fun CircleProgressBar(state: MutableState<Boolean>) {
    if (state.value) {
        Box(
            contentAlignment = Alignment.Center, modifier =
            Modifier
                .fillMaxSize()
                .background(HalfTransparent)
              //  .clickable { }
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    }
}