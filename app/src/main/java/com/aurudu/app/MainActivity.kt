package com.aurudu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aurudu.app.data.Language
import com.aurudu.app.data.LanguagePreference
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
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "getStart") {
        composable("getStart") {
            GetStartScreen(onLanguageSelected = { language ->
                LanguagePreference.set(context, language)
                navController.navigate("home/${language.name}")
            })
        }
        composable(
            "home/{language}",
            arguments = listOf(navArgument("language") { type = NavType.StringType }),
        ) { backStackEntry ->
            val language = Language.valueOf(
                backStackEntry.arguments?.getString("language") ?: Language.SINHALA.name
            )
            HomeScreen(language = language)
        }
    }
}
