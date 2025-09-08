package com.company.restaurantpos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.restaurantpos.R
import com.company.restaurantpos.ui.components.CartItemRow
import com.company.restaurantpos.ui.components.CheckoutDialog
import com.company.restaurantpos.ui.components.ProductCard
import com.company.restaurantpos.ui.models.OrderType
import com.company.restaurantpos.ui.models.PaymentMethod
import com.company.restaurantpos.ui.viewmodels.POSViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSScreen(
    viewModel: POSViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val decimalFormat = DecimalFormat("#.##")
    
    // Show error snackbar
    LaunchedEffect(state.error) {
        if (state.error != null) {
            // In a real app, you'd show a snackbar here
            // For now, we'll just clear it after a delay
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Left side - Products
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text(stringResource(R.string.search_products)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onSearchQueryChanged("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Products grid
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.products) { product ->
                        ProductCard(
                            product = product,
                            onAddToCart = { viewModel.addToCart(product) }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Right side - Cart and Customer
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Customer section
            CustomerSection(
                customerPhone = state.customerPhone,
                customerName = state.customerName,
                customerAddress = state.customerAddress,
                selectedCustomer = state.selectedCustomer,
                showCreateCustomer = state.showCreateCustomer,
                onPhoneChanged = viewModel::onCustomerPhoneChanged,
                onNameChanged = viewModel::onCustomerNameChanged,
                onAddressChanged = viewModel::onCustomerAddressChanged,
                onCreateCustomer = viewModel::createCustomer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cart section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.cart),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Badge {
                            Text(text = state.cartItemCount.toString())
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (state.cartItems.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_cart),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.cartItems) { cartItem ->
                                CartItemRow(
                                    cartItem = cartItem,
                                    onQuantityChanged = { quantity ->
                                        viewModel.updateCartItemQuantity(cartItem.product.id, quantity)
                                    },
                                    onNotesChanged = { notes ->
                                        viewModel.updateCartItemNotes(cartItem.product.id, notes)
                                    },
                                    onRemove = {
                                        viewModel.removeFromCart(cartItem.product.id)
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Order summary
                        OrderSummarySection(
                            subtotal = state.subtotal,
                            tax = state.taxAmount,
                            discount = state.discount,
                            total = state.total,
                            onDiscountChanged = viewModel::onDiscountChanged,
                            decimalFormat = decimalFormat
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Test Print button
                        OutlinedButton(
                            onClick = viewModel::testPrint,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Print")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Checkout button
                        Button(
                            onClick = viewModel::showCheckoutDialog,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.cartItems.isNotEmpty() && !state.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.checkout))
                        }
                    }
                }
            }
        }
    }
    
    // Checkout dialog
    if (state.showCheckoutDialog) {
        CheckoutDialog(
            subtotal = state.subtotal,
            tax = state.taxAmount,
            discount = state.discount,
            total = state.total,
            orderType = state.orderType,
            paymentMethod = state.paymentMethod,
            onOrderTypeChanged = viewModel::onOrderTypeChanged,
            onPaymentMethodChanged = viewModel::onPaymentMethodChanged,
            onConfirm = viewModel::confirmOrder,
            onDismiss = viewModel::hideCheckoutDialog,
            isLoading = state.isLoading
        )
    }
    
    // Error display
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message
            // In a real app, you'd use SnackbarHost here
        }
    }
}

@Composable
private fun CustomerSection(
    customerPhone: String,
    customerName: String,
    customerAddress: String,
    selectedCustomer: com.company.restaurantpos.data.local.entities.Customer?,
    showCreateCustomer: Boolean,
    onPhoneChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onCreateCustomer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Customer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = customerPhone,
                onValueChange = onPhoneChanged,
                label = { Text(stringResource(R.string.customer_phone)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            if (selectedCustomer != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.customer_found),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = selectedCustomer.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (selectedCustomer.address.isNotEmpty()) {
                            Text(
                                text = selectedCustomer.address,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else if (showCreateCustomer && customerPhone.length >= 3) {
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = customerName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.customer_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = onAddressChanged,
                    label = { Text(stringResource(R.string.customer_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onCreateCustomer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.create_customer))
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(
    subtotal: Double,
    tax: Double,
    discount: Double,
    total: Double,
    onDiscountChanged: (Double) -> Unit,
    decimalFormat: DecimalFormat
) {
    var discountText by remember { mutableStateOf(discount.toString()) }
    
    Column {
        Divider()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.subtotal))
            Text("$${decimalFormat.format(subtotal)}")
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.tax))
            Text("$${decimalFormat.format(tax)}")
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.discount))
            
            OutlinedTextField(
                value = discountText,
                onValueChange = { newValue ->
                    discountText = newValue
                    newValue.toDoubleOrNull()?.let { discount ->
                        onDiscountChanged(discount)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.width(80.dp),
                singleLine = true,
                prefix = { Text("$") }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Divider()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$${decimalFormat.format(total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}