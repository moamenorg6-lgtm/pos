package com.company.restaurantpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.restaurantpos.ui.navigation.AppNavigation
import com.company.restaurantpos.ui.theme.RestaurantThemeProvider
import com.company.restaurantpos.ui.theme.ThemeMode
import com.company.restaurantpos.ui.viewmodels.ThemeViewModel
import com.company.restaurantpos.utils.LocalizationManager
import com.company.restaurantpos.utils.LocalizationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var localizationManager: LocalizationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantPOSApp()
        }
    }
    
    @Composable
    private fun RestaurantPOSApp() {
        val themeViewModel: ThemeViewModel = hiltViewModel()
        val themeMode by themeViewModel.themeMode.collectAsState()
        
        RestaurantThemeProvider(themeMode = themeMode) {
            LocalizationProvider(localizationManager) { localization ->
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        localizationManager = localizationManager
                    )
                }
            }
        }
    }
}