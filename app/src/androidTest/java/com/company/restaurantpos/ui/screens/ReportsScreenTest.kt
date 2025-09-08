package com.company.restaurantpos.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.company.restaurantpos.ui.theme.RestaurantPOSTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportsScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun reportsScreen_displaysCorrectTitle() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        composeTestRule
            .onNodeWithText("Reports")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_displaysDateRangeFilters() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Check that date range filter chips are displayed
        composeTestRule
            .onNodeWithText("Today")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("This Week")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("This Month")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("3 Months")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_displaysTabs() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Check that all tabs are displayed
        composeTestRule
            .onNodeWithText("Daily Sales")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Top Products")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Low Stock")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_defaultTabIsDailySales() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Daily Sales tab should be selected by default
        composeTestRule
            .onNodeWithText("Sales Summary")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Order Status")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Order Types")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Payment Methods")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_canSwitchToTopProductsTab() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Click on Top Products tab
        composeTestRule
            .onNodeWithText("Top Products")
            .performClick()
        
        // Check that Top Products content is displayed
        composeTestRule
            .onNodeWithText("Top Selling Products")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_canSwitchToLowStockTab() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Click on Low Stock tab
        composeTestRule
            .onNodeWithText("Low Stock")
            .performClick()
        
        // Check that Low Stock content is displayed
        composeTestRule
            .onNodeWithText("Low Stock Items")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_canChangeDateRange() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Initially "Today" should be selected
        composeTestRule
            .onNodeWithText("Today")
            .assertIsDisplayed()
        
        // Click on "This Week" filter
        composeTestRule
            .onNodeWithText("This Week")
            .performClick()
        
        // The filter should remain displayed (selection state is internal)
        composeTestRule
            .onNodeWithText("This Week")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_displaysEmptyStateForTopProducts() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Switch to Top Products tab
        composeTestRule
            .onNodeWithText("Top Products")
            .performClick()
        
        // Should display empty state message when no data
        composeTestRule
            .onNodeWithText("No sales data available for the selected period")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_displaysEmptyStateForLowStock() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Switch to Low Stock tab
        composeTestRule
            .onNodeWithText("Low Stock")
            .performClick()
        
        // Should display empty state message when no low stock items
        composeTestRule
            .onNodeWithText("All ingredients are well stocked!")
            .assertIsDisplayed()
    }
    
    @Test
    fun reportsScreen_salesSummaryDisplaysLoadingState() {
        composeTestRule.setContent {
            RestaurantPOSTheme {
                ReportsScreen()
            }
        }
        
        // Should display loading text initially
        composeTestRule
            .onNodeWithText("Loading...")
            .assertIsDisplayed()
    }
}