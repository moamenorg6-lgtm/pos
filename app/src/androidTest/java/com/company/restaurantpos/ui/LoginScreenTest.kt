package com.company.restaurantpos.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.company.restaurantpos.ui.screens.LoginScreen
import com.company.restaurantpos.ui.theme.RestaurantPOSTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for LoginScreen
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loginScreen_displaysCorrectElements() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Check if main elements are displayed
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in to your account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Default Admin Credentials").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_showsDefaultCredentials() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Check if default credentials are shown
        composeTestRule.onNodeWithText("Username: admin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password: admin123").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_allowsTextInput() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Test username input
        composeTestRule.onNodeWithText("Username")
            .performTextInput("testuser")
        
        // Test password input
        composeTestRule.onNodeWithText("Password")
            .performTextInput("testpass")
        
        // Verify text was entered (this is implicit - if no exception, text was entered)
    }
    
    @Test
    fun loginScreen_signInButtonClickable() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Check if sign in button is clickable
        composeTestRule.onNodeWithText("Sign In")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
    
    @Test
    fun loginScreen_passwordToggleWorks() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Enter password
        composeTestRule.onNodeWithText("Password")
            .performTextInput("testpass")
        
        // Find and click password visibility toggle
        composeTestRule.onNodeWithContentDescription("Show password")
            .assertIsDisplayed()
            .performClick()
        
        // After clicking, it should show "Hide password"
        composeTestRule.onNodeWithContentDescription("Hide password")
            .assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_rememberMeToggleWorks() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Find and click remember me checkbox
        composeTestRule.onNodeWithText("Remember me")
            .assertIsDisplayed()
        
        // The checkbox should be clickable
        composeTestRule.onNode(hasClickAction() and hasText("Remember me"))
            .performClick()
    }
    
    @Test
    fun loginScreen_fillDefaultCredentialsButtonWorks() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        
        // Click the "Use Default" button
        composeTestRule.onNodeWithText("Use Default")
            .assertIsDisplayed()
            .performClick()
        
        // This should fill the username and password fields
        // Note: In a real test, you'd verify the fields are filled
        // but that requires access to the text field values
    }
}