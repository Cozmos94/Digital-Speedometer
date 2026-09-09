package com.digitalspeedometer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalspeedometer.app.data.PreferencesManager
import com.digitalspeedometer.app.ui.HomeScreen
import com.digitalspeedometer.app.ui.SpeedometerScreen
import com.digitalspeedometer.app.ui.theme.DigitalSpeedometerTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_SPEEDOMETER = "speedometer"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferencesManager(this)

        setContent {
            DigitalSpeedometerTheme {
                DigitalSpeedometerNavHost(prefs)
            }
        }
    }
}

@Composable
private fun DigitalSpeedometerNavHost(prefs: PreferencesManager) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(onStartClick = { navController.navigate(ROUTE_SPEEDOMETER) })
        }
        composable(ROUTE_SPEEDOMETER) {
            SpeedometerScreen(prefs = prefs, onBack = { navController.popBackStack() })
        }
    }
}
