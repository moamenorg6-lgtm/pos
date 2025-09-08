package com.company.restaurantpos.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.company.restaurantpos.ui.screens.POSScreen
import com.company.restaurantpos.ui.theme.RestaurantPOSTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class POSScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun posScreen_displaysSearchBar() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                POSScreen()
            }
        }
        
        // Check if search bar is displayed
        composeTestRule
            .onNodeWithText("Search products by name or SKU")
            .assertIsDisplayed()
    }
    
    @Test
    fun posScreen_displaysCartSection() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                POSScreen()
            }
        }
        
        // Check if cart section is displayed
        composeTestRule
            .onNodeWithText("Cart")
            .assertIsDisplayed()
        
        // Check if empty cart message is displayed
        composeTestRule
            .onNodeWithText("Your cart is empty")
            .assertIsDisplayed()
    }
    
    @Test
    fun posScreen_displaysCustomerSection() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                POSScreen()
            }
        }
        
        // Check if customer section is displayed
        composeTestRule
            .onNodeWithText("Customer")
            .assertIsDisplayed()
        
        // Check if customer phone field is displayed
        composeTestRule
            .onNodeWithText("Customer Phone")
            .assertIsDisplayed()
    }
    
    @Test
    fun posScreen_searchBarAcceptsInput() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                POSScreen()
            }
        }
        
        val searchQuery = "burger"
        
        // Type in search bar
        composeTestRule
            .onNodeWithText("Search products by name or SKU")
            .performTextInput(searchQuery)
        
        // Verify text was entered
        composeTestRule
            .onNodeWithText(searchQuery)
            .assertIsDisplayed()
    }
    
    @Test
    fun posScreen_customerPhoneFieldAcceptsInput() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                POSScreen()
            }
        }
        
        val phoneNumber = "1234567890"
        
        // Type in customer phone field
        composeTestRule
            .onNodeWithText("Customer Phone")
            .performTextInput(phoneNumber)
        
        // Verify text was entered
        composeTestRule
            .onNodeWithText(phoneNumber)
            .assertIsDisplayed()
    }
}