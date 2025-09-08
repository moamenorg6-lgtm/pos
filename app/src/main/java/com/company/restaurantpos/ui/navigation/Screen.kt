package com.company.restaurantpos.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object POS : Screen("pos")
    object Kitchen : Screen("kitchen")
    object Admin : Screen("admin")
}