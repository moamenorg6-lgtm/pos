package com.company.restaurantpos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.company.restaurantpos.R
import com.company.restaurantpos.data.local.entities.Product
import java.text.DecimalFormat

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("#.##")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product name
            Text(
                text = product.nameEn, // TODO: Add locale-based selection
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Category and SKU
            Text(
                text = "${product.category} • ${product.sku}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price and stock info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${decimalFormat.format(product.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (!product.isRecipe) {
                        Text(
                            text = "Stock: ${decimalFormat.format(product.stock)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (product.stock > 10) 
                                MaterialTheme.colorScheme.onSurfaceVariant 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Recipe Item",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                
                // Add to cart button
                FilledTonalButton(
                    onClick = onAddToCart,
                    enabled = product.isActive && (product.isRecipe || product.stock > 0)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_to_cart))
                }
            }
        }
    }
}