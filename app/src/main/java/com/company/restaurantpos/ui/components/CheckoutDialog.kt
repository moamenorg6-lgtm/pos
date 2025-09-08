package com.company.restaurantpos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.company.restaurantpos.R
import com.company.restaurantpos.ui.models.OrderType
import com.company.restaurantpos.ui.models.PaymentMethod
import java.text.DecimalFormat

@Composable
fun CheckoutDialog(
    subtotal: Double,
    tax: Double,
    discount: Double,
    total: Double,
    orderType: OrderType,
    paymentMethod: PaymentMethod,
    onOrderTypeChanged: (OrderType) -> Unit,
    onPaymentMethodChanged: (PaymentMethod) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    val decimalFormat = DecimalFormat("#.##")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Title
                Text(
                    text = stringResource(R.string.checkout),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Order summary
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OrderSummaryRow(
                            label = stringResource(R.string.subtotal),
                            amount = subtotal,
                            decimalFormat = decimalFormat
                        )
                        
                        OrderSummaryRow(
                            label = stringResource(R.string.tax),
                            amount = tax,
                            decimalFormat = decimalFormat
                        )
                        
                        if (discount > 0) {
                            OrderSummaryRow(
                                label = stringResource(R.string.discount),
                                amount = -discount,
                                decimalFormat = decimalFormat,
                                isDiscount = true
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        OrderSummaryRow(
                            label = stringResource(R.string.total),
                            amount = total,
                            decimalFormat = decimalFormat,
                            isTotal = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Order type selection
                Text(
                    text = "Order Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(Modifier.selectableGroup()) {
                    OrderType.values().forEach { type ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (type == orderType),
                                    onClick = { onOrderTypeChanged(type) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (type == orderType),
                                onClick = null
                            )
                            Text(
                                text = when (type) {
                                    OrderType.DINE_IN -> stringResource(R.string.dine_in)
                                    OrderType.TAKEAWAY -> stringResource(R.string.takeaway)
                                    OrderType.DELIVERY -> stringResource(R.string.delivery)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Payment method selection
                Text(
                    text = stringResource(R.string.payment_method),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(Modifier.selectableGroup()) {
                    PaymentMethod.values().forEach { method ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (method == paymentMethod),
                                    onClick = { onPaymentMethodChanged(method) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (method == paymentMethod),
                                onClick = null
                            )
                            Text(
                                text = when (method) {
                                    PaymentMethod.CASH -> stringResource(R.string.cash)
                                    PaymentMethod.CARD -> stringResource(R.string.card)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.confirm_order))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryRow(
    label: String,
    amount: Double,
    decimalFormat: DecimalFormat,
    isDiscount: Boolean = false,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        
        Text(
            text = "${if (isDiscount) "-" else ""}$${decimalFormat.format(kotlin.math.abs(amount))}",
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isDiscount -> MaterialTheme.colorScheme.tertiary
                isTotal -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}