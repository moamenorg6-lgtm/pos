package com.company.restaurantpos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.restaurantpos.business.CustomerAutoFillManager
import com.company.restaurantpos.business.OrderManager
import com.company.restaurantpos.business.PrintManager
import com.company.restaurantpos.business.RecipeManager
import com.company.restaurantpos.data.local.daos.ProductDao
import com.company.restaurantpos.data.local.entities.Customer
import com.company.restaurantpos.data.local.entities.Product
import com.company.restaurantpos.ui.models.CartItem
import com.company.restaurantpos.ui.models.OrderType
import com.company.restaurantpos.ui.models.POSState
import com.company.restaurantpos.ui.models.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class POSViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val customerAutoFillManager: CustomerAutoFillManager,
    private val recipeManager: RecipeManager,
    private val orderManager: OrderManager,
    private val printManager: PrintManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(POSState())
    val state: StateFlow<POSState> = _state.asStateFlow()
    
    private var searchJob: Job? = null
    private var customerSearchJob: Job? = null
    
    init {
        loadProducts()
    }
    
    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchProducts(query)
    }
    
    fun onCustomerPhoneChanged(phone: String) {
        _state.value = _state.value.copy(
            customerPhone = phone,
            selectedCustomer = null,
            showCreateCustomer = false
        )
        
        if (phone.length >= 3) {
            searchCustomer(phone)
        }
    }
    
    fun onCustomerNameChanged(name: String) {
        _state.value = _state.value.copy(customerName = name)
    }
    
    fun onCustomerAddressChanged(address: String) {
        _state.value = _state.value.copy(customerAddress = address)
    }
    
    fun onDiscountChanged(discount: Double) {
        _state.value = _state.value.copy(discount = discount.coerceAtLeast(0.0))
    }
    
    fun onOrderTypeChanged(orderType: OrderType) {
        _state.value = _state.value.copy(orderType = orderType)
    }
    
    fun onPaymentMethodChanged(paymentMethod: PaymentMethod) {
        _state.value = _state.value.copy(paymentMethod = paymentMethod)
    }
    
    fun addToCart(product: Product) {
        val currentCart = _state.value.cartItems.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.product.id == product.id }
        
        if (existingItemIndex >= 0) {
            // Update quantity of existing item
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Add new item
            currentCart.add(CartItem(product = product))
        }
        
        _state.value = _state.value.copy(cartItems = currentCart)
    }
    
    fun removeFromCart(productId: Long) {
        val currentCart = _state.value.cartItems.toMutableList()
        currentCart.removeAll { it.product.id == productId }
        _state.value = _state.value.copy(cartItems = currentCart)
    }
    
    fun updateCartItemQuantity(productId: Long, quantity: Double) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        
        val currentCart = _state.value.cartItems.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
        
        if (itemIndex >= 0) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = quantity)
            _state.value = _state.value.copy(cartItems = currentCart)
        }
    }
    
    fun updateCartItemNotes(productId: Long, notes: String) {
        val currentCart = _state.value.cartItems.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
        
        if (itemIndex >= 0) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(notes = notes)
            _state.value = _state.value.copy(cartItems = currentCart)
        }
    }
    
    fun showCheckoutDialog() {
        if (_state.value.cartItems.isEmpty()) {
            _state.value = _state.value.copy(error = "Cart is empty")
            return
        }
        
        _state.value = _state.value.copy(showCheckoutDialog = true)
    }
    
    fun hideCheckoutDialog() {
        _state.value = _state.value.copy(showCheckoutDialog = false)
    }
    
    fun createCustomer() {
        val currentState = _state.value
        val validation = customerAutoFillManager.validateCustomerData(
            currentState.customerName,
            currentState.customerPhone
        )
        
        if (!validation.isValid) {
            _state.value = _state.value.copy(error = validation.errors.joinToString(", "))
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val customer = customerAutoFillManager.createCustomer(
                name = currentState.customerName,
                phone = currentState.customerPhone,
                address = currentState.customerAddress
            )
            
            if (customer != null) {
                _state.value = _state.value.copy(
                    selectedCustomer = customer,
                    showCreateCustomer = false,
                    isLoading = false,
                    error = null
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to create customer"
                )
            }
        }
    }
    
    fun confirmOrder() {
        val currentState = _state.value
        
        if (currentState.cartItems.isEmpty()) {
            _state.value = _state.value.copy(error = "Cart is empty")
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Validate stock first
            val stockValidation = recipeManager.validateStock(currentState.cartItems)
            if (!stockValidation.isValid) {
                val errorMessage = "Insufficient stock for: " + 
                    stockValidation.insufficientIngredients.joinToString(", ") { 
                        "${it.nameEn} (Required: ${it.required}, Available: ${it.available})"
                    }
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                return@launch
            }
            
            // Create order
            val orderId = orderManager.createOrder(
                cartItems = currentState.cartItems,
                customerId = currentState.selectedCustomer?.id,
                orderType = currentState.orderType,
                paymentMethod = currentState.paymentMethod,
                subtotal = currentState.subtotal,
                taxAmount = currentState.taxAmount,
                discount = currentState.discount,
                total = currentState.total
            )
            
            if (orderId != null) {
                // Print receipts after successful order creation
                try {
                    printManager.initializePrinter("STUB") // Initialize with stub for testing
                    
                    // Get order details for printing
                    val order = orderManager.getOrderById(orderId.toInt())
                    val orderItems = orderManager.getOrderItemsWithProducts(orderId.toInt())
                    
                    if (order != null && orderItems.isNotEmpty()) {
                        // Print customer receipt
                        val receiptPrinted = printManager.printCustomerReceipt(
                            order = order,
                            orderItems = orderItems,
                            customer = currentState.selectedCustomer,
                            payments = emptyList() // Payments will be added later when payment is processed
                        )
                        
                        // Print kitchen ticket
                        val kitchenTicketPrinted = printManager.printKitchenTicket(
                            order = order,
                            orderItems = orderItems
                        )
                        
                        if (receiptPrinted && kitchenTicketPrinted) {
                            _state.value = _state.value.copy(error = "Order created and printed successfully!")
                        } else {
                            _state.value = _state.value.copy(error = "Order created but printing failed")
                        }
                    } else {
                        _state.value = _state.value.copy(error = "Order created but could not retrieve details for printing")
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Order created but printing failed: ${e.message}")
                }
                
                // Clear cart and reset state
                _state.value = _state.value.copy(
                    cartItems = emptyList(),
                    customerPhone = "",
                    customerName = "",
                    customerAddress = "",
                    selectedCustomer = null,
                    showCreateCustomer = false,
                    showCheckoutDialog = false,
                    discount = 0.0,
                    isLoading = false
                )
                
                delay(3000) // Show message longer for printing feedback
                clearError()
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to create order"
                )
            }
        }
    }
    
    fun clearCart() {
        _state.value = _state.value.copy(cartItems = emptyList())
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    /**
     * Test print functionality
     */
    fun testPrint() {
        viewModelScope.launch {
            try {
                val initialized = printManager.initializePrinter("STUB")
                if (initialized) {
                    val testResult = printManager.testPrint()
                    if (testResult) {
                        _state.value = _state.value.copy(error = "Test print successful!")
                    } else {
                        _state.value = _state.value.copy(error = "Test print failed")
                    }
                } else {
                    _state.value = _state.value.copy(error = "Failed to initialize printer")
                }
                
                delay(2000)
                clearError()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Test print error: ${e.message}")
                delay(2000)
                clearError()
            }
        }
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = productDao.getAllActive()
                _state.value = _state.value.copy(products = products)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to load products")
            }
        }
    }
    
    private fun searchProducts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            
            try {
                val products = if (query.isBlank()) {
                    productDao.getAllActive()
                } else {
                    productDao.searchMultilingual(query)
                }
                _state.value = _state.value.copy(products = products)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to search products")
            }
        }
    }
    
    private fun searchCustomer(phone: String) {
        customerSearchJob?.cancel()
        customerSearchJob = viewModelScope.launch {
            delay(500) // Debounce
            
            try {
                val customer = customerAutoFillManager.searchCustomerByPhone(phone)
                if (customer != null) {
                    _state.value = _state.value.copy(
                        selectedCustomer = customer,
                        customerName = customer.name,
                        customerAddress = customer.address,
                        showCreateCustomer = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        selectedCustomer = null,
                        showCreateCustomer = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to search customer")
            }
        }
    }
}