package com.company.restaurantpos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * Enhanced theme with restaurant branding colors and dark/light mode support
 */

// Restaurant Brand Colors
private val RestaurantPrimary = Color(0xFF2E7D32) // Forest Green
private val RestaurantPrimaryVariant = Color(0xFF1B5E20) // Dark Green
private val RestaurantSecondary = Color(0xFFFF6F00) // Orange
private val RestaurantSecondaryVariant = Color(0xFFE65100) // Dark Orange
private val RestaurantAccent = Color(0xFFFFC107) // Amber
private val RestaurantError = Color(0xFFD32F2F) // Red
private val RestaurantSuccess = Color(0xFF388E3C) // Green
private val RestaurantWarning = Color(0xFFF57C00) // Orange

// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = RestaurantPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    
    secondary = RestaurantSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFE65100),
    
    tertiary = RestaurantAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFF8E1),
    onTertiaryContainer = Color(0xFFF57F17),
    
    error = RestaurantError,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFA5D6A7)
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFFE65100),
    secondaryContainer = Color(0xFFFF8F00),
    onSecondaryContainer = Color(0xFFFFE0B2),
    
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFFF57F17),
    tertiaryContainer = Color(0xFFFFC107),
    onTertiaryContainer = Color(0xFFFFF8E1),
    
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFEBEE),
    
    background = Color(0xFF10131C),
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF10131C),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF2E7D32)
)

/**
 * Theme mode enum
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Enhanced Restaurant POS Theme
 */
@Composable
fun RestaurantPOSTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RestaurantTypography,
        shapes = RestaurantShapes,
        content = content
    )
}

/**
 * Custom typography for restaurant branding
 */
val RestaurantTypography = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    displayMedium = Typography().displayMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    displaySmall = Typography().displaySmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    titleLarge = Typography().titleLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    titleSmall = Typography().titleSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    bodyLarge = Typography().bodyLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    bodySmall = Typography().bodySmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
    ),
    labelLarge = Typography().labelLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelMedium = Typography().labelMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelSmall = Typography().labelSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    )
)

/**
 * Custom shapes for restaurant UI
 */
val RestaurantShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

/**
 * Extended color palette for restaurant theme
 */
@Stable
class RestaurantColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

val LightRestaurantColors = RestaurantColors(
    success = RestaurantSuccess,
    onSuccess = Color.White,
    successContainer = Color(0xFFE8F5E8),
    onSuccessContainer = Color(0xFF1B5E20),
    warning = RestaurantWarning,
    onWarning = Color.White,
    warningContainer = Color(0xFFFFF3E0),
    onWarningContainer = Color(0xFFE65100),
    info = Color(0xFF1976D2),
    onInfo = Color.White,
    infoContainer = Color(0xFFE3F2FD),
    onInfoContainer = Color(0xFF0D47A1)
)

val DarkRestaurantColors = RestaurantColors(
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF1B5E20),
    successContainer = Color(0xFF2E7D32),
    onSuccessContainer = Color(0xFFE8F5E8),
    warning = Color(0xFFFFB74D),
    onWarning = Color(0xFFE65100),
    warningContainer = Color(0xFFF57C00),
    onWarningContainer = Color(0xFFFFF3E0),
    info = Color(0xFF64B5F6),
    onInfo = Color(0xFF0D47A1),
    infoContainer = Color(0xFF1976D2),
    onInfoContainer = Color(0xFFE3F2FD)
)

val LocalRestaurantColors = staticCompositionLocalOf { LightRestaurantColors }

/**
 * Access extended restaurant colors
 */
val MaterialTheme.restaurantColors: RestaurantColors
    @Composable
    @ReadOnlyComposable
    get() = LocalRestaurantColors.current

/**
 * Theme provider with extended colors
 */
@Composable
fun RestaurantThemeProvider(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val restaurantColors = if (darkTheme) DarkRestaurantColors else LightRestaurantColors
    
    CompositionLocalProvider(
        LocalRestaurantColors provides restaurantColors
    ) {
        RestaurantPOSTheme(
            themeMode = themeMode,
            content = content
        )
    }
}