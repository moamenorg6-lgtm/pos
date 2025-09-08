package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.entities.Order
import com.company.restaurantpos.data.local.entities.OrderItem
import com.company.restaurantpos.data.local.entities.Product
import com.company.restaurantpos.data.local.entities.Customer
import com.company.restaurantpos.data.local.entities.Payment

/**
 * ReceiptTemplate handles the formatting and generation of customer receipts
 * Provides structured receipt content with proper formatting for thermal printers
 */
object ReceiptTemplate {
    
    // ESC/POS Commands
    private const val ESC = "\u001B"
    private const val INIT = "$ESC@"
    private const val BOLD_ON = "$ESC[1m"
    private const val BOLD_OFF = "$ESC[0m"
    private const val CENTER = "$ESC[1m"
    private const val LEFT = "$ESC[0m"
    private const val CUT_PAPER = "$ESC[m"
    private const val LINE_FEED = "\n"
    private const val DOUBLE_LINE_FEED = "\n\n"
    
    // Receipt configuration
    private const val RECEIPT_WIDTH = 32
    private const val TAX_RATE = 0.10 // 10% tax
    
    /**
     * Generate complete customer receipt
     */
    fun generateReceipt(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>,
        customer: Customer? = null,
        payments: List<Payment> = emptyList(),
        restaurantInfo: RestaurantInfo = RestaurantInfo()
    ): String {
        val receipt = StringBuilder()
        
        receipt.append(INIT)
        receipt.append(generateHeader(restaurantInfo))
        receipt.append(generateOrderInfo(order, customer))
        receipt.append(generateItemsList(orderItems))
        receipt.append(generateTotals(orderItems))
        receipt.append(generatePayments(payments))
        receipt.append(generateFooter())
        receipt.append(CUT_PAPER)
        
        return receipt.toString()
    }
    
    /**
     * Generate receipt header with restaurant information
     */
    private fun generateHeader(restaurantInfo: RestaurantInfo): String {
        val header = StringBuilder()
        
        header.append(CENTER)
        header.append(BOLD_ON)
        header.append(restaurantInfo.name)
        header.append(BOLD_OFF)
        header.append(LINE_FEED)
        header.append(restaurantInfo.phone)
        header.append(LINE_FEED)
        header.append(restaurantInfo.address)
        header.append(LINE_FEED)
        header.append(LEFT)
        header.append("=".repeat(RECEIPT_WIDTH))
        header.append(LINE_FEED)
        
        return header.toString()
    }
    
    /**
     * Generate order information section
     */
    private fun generateOrderInfo(order: Order, customer: Customer?): String {
        val info = StringBuilder()
        
        info.append("Order No: ${order.orderNo}")
        info.append(LINE_FEED)
        info.append("Date: ${formatTimestamp(order.createdAt)}")
        info.append(LINE_FEED)
        info.append("Type: ${order.type.uppercase()}")
        info.append(LINE_FEED)
        
        customer?.let {
            info.append("Customer: ${it.name}")
            info.append(LINE_FEED)
            info.append("Phone: ${it.phone}")
            info.append(LINE_FEED)
            if (order.type == "delivery" && it.address.isNotBlank()) {
                info.append("Address: ${it.address}")
                info.append(LINE_FEED)
            }
        }
        
        info.append("=".repeat(RECEIPT_WIDTH))
        info.append(LINE_FEED)
        
        return info.toString()
    }
    
    /**
     * Generate items list section
     */
    private fun generateItemsList(orderItems: List<Pair<OrderItem, Product>>): String {
        val items = StringBuilder()
        
        items.append(BOLD_ON)
        items.append(formatLine("Item", "Qty", "Price", "Total"))
        items.append(BOLD_OFF)
        items.append(LINE_FEED)
        items.append("-".repeat(RECEIPT_WIDTH))
        items.append(LINE_FEED)
        
        orderItems.forEach { (orderItem, product) ->
            val itemTotal = orderItem.qty * orderItem.price
            
            items.append(formatLine(
                product.nameEn.take(12),
                formatQuantity(orderItem.qty),
                "$${String.format("%.2f", orderItem.price)}",
                "$${String.format("%.2f", itemTotal)}"
            ))
            items.append(LINE_FEED)
            
            // Add notes if present
            orderItem.notes?.let { notes ->
                items.append("  * $notes")
                items.append(LINE_FEED)
            }
        }
        
        items.append("-".repeat(RECEIPT_WIDTH))
        items.append(LINE_FEED)
        
        return items.toString()
    }
    
    /**
     * Generate totals section
     */
    private fun generateTotals(orderItems: List<Pair<OrderItem, Product>>): String {
        val totals = StringBuilder()
        
        val subtotal = calculateSubtotal(orderItems)
        val tax = subtotal * TAX_RATE
        val discount = 0.0 // No discount implementation yet
        val grandTotal = subtotal + tax - discount
        
        totals.append(formatTotalLine("Subtotal:", "$${String.format("%.2f", subtotal)}"))
        totals.append(LINE_FEED)
        totals.append(formatTotalLine("Tax (${(TAX_RATE * 100).toInt()}%):", "$${String.format("%.2f", tax)}"))
        totals.append(LINE_FEED)
        
        if (discount > 0) {
            totals.append(formatTotalLine("Discount:", "-$${String.format("%.2f", discount)}"))
            totals.append(LINE_FEED)
        }
        
        totals.append(BOLD_ON)
        totals.append(formatTotalLine("TOTAL:", "$${String.format("%.2f", grandTotal)}"))
        totals.append(BOLD_OFF)
        totals.append(LINE_FEED)
        
        return totals.toString()
    }
    
    /**
     * Generate payments section
     */
    private fun generatePayments(payments: List<Payment>): String {
        if (payments.isEmpty()) return ""
        
        val paymentsSection = StringBuilder()
        
        paymentsSection.append("=".repeat(RECEIPT_WIDTH))
        paymentsSection.append(LINE_FEED)
        paymentsSection.append(BOLD_ON)
        paymentsSection.append("PAYMENTS:")
        paymentsSection.append(BOLD_OFF)
        paymentsSection.append(LINE_FEED)
        
        payments.forEach { payment ->
            paymentsSection.append(formatTotalLine(
                "${payment.method.uppercase()}:",
                "$${String.format("%.2f", payment.amount)}"
            ))
            paymentsSection.append(LINE_FEED)
        }
        
        val totalPaid = payments.sumOf { it.amount }
        paymentsSection.append(formatTotalLine("PAID:", "$${String.format("%.2f", totalPaid)}"))
        paymentsSection.append(LINE_FEED)
        
        return paymentsSection.toString()
    }
    
    /**
     * Generate receipt footer
     */
    private fun generateFooter(): String {
        val footer = StringBuilder()
        
        footer.append("=".repeat(RECEIPT_WIDTH))
        footer.append(LINE_FEED)
        footer.append(CENTER)
        footer.append("Thank you for your visit!")
        footer.append(LINE_FEED)
        footer.append("شكراً لزيارتكم!")
        footer.append(LINE_FEED)
        footer.append("Visit us again soon!")
        footer.append(DOUBLE_LINE_FEED)
        footer.append(LEFT)
        
        return footer.toString()
    }
    
    /**
     * Calculate subtotal from order items
     */
    private fun calculateSubtotal(orderItems: List<Pair<OrderItem, Product>>): Double {
        return orderItems.sumOf { (orderItem, _) ->
            orderItem.qty * orderItem.price
        }
    }
    
    /**
     * Format a line with columns
     */
    private fun formatLine(col1: String, col2: String, col3: String, col4: String): String {
        val c1 = col1.take(12).padEnd(12)
        val c2 = col2.take(4).padStart(4)
        val c3 = col3.take(6).padStart(6)
        val c4 = col4.take(8).padStart(8)
        return "$c1$c2$c3$c4"
    }
    
    /**
     * Format a total line (right-aligned)
     */
    private fun formatTotalLine(label: String, amount: String): String {
        val combined = "$label $amount"
        return combined.padStart(RECEIPT_WIDTH)
    }
    
    /**
     * Format quantity for display
     */
    private fun formatQuantity(qty: Double): String {
        return if (qty == qty.toInt().toDouble()) {
            qty.toInt().toString()
        } else {
            String.format("%.1f", qty)
        }
    }
    
    /**
     * Format timestamp to readable date/time
     */
    private fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }
    
    /**
     * Restaurant information data class
     */
    data class RestaurantInfo(
        val name: String = "RESTAURANT POS",
        val phone: String = "Phone: +1-234-567-8900",
        val address: String = "123 Main St, City, State"
    )
}