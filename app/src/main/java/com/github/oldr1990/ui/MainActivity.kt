package com.github.oldr1990.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.github.oldr1990.R
import com.github.oldr1990.data.Constants.EMPTY_STRING
import com.github.oldr1990.data.Constants.NavigationDestinations.AUTH_PAGE
import com.github.oldr1990.data.Constants.NavigationDestinations.CHARTS_PAGE
import com.github.oldr1990.data.Constants.NavigationDestinations.HOME_PAGE
import com.github.oldr1990.ui.auth.AuthScreen
import com.github.oldr1990.ui.auth.AuthViewModel
import com.github.oldr1990.ui.charts.ChartScreen
import com.github.oldr1990.ui.charts.ChartsViewModel
import com.github.oldr1990.ui.home.HomeScreen
import com.github.oldr1990.ui.home.HomeViewModel
import com.github.oldr1990.ui.theme.ArduinoIoTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val chartsViewModel: ChartsViewModel by viewModels()
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ArduinoIoT)
        setContent {
            val navController = rememberNavController()
            ArduinoIoTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize(1f)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AUTH_PAGE
                    ) {
                        composable(AUTH_PAGE) {
                           AuthScreen(authViewModel, navController)
                        }
                        composable(
                            "$HOME_PAGE{userID}",
                            arguments = listOf(navArgument("userID") { type = NavType.StringType })
                        ) {
                            val userID = it.arguments?.getString("userID")
                            if (userID == EMPTY_STRING || userID == null) navController.navigate(AUTH_PAGE)
                            HomeScreen(homeViewModel,navController)
                        }
                        composable("$CHARTS_PAGE{sensorID}",
                            arguments = listOf(navArgument("sensorID") { type = NavType.StringType })
                        ){
                            val sensorID = it.arguments?.getString("sensorID")?:""
                            ChartScreen(sensorID, chartsViewModel)
                        }
                    }

                }
            }
        }
    }
}
