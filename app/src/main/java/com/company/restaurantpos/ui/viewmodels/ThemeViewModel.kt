package com.company.restaurantpos.ui.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.restaurantpos.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * ViewModel for managing app theme settings
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    /**
     * Current theme mode as Flow
     */
    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(themeModeString)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }
    
    /**
     * Set theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            context.themeDataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
        }
    }
    
    /**
     * Toggle between light and dark mode
     */
    fun toggleTheme() {
        viewModelScope.launch {
            val currentModeValue = context.themeDataStore.data.map { preferences ->
                val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                try {
                    ThemeMode.valueOf(themeModeString)
                } catch (e: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            }.firstOrNull() ?: ThemeMode.SYSTEM
            
            // For simplicity, toggle between LIGHT and DARK
            // In a real app, you might want more sophisticated logic
            val newMode = when (currentModeValue) {
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.SYSTEM -> ThemeMode.DARK // Default to dark when toggling from system
            }
            
            setThemeMode(newMode)
        }
    }
}