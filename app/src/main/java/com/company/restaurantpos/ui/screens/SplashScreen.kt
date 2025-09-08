package com.company.restaurantpos.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.restaurantpos.R
import com.company.restaurantpos.ui.viewmodels.AuthViewModel
import com.company.restaurantpos.utils.LocalizationManager
import com.company.restaurantpos.utils.LocalizationProvider
import kotlinx.coroutines.delay

/**
 * Splash screen with animated logo and loading indicator
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    localizationManager: LocalizationManager
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    
    // Logo animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "logo_alpha"
    )
    
    // Text animation
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "text_alpha"
    )
    
    // Loading animation
    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")
    val loadingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_rotation"
    )
    
    // Start animation on composition
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) // Show splash for 2.5 seconds
        
        // Navigate based on authentication state
        if (authUiState.isLoggedIn) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }
    
    LocalizationProvider(localizationManager) { localization ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo Container
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_restaurant),
                            contentDescription = "Restaurant Logo",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // App Name
                Text(
                    text = localization.getString("app_name"),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtitle
                Text(
                    text = "Point of Sale System",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Loading Indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(textAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Loading Text
                Text(
                    text = localization.getString("loading"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.alpha(textAlpha)
                )
            }
            
            // Version info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .alpha(textAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "© 2024 Restaurant POS",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Animated logo component
 */
@Composable
private fun AnimatedLogo(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_restaurant),
                contentDescription = "Restaurant Logo",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Animated text component
 */
@Composable
private fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge,
    color: Color = Color.White,
    isVisible: Boolean = true,
    delay: Int = 0
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "text_alpha"
    )
    
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "text_offset"
    )
    
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
            .alpha(alpha)
            .offset(y = offsetY.dp)
    )
}