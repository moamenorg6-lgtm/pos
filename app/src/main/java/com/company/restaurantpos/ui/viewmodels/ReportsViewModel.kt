package com.company.restaurantpos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.restaurantpos.data.local.daos.*
import com.company.restaurantpos.data.local.entities.Ingredient
import com.company.restaurantpos.ui.screens.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val paymentDao: PaymentDao,
    private val ingredientDao: IngredientDao
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val LOW_STOCK_THRESHOLD = 10.0
        private const val TOP_PRODUCTS_LIMIT = 10
    }
    
    init {
        loadReportsData()
    }
    
    /**
     * Set the date range for reports and reload data
     */
    fun setDateRange(dateRange: DateRange) {
        _uiState.value = _uiState.value.copy(selectedDateRange = dateRange)
        loadReportsData()
    }
    
    /**
     * Load all reports data based on selected date range
     */
    private fun loadReportsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val (startTime, endTime) = getDateRangeTimestamps(_uiState.value.selectedDateRange)
            
            try {
                // Load all reports data concurrently
                val dailySalesSummary = loadDailySalesSummary(startTime, endTime)
                val orderStatusCounts = loadOrderStatusCounts(startTime, endTime)
                val orderTypeStats = loadOrderTypeStats(startTime, endTime)
                val paymentMethodStats = loadPaymentMethodStats(startTime, endTime)
                val topSellingProducts = loadTopSellingProducts(startTime, endTime)
                val lowStockItems = loadLowStockItems()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dailySalesSummary = dailySalesSummary,
                    orderStatusCounts = orderStatusCounts,
                    orderTypeStats = orderTypeStats,
                    paymentMethodStats = paymentMethodStats,
                    topSellingProducts = topSellingProducts,
                    lowStockItems = lowStockItems
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                // TODO: Handle error state
            }
        }
    }
    
    /**
     * Load daily sales summary
     */
    private suspend fun loadDailySalesSummary(startTime: Long, endTime: Long): DailySalesSummaryUi? {
        return try {
            val summary = orderDao.getDailySalesSummary(startTime, endTime)
            DailySalesSummaryUi(
                totalOrders = summary.totalOrders,
                totalRevenue = summary.totalRevenue,
                averageOrderValue = summary.averageOrderValue
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Load order status counts
     */
    private suspend fun loadOrderStatusCounts(startTime: Long, endTime: Long): List<OrderStatusCountUi> {
        return try {
            orderDao.getOrderStatusCounts(startTime, endTime).map { statusCount ->
                OrderStatusCountUi(
                    status = statusCount.status,
                    count = statusCount.count
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Load order type statistics
     */
    private suspend fun loadOrderTypeStats(startTime: Long, endTime: Long): List<OrderTypeStatsUi> {
        return try {
            orderDao.getOrderTypeCounts(startTime, endTime).map { typeStats ->
                OrderTypeStatsUi(
                    type = typeStats.type,
                    count = typeStats.count,
                    revenue = typeStats.revenue
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Load payment method statistics
     */
    private suspend fun loadPaymentMethodStats(startTime: Long, endTime: Long): List<PaymentMethodStatsUi> {
        return try {
            paymentDao.getPaymentMethodStats(startTime, endTime).map { paymentStats ->
                PaymentMethodStatsUi(
                    method = paymentStats.method,
                    total = paymentStats.total,
                    count = paymentStats.count
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Load top selling products
     */
    private suspend fun loadTopSellingProducts(startTime: Long, endTime: Long): List<TopSellingProductUi> {
        return try {
            orderItemDao.getTopSellingProductsWithDetails(startTime, endTime, TOP_PRODUCTS_LIMIT).map { product ->
                TopSellingProductUi(
                    productId = product.productId,
                    nameEn = product.nameEn,
                    nameAr = product.nameAr,
                    category = product.category,
                    totalQty = product.totalQty,
                    totalRevenue = product.totalRevenue,
                    averagePrice = product.averagePrice
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Load low stock items
     */
    private suspend fun loadLowStockItems(): List<LowStockItemUi> {
        return try {
            ingredientDao.getLowStock(LOW_STOCK_THRESHOLD).map { ingredient ->
                LowStockItemUi(
                    id = ingredient.id,
                    nameEn = ingredient.nameEn,
                    nameAr = ingredient.nameAr,
                    stock = ingredient.stock,
                    unit = ingredient.unit
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get start and end timestamps for the selected date range
     */
    private fun getDateRangeTimestamps(dateRange: DateRange): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        when (dateRange) {
            DateRange.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            DateRange.WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
            DateRange.MONTH -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
            }
            DateRange.QUARTER -> {
                calendar.add(Calendar.DAY_OF_YEAR, -90)
            }
        }
        
        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }
    
    /**
     * Refresh reports data
     */
    fun refreshData() {
        loadReportsData()
    }
}