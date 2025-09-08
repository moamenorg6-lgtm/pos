package com.company.restaurantpos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.restaurantpos.ui.viewmodels.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Reports",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Date Range Selector
        DateRangeSelector(
            selectedRange = uiState.selectedDateRange,
            onRangeSelected = viewModel::setDateRange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Row
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf(
            TabItem("Daily Sales", Icons.Default.Assessment),
            TabItem("Top Products", Icons.Default.TrendingUp),
            TabItem("Low Stock", Icons.Default.Inventory)
        )
        
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon, contentDescription = tab.title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        when (selectedTabIndex) {
            0 -> DailySalesTab(uiState)
            1 -> TopProductsTab(uiState)
            2 -> LowStockTab(uiState)
        }
    }
}

@Composable
private fun DateRangeSelector(
    selectedRange: DateRange,
    onRangeSelected: (DateRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DateRange.values().forEach { range ->
            FilterChip(
                onClick = { onRangeSelected(range) },
                label = { Text(range.displayName) },
                selected = selectedRange == range,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DailySalesTab(uiState: ReportsUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sales Summary Card
        item {
            SalesSummaryCard(uiState.dailySalesSummary)
        }
        
        // Order Status Breakdown
        item {
            OrderStatusCard(uiState.orderStatusCounts)
        }
        
        // Order Type Breakdown
        item {
            OrderTypeCard(uiState.orderTypeStats)
        }
        
        // Payment Methods
        item {
            PaymentMethodsCard(uiState.paymentMethodStats)
        }
    }
}

@Composable
private fun TopProductsTab(uiState: ReportsUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Top Selling Products",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(uiState.topSellingProducts) { product ->
            TopProductCard(product)
        }
        
        if (uiState.topSellingProducts.isEmpty()) {
            item {
                EmptyStateCard("No sales data available for the selected period")
            }
        }
    }
}

@Composable
private fun LowStockTab(uiState: ReportsUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Low Stock Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(uiState.lowStockItems) { ingredient ->
            LowStockCard(ingredient)
        }
        
        if (uiState.lowStockItems.isEmpty()) {
            item {
                EmptyStateCard("All ingredients are well stocked!")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesSummaryCard(summary: DailySalesSummaryUi?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sales Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (summary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem("Total Orders", summary.totalOrders.toString())
                    SummaryItem("Revenue", "$${String.format("%.2f", summary.totalRevenue)}")
                    SummaryItem("Avg Order", "$${String.format("%.2f", summary.averageOrderValue)}")
                }
            } else {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderStatusCard(statusCounts: List<OrderStatusCountUi>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (statusCounts.isNotEmpty()) {
                statusCounts.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = status.status.uppercase(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = status.count.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Text(
                    text = "No orders found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderTypeCard(typeStats: List<OrderTypeStatsUi>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Types",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (typeStats.isNotEmpty()) {
                typeStats.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = type.type.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${type.count} orders",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$${String.format("%.2f", type.revenue)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "No orders found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodsCard(paymentStats: List<PaymentMethodStatsUi>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Payment Methods",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (paymentStats.isNotEmpty()) {
                paymentStats.forEach { payment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = payment.method.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${payment.count} transactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$${String.format("%.2f", payment.total)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "No payments found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopProductCard(product: TopSellingProductUi) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.nameEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${product.totalQty.toInt()} sold",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$${String.format("%.2f", product.totalRevenue)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LowStockCard(ingredient: LowStockItemUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingredient.nameEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = ingredient.unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "${ingredient.stock} remaining",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Data classes for UI state
data class TabItem(
    val title: String,
    val icon: ImageVector
)

enum class DateRange(val displayName: String, val days: Int) {
    TODAY("Today", 1),
    WEEK("This Week", 7),
    MONTH("This Month", 30),
    QUARTER("3 Months", 90)
}

// UI State data classes
data class ReportsUiState(
    val selectedDateRange: DateRange = DateRange.TODAY,
    val isLoading: Boolean = false,
    val dailySalesSummary: DailySalesSummaryUi? = null,
    val orderStatusCounts: List<OrderStatusCountUi> = emptyList(),
    val orderTypeStats: List<OrderTypeStatsUi> = emptyList(),
    val paymentMethodStats: List<PaymentMethodStatsUi> = emptyList(),
    val topSellingProducts: List<TopSellingProductUi> = emptyList(),
    val lowStockItems: List<LowStockItemUi> = emptyList()
)

data class DailySalesSummaryUi(
    val totalOrders: Int,
    val totalRevenue: Double,
    val averageOrderValue: Double
)

data class OrderStatusCountUi(
    val status: String,
    val count: Int
)

data class OrderTypeStatsUi(
    val type: String,
    val count: Int,
    val revenue: Double
)

data class PaymentMethodStatsUi(
    val method: String,
    val total: Double,
    val count: Int
)

data class TopSellingProductUi(
    val productId: Int,
    val nameEn: String,
    val nameAr: String,
    val category: String,
    val totalQty: Double,
    val totalRevenue: Double,
    val averagePrice: Double
)

data class LowStockItemUi(
    val id: Int,
    val nameEn: String,
    val nameAr: String,
    val stock: Double,
    val unit: String
)