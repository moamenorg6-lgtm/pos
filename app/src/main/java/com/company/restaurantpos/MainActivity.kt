package com.company.restaurantpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.restaurantpos.ui.navigation.Screen
import com.company.restaurantpos.ui.screens.AdminScreen
import com.company.restaurantpos.ui.screens.HomeScreen
import com.company.restaurantpos.ui.screens.KitchenScreen
import com.company.restaurantpos.ui.screens.POSScreen
import com.company.restaurantpos.ui.theme.RestaurantPOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantPOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantPOSApp()
                }
            }
        }
    }
}

@Composable
fun RestaurantPOSApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.POS.route) {
            POSScreen()
        }
        composable(Screen.Kitchen.route) {
            KitchenScreen()
        }
        composable(Screen.Admin.route) {
            AdminScreen()
        }
    }
}