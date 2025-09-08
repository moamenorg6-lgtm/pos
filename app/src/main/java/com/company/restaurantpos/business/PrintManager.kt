package com.company.restaurantpos.business

import android.content.Context
import android.util.Log
import com.company.restaurantpos.data.local.entities.Order
import com.company.restaurantpos.data.local.entities.OrderItem
import com.company.restaurantpos.data.local.entities.Product
import com.company.restaurantpos.data.local.entities.Customer
import com.company.restaurantpos.data.local.entities.Payment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrintManager handles thermal printing operations for receipts and kitchen tickets
 * Supports ESC/POS commands for thermal printers (Bluetooth/USB stub implementation)
 */
@Singleton
class PrintManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "PrintManager"
        
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
        
        // Print widths
        private const val RECEIPT_WIDTH = 32
        private const val KITCHEN_WIDTH = 32
    }
    
    private var isConnected = false
    private var printerType = "STUB" // BLUETOOTH, USB, or STUB for testing
    
    /**
     * Initialize printer connection
     * @param type Printer connection type (BLUETOOTH, USB, STUB)
     * @return true if connection successful
     */
    fun initializePrinter(type: String = "STUB"): Boolean {
        return try {
            printerType = type
            when (type) {
                "BLUETOOTH" -> initBluetoothPrinter()
                "USB" -> initUSBPrinter()
                "STUB" -> {
                    Log.d(TAG, "Using stub printer for testing")
                    isConnected = true
                    true
                }
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize printer: ${e.message}")
            false
        }
    }
    
    /**
     * Print customer receipt
     * @param order Order details
     * @param orderItems List of order items with product details
     * @param customer Customer information (nullable)
     * @param payments List of payments made
     * @return true if print successful
     */
    fun printCustomerReceipt(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>,
        customer: Customer? = null,
        payments: List<Payment> = emptyList()
    ): Boolean {
        if (!isConnected) {
            Log.w(TAG, "Printer not connected")
            return false
        }
        
        return try {
            val receiptContent = generateCustomerReceipt(order, orderItems, customer, payments)
            sendToPrinter(receiptContent)
            Log.d(TAG, "Customer receipt printed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print customer receipt: ${e.message}")
            false
        }
    }
    
    /**
     * Print kitchen ticket
     * @param order Order details
     * @param orderItems List of order items with product details
     * @return true if print successful
     */
    fun printKitchenTicket(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>
    ): Boolean {
        if (!isConnected) {
            Log.w(TAG, "Printer not connected")
            return false
        }
        
        return try {
            val ticketContent = generateKitchenTicket(order, orderItems)
            sendToPrinter(ticketContent)
            Log.d(TAG, "Kitchen ticket printed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print kitchen ticket: ${e.message}")
            false
        }
    }
    
    /**
     * Test print functionality
     * @return true if test print successful
     */
    fun testPrint(): Boolean {
        if (!isConnected) {
            Log.w(TAG, "Printer not connected")
            return false
        }
        
        return try {
            val testContent = generateTestPrint()
            sendToPrinter(testContent)
            Log.d(TAG, "Test print completed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform test print: ${e.message}")
            false
        }
    }
    
    /**
     * Generate customer receipt content
     */
    private fun generateCustomerReceipt(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>,
        customer: Customer?,
        payments: List<Payment>
    ): String {
        val receipt = StringBuilder()
        
        // Initialize printer
        receipt.append(INIT)
        
        // Header
        receipt.append(CENTER)
        receipt.append(BOLD_ON)
        receipt.append("RESTAURANT POS")
        receipt.append(BOLD_OFF)
        receipt.append(LINE_FEED)
        receipt.append("Phone: +1-234-567-8900")
        receipt.append(LINE_FEED)
        receipt.append("Address: 123 Main St, City")
        receipt.append(LINE_FEED)
        receipt.append(LEFT)
        receipt.append("=".repeat(RECEIPT_WIDTH))
        receipt.append(LINE_FEED)
        
        // Order info
        receipt.append("Order No: ${order.orderNo}")
        receipt.append(LINE_FEED)
        receipt.append("Date: ${formatTimestamp(order.createdAt)}")
        receipt.append(LINE_FEED)
        receipt.append("Type: ${order.type.uppercase()}")
        receipt.append(LINE_FEED)
        
        // Customer info
        customer?.let {
            receipt.append("Customer: ${it.name}")
            receipt.append(LINE_FEED)
            receipt.append("Phone: ${it.phone}")
            receipt.append(LINE_FEED)
        }
        
        receipt.append("=".repeat(RECEIPT_WIDTH))
        receipt.append(LINE_FEED)
        
        // Items
        receipt.append(BOLD_ON)
        receipt.append(formatLine("Item", "Qty", "Price", "Total"))
        receipt.append(BOLD_OFF)
        receipt.append(LINE_FEED)
        receipt.append("-".repeat(RECEIPT_WIDTH))
        receipt.append(LINE_FEED)
        
        var subtotal = 0.0
        orderItems.forEach { (orderItem, product) ->
            val itemTotal = orderItem.qty * orderItem.price
            subtotal += itemTotal
            
            receipt.append(formatLine(
                product.nameEn.take(12),
                orderItem.qty.toString(),
                "$${String.format("%.2f", orderItem.price)}",
                "$${String.format("%.2f", itemTotal)}"
            ))
            receipt.append(LINE_FEED)
            
            // Add notes if present
            orderItem.notes?.let { notes ->
                receipt.append("  * $notes")
                receipt.append(LINE_FEED)
            }
        }
        
        receipt.append("-".repeat(RECEIPT_WIDTH))
        receipt.append(LINE_FEED)
        
        // Totals
        val tax = subtotal * 0.1 // 10% tax
        val discount = 0.0 // No discount for now
        val grandTotal = subtotal + tax - discount
        
        receipt.append(formatTotalLine("Subtotal:", "$${String.format("%.2f", subtotal)}"))
        receipt.append(LINE_FEED)
        receipt.append(formatTotalLine("Tax (10%):", "$${String.format("%.2f", tax)}"))
        receipt.append(LINE_FEED)
        if (discount > 0) {
            receipt.append(formatTotalLine("Discount:", "-$${String.format("%.2f", discount)}"))
            receipt.append(LINE_FEED)
        }
        receipt.append(BOLD_ON)
        receipt.append(formatTotalLine("TOTAL:", "$${String.format("%.2f", grandTotal)}"))
        receipt.append(BOLD_OFF)
        receipt.append(LINE_FEED)
        
        // Payment info
        if (payments.isNotEmpty()) {
            receipt.append("=".repeat(RECEIPT_WIDTH))
            receipt.append(LINE_FEED)
            receipt.append(BOLD_ON)
            receipt.append("PAYMENTS:")
            receipt.append(BOLD_OFF)
            receipt.append(LINE_FEED)
            
            payments.forEach { payment ->
                receipt.append(formatTotalLine(
                    "${payment.method.uppercase()}:",
                    "$${String.format("%.2f", payment.amount)}"
                ))
                receipt.append(LINE_FEED)
            }
        }
        
        // Footer
        receipt.append("=".repeat(RECEIPT_WIDTH))
        receipt.append(LINE_FEED)
        receipt.append(CENTER)
        receipt.append("Thank you for your visit!")
        receipt.append(LINE_FEED)
        receipt.append("شكراً لزيارتكم!")
        receipt.append(DOUBLE_LINE_FEED)
        receipt.append(LEFT)
        
        // Cut paper
        receipt.append(CUT_PAPER)
        
        return receipt.toString()
    }
    
    /**
     * Generate kitchen ticket content
     */
    private fun generateKitchenTicket(
        order: Order,
        orderItems: List<Pair<OrderItem, Product>>
    ): String {
        val ticket = StringBuilder()
        
        // Initialize printer
        ticket.append(INIT)
        
        // Header
        ticket.append(CENTER)
        ticket.append(BOLD_ON)
        ticket.append("KITCHEN TICKET")
        ticket.append(BOLD_OFF)
        ticket.append(LINE_FEED)
        ticket.append(LEFT)
        ticket.append("=".repeat(KITCHEN_WIDTH))
        ticket.append(LINE_FEED)
        
        // Order info
        ticket.append(BOLD_ON)
        ticket.append("Order No: ${order.orderNo}")
        ticket.append(BOLD_OFF)
        ticket.append(LINE_FEED)
        ticket.append("Time: ${formatTimestamp(order.createdAt)}")
        ticket.append(LINE_FEED)
        ticket.append("Type: ${order.type.uppercase()}")
        ticket.append(LINE_FEED)
        ticket.append("=".repeat(KITCHEN_WIDTH))
        ticket.append(LINE_FEED)
        
        // Items (no prices for kitchen)
        ticket.append(BOLD_ON)
        ticket.append("ITEMS:")
        ticket.append(BOLD_OFF)
        ticket.append(LINE_FEED)
        ticket.append("-".repeat(KITCHEN_WIDTH))
        ticket.append(LINE_FEED)
        
        orderItems.forEach { (orderItem, product) ->
            ticket.append(BOLD_ON)
            ticket.append("${orderItem.qty.toInt()}x ${product.nameEn}")
            ticket.append(BOLD_OFF)
            ticket.append(LINE_FEED)
            
            // Highlight special instructions
            orderItem.notes?.let { notes ->
                ticket.append(BOLD_ON)
                ticket.append("*** $notes ***")
                ticket.append(BOLD_OFF)
                ticket.append(LINE_FEED)
            }
            ticket.append(LINE_FEED)
        }
        
        ticket.append("-".repeat(KITCHEN_WIDTH))
        ticket.append(DOUBLE_LINE_FEED)
        
        // Cut paper
        ticket.append(CUT_PAPER)
        
        return ticket.toString()
    }
    
    /**
     * Generate test print content
     */
    private fun generateTestPrint(): String {
        val test = StringBuilder()
        
        test.append(INIT)
        test.append(CENTER)
        test.append(BOLD_ON)
        test.append("PRINTER TEST")
        test.append(BOLD_OFF)
        test.append(LINE_FEED)
        test.append(LEFT)
        test.append("=".repeat(RECEIPT_WIDTH))
        test.append(LINE_FEED)
        test.append("This is a test print")
        test.append(LINE_FEED)
        test.append("Date: ${formatTimestamp(System.currentTimeMillis())}")
        test.append(LINE_FEED)
        test.append("Printer: $printerType")
        test.append(LINE_FEED)
        test.append("Status: Connected")
        test.append(DOUBLE_LINE_FEED)
        test.append(CUT_PAPER)
        
        return test.toString()
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
        val totalWidth = RECEIPT_WIDTH
        val combined = "$label $amount"
        return combined.padStart(totalWidth)
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
     * Send content to printer (stub implementation)
     */
    private fun sendToPrinter(content: String): Boolean {
        return when (printerType) {
            "BLUETOOTH" -> sendToBluetoothPrinter(content)
            "USB" -> sendToUSBPrinter(content)
            "STUB" -> {
                Log.d(TAG, "STUB PRINT OUTPUT:")
                Log.d(TAG, content)
                true
            }
            else -> false
        }
    }
    
    /**
     * Initialize Bluetooth printer (stub implementation)
     */
    private fun initBluetoothPrinter(): Boolean {
        // TODO: Implement Bluetooth printer initialization
        Log.d(TAG, "Bluetooth printer initialization - stub")
        isConnected = true
        return true
    }
    
    /**
     * Initialize USB printer (stub implementation)
     */
    private fun initUSBPrinter(): Boolean {
        // TODO: Implement USB printer initialization
        Log.d(TAG, "USB printer initialization - stub")
        isConnected = true
        return true
    }
    
    /**
     * Send to Bluetooth printer (stub implementation)
     */
    private fun sendToBluetoothPrinter(content: String): Boolean {
        // TODO: Implement Bluetooth printing
        Log.d(TAG, "Bluetooth print - stub")
        Log.d(TAG, content)
        return true
    }
    
    /**
     * Send to USB printer (stub implementation)
     */
    private fun sendToUSBPrinter(content: String): Boolean {
        // TODO: Implement USB printing
        Log.d(TAG, "USB print - stub")
        Log.d(TAG, content)
        return true
    }
    
    /**
     * Check if printer is connected
     */
    fun isConnected(): Boolean = isConnected
    
    /**
     * Disconnect printer
     */
    fun disconnect() {
        isConnected = false
        Log.d(TAG, "Printer disconnected")
    }
}