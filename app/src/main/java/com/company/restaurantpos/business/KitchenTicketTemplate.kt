package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.entities.Order
import com.company.restaurantpos.data.local.entities.OrderItem
import com.company.restaurantpos.data.local.entities.Product

/**
 * KitchenTicketTemplate handles the formatting and generation of kitchen tickets
 * Provides structured kitchen ticket content optimized for kitchen staff workflow
 */
object KitchenTicketTemplate {
    
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
    
    // Ticket configuration
    private const val TICKET_WIDTH = 32
    
    /**
     * Generate complete kitchen ticket
     */
    fun generateKitchenTicket(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>
    ): String {
        val ticket = StringBuilder()
        
        ticket.append(INIT)
        ticket.append(generateHeader())
        ticket.append(generateOrderInfo(order))
        ticket.append(generateItemsList(orderItems))
        ticket.append(generateFooter(order))
        ticket.append(CUT_PAPER)
        
        return ticket.toString()
    }
    
    /**
     * Generate ticket header
     */
    private fun generateHeader(): String {
        val header = StringBuilder()
        
        header.append(CENTER)
        header.append(BOLD_ON)
        header.append("KITCHEN TICKET")
        header.append(BOLD_OFF)
        header.append(LINE_FEED)
        header.append(LEFT)
        header.append("=".repeat(TICKET_WIDTH))
        header.append(LINE_FEED)
        
        return header.toString()
    }
    
    /**
     * Generate order information section
     */
    private fun generateOrderInfo(order: Order): String {
        val info = StringBuilder()
        
        info.append(BOLD_ON)
        info.append("Order No: ${order.orderNo}")
        info.append(BOLD_OFF)
        info.append(LINE_FEED)
        info.append("Time: ${formatTimestamp(order.createdAt)}")
        info.append(LINE_FEED)
        info.append("Type: ${formatOrderType(order.type)}")
        info.append(LINE_FEED)
        
        // Add priority indicator for delivery orders
        if (order.type == "delivery") {
            info.append(BOLD_ON)
            info.append("*** DELIVERY ORDER ***")
            info.append(BOLD_OFF)
            info.append(LINE_FEED)
        }
        
        info.append("=".repeat(TICKET_WIDTH))
        info.append(LINE_FEED)
        
        return info.toString()
    }
    
    /**
     * Generate items list section (kitchen-focused)
     */
    private fun generateItemsList(orderItems: List<Pair<OrderItem, Product>>): String {
        val items = StringBuilder()
        
        items.append(BOLD_ON)
        items.append("ITEMS TO PREPARE:")
        items.append(BOLD_OFF)
        items.append(LINE_FEED)
        items.append("-".repeat(TICKET_WIDTH))
        items.append(LINE_FEED)
        
        // Group items by category for better kitchen workflow
        val groupedItems = orderItems.groupBy { (_, product) -> product.category }
        
        groupedItems.forEach { (category, categoryItems) ->
            // Category header
            items.append(BOLD_ON)
            items.append("${category.uppercase()}:")
            items.append(BOLD_OFF)
            items.append(LINE_FEED)
            
            categoryItems.forEach { (orderItem, product) ->
                // Item with quantity
                items.append(BOLD_ON)
                items.append("${formatQuantity(orderItem.qty)}x ${product.nameEn}")
                items.append(BOLD_OFF)
                items.append(LINE_FEED)
                
                // Arabic name if different
                if (product.nameAr.isNotBlank() && product.nameAr != product.nameEn) {
                    items.append("   (${product.nameAr})")
                    items.append(LINE_FEED)
                }
                
                // Special instructions - highlighted for kitchen attention
                orderItem.notes?.let { notes ->
                    items.append(BOLD_ON)
                    items.append("*** SPECIAL: $notes ***")
                    items.append(BOLD_OFF)
                    items.append(LINE_FEED)
                }
                
                items.append(LINE_FEED)
            }
        }
        
        items.append("-".repeat(TICKET_WIDTH))
        items.append(LINE_FEED)
        
        return items.toString()
    }
    
    /**
     * Generate ticket footer with preparation notes
     */
    private fun generateFooter(order: Order): String {
        val footer = StringBuilder()
        
        // Total item count for kitchen reference
        footer.append("Total Items: ${getTotalItemCount(order)}")
        footer.append(LINE_FEED)
        
        // Service type reminder
        when (order.type) {
            "dine_in" -> {
                footer.append("Service: DINE IN")
                footer.append(LINE_FEED)
                footer.append("Serve hot to table")
            }
            "takeaway" -> {
                footer.append("Service: TAKEAWAY")
                footer.append(LINE_FEED)
                footer.append("Pack for pickup")
            }
            "delivery" -> {
                footer.append("Service: DELIVERY")
                footer.append(LINE_FEED)
                footer.append("Pack securely for delivery")
            }
        }
        footer.append(DOUBLE_LINE_FEED)
        
        // Kitchen workflow reminder
        footer.append(CENTER)
        footer.append("Check order when complete")
        footer.append(LINE_FEED)
        footer.append("Update status to READY")
        footer.append(DOUBLE_LINE_FEED)
        footer.append(LEFT)
        
        return footer.toString()
    }
    
    /**
     * Get total item count for the order
     */
    private fun getTotalItemCount(order: Order): String {
        // This would need to be calculated from orderItems
        // For now, return a placeholder
        return "N/A"
    }
    
    /**
     * Format order type for kitchen display
     */
    private fun formatOrderType(type: String): String {
        return when (type) {
            "dine_in" -> "DINE IN"
            "takeaway" -> "TAKEAWAY"
            "delivery" -> "DELIVERY"
            else -> type.uppercase()
        }
    }
    
    /**
     * Format quantity for kitchen display
     */
    private fun formatQuantity(qty: Double): String {
        return if (qty == qty.toInt().toDouble()) {
            qty.toInt().toString()
        } else {
            String.format("%.1f", qty)
        }
    }
    
    /**
     * Format timestamp to readable time (kitchen-focused)
     */
    private fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }
    
    /**
     * Generate urgent ticket for rush orders
     */
    fun generateUrgentKitchenTicket(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>,
        urgencyReason: String = "Rush Order"
    ): String {
        val ticket = StringBuilder()
        
        ticket.append(INIT)
        
        // Urgent header
        ticket.append(CENTER)
        ticket.append(BOLD_ON)
        ticket.append("*** URGENT ***")
        ticket.append(LINE_FEED)
        ticket.append("KITCHEN TICKET")
        ticket.append(LINE_FEED)
        ticket.append("*** $urgencyReason ***")
        ticket.append(BOLD_OFF)
        ticket.append(LINE_FEED)
        ticket.append(LEFT)
        ticket.append("=".repeat(TICKET_WIDTH))
        ticket.append(LINE_FEED)
        
        // Rest of the ticket
        ticket.append(generateOrderInfo(order))
        ticket.append(generateItemsList(orderItems))
        ticket.append(generateFooter(order))
        ticket.append(CUT_PAPER)
        
        return ticket.toString()
    }
}