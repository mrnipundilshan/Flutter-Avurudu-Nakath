package com.aurudu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aurudu.app.ui.screens.GetStartScreen
import com.aurudu.app.ui.screens.HomeScreen
import com.aurudu.app.ui.theme.AvurudunakathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvurudunakathTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "getStart") {
        composable("getStart") {
            GetStartScreen(onSinhalaSelected = { navController.navigate("home") })
        }
        composable("home") {
            HomeScreen()
        }
    }
}
