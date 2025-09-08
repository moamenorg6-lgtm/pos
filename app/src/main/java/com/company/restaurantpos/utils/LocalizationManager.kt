package com.company.restaurantpos.utils

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.localizationDataStore: DataStore<Preferences> by preferencesDataStore(name = "localization_preferences")

/**
 * Manager for app localization and language switching
 */
@Singleton
class LocalizationManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ARABIC = "ar"
    }
    
    /**
     * Get current language as Flow
     */
    fun getCurrentLanguageFlow(): Flow<String> {
        return context.localizationDataStore.data.map { preferences ->
            preferences[SELECTED_LANGUAGE] ?: getSystemLanguage()
        }
    }
    
    /**
     * Get current language
     */
    suspend fun getCurrentLanguage(): String {
        return context.localizationDataStore.data.first()[SELECTED_LANGUAGE] ?: getSystemLanguage()
    }
    
    /**
     * Set language
     */
    suspend fun setLanguage(language: String) {
        context.localizationDataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }
    
    /**
     * Get system language
     */
    private fun getSystemLanguage(): String {
        val systemLanguage = Locale.getDefault().language
        return if (systemLanguage == LANGUAGE_ARABIC) LANGUAGE_ARABIC else LANGUAGE_ENGLISH
    }
    
    /**
     * Get supported languages
     */
    fun getSupportedLanguages(): List<Language> {
        return listOf(
            Language(LANGUAGE_ENGLISH, "English", "🇺🇸"),
            Language(LANGUAGE_ARABIC, "العربية", "🇸🇦")
        )
    }
    
    /**
     * Check if current language is RTL
     */
    suspend fun isRTL(): Boolean {
        return getCurrentLanguage() == LANGUAGE_ARABIC
    }
    
    /**
     * Get localized string
     */
    fun getString(key: String, language: String = LANGUAGE_ENGLISH): String {
        return when (language) {
            LANGUAGE_ARABIC -> getArabicString(key)
            else -> getEnglishString(key)
        }
    }
    
    /**
     * Get English strings
     */
    private fun getEnglishString(key: String): String {
        return when (key) {
            // App
            "app_name" -> "Restaurant POS"
            "welcome" -> "Welcome"
            "loading" -> "Loading..."
            "error" -> "Error"
            "success" -> "Success"
            "cancel" -> "Cancel"
            "ok" -> "OK"
            "yes" -> "Yes"
            "no" -> "No"
            "save" -> "Save"
            "delete" -> "Delete"
            "edit" -> "Edit"
            "add" -> "Add"
            "search" -> "Search"
            "filter" -> "Filter"
            "refresh" -> "Refresh"
            
            // Authentication
            "login" -> "Login"
            "logout" -> "Logout"
            "username" -> "Username"
            "password" -> "Password"
            "sign_in" -> "Sign In"
            "sign_out" -> "Sign Out"
            "remember_me" -> "Remember me"
            "forgot_password" -> "Forgot Password?"
            "change_password" -> "Change Password"
            "current_password" -> "Current Password"
            "new_password" -> "New Password"
            "confirm_password" -> "Confirm Password"
            
            // Navigation
            "home" -> "Home"
            "pos" -> "POS"
            "reports" -> "Reports"
            "inventory" -> "Inventory"
            "settings" -> "Settings"
            "kitchen" -> "Kitchen"
            "admin" -> "Admin"
            
            // POS
            "add_to_cart" -> "Add to Cart"
            "cart" -> "Cart"
            "checkout" -> "Checkout"
            "total" -> "Total"
            "subtotal" -> "Subtotal"
            "tax" -> "Tax"
            "discount" -> "Discount"
            "grand_total" -> "Grand Total"
            "cash" -> "Cash"
            "card" -> "Card"
            "payment_method" -> "Payment Method"
            "print_receipt" -> "Print Receipt"
            "print_kitchen_ticket" -> "Print Kitchen Ticket"
            
            // Orders
            "orders" -> "Orders"
            "order_number" -> "Order #"
            "order_date" -> "Order Date"
            "order_status" -> "Order Status"
            "pending" -> "Pending"
            "preparing" -> "Preparing"
            "ready" -> "Ready"
            "completed" -> "Completed"
            "cancelled" -> "Cancelled"
            
            // Products
            "products" -> "Products"
            "product_name" -> "Product Name"
            "price" -> "Price"
            "category" -> "Category"
            "description" -> "Description"
            "available" -> "Available"
            "out_of_stock" -> "Out of Stock"
            
            // Reports
            "daily_sales" -> "Daily Sales"
            "top_products" -> "Top Products"
            "low_stock" -> "Low Stock"
            "sales_report" -> "Sales Report"
            "revenue" -> "Revenue"
            "quantity_sold" -> "Quantity Sold"
            
            // Settings
            "backup" -> "Backup"
            "restore" -> "Restore"
            "export_backup" -> "Export Backup"
            "import_backup" -> "Import Backup"
            "user_management" -> "User Management"
            "create_user" -> "Create User"
            "language" -> "Language"
            "theme" -> "Theme"
            "dark_mode" -> "Dark Mode"
            "light_mode" -> "Light Mode"
            
            // Messages
            "thank_you" -> "Thank you for your visit!"
            "order_placed" -> "Order placed successfully"
            "payment_successful" -> "Payment successful"
            "backup_created" -> "Backup created successfully"
            "backup_restored" -> "Backup restored successfully"
            "user_created" -> "User created successfully"
            "password_changed" -> "Password changed successfully"
            
            else -> key
        }
    }
    
    /**
     * Get Arabic strings
     */
    private fun getArabicString(key: String): String {
        return when (key) {
            // App
            "app_name" -> "نظام نقاط البيع للمطاعم"
            "welcome" -> "مرحباً"
            "loading" -> "جاري التحميل..."
            "error" -> "خطأ"
            "success" -> "نجح"
            "cancel" -> "إلغاء"
            "ok" -> "موافق"
            "yes" -> "نعم"
            "no" -> "لا"
            "save" -> "حفظ"
            "delete" -> "حذف"
            "edit" -> "تعديل"
            "add" -> "إضافة"
            "search" -> "بحث"
            "filter" -> "تصفية"
            "refresh" -> "تحديث"
            
            // Authentication
            "login" -> "تسجيل الدخول"
            "logout" -> "تسجيل الخروج"
            "username" -> "اسم المستخدم"
            "password" -> "كلمة المرور"
            "sign_in" -> "دخول"
            "sign_out" -> "خروج"
            "remember_me" -> "تذكرني"
            "forgot_password" -> "نسيت كلمة المرور؟"
            "change_password" -> "تغيير كلمة المرور"
            "current_password" -> "كلمة المرور الحالية"
            "new_password" -> "كلمة المرور الجديدة"
            "confirm_password" -> "تأكيد كلمة المرور"
            
            // Navigation
            "home" -> "الرئيسية"
            "pos" -> "نقاط البيع"
            "reports" -> "التقارير"
            "inventory" -> "المخزون"
            "settings" -> "الإعدادات"
            "kitchen" -> "المطبخ"
            "admin" -> "الإدارة"
            
            // POS
            "add_to_cart" -> "إضافة للسلة"
            "cart" -> "السلة"
            "checkout" -> "الدفع"
            "total" -> "المجموع"
            "subtotal" -> "المجموع الفرعي"
            "tax" -> "الضريبة"
            "discount" -> "الخصم"
            "grand_total" -> "المجموع الإجمالي"
            "cash" -> "نقداً"
            "card" -> "بطاقة"
            "payment_method" -> "طريقة الدفع"
            "print_receipt" -> "طباعة الفاتورة"
            "print_kitchen_ticket" -> "طباعة تذكرة المطبخ"
            
            // Orders
            "orders" -> "الطلبات"
            "order_number" -> "رقم الطلب"
            "order_date" -> "تاريخ الطلب"
            "order_status" -> "حالة الطلب"
            "pending" -> "في الانتظار"
            "preparing" -> "قيد التحضير"
            "ready" -> "جاهز"
            "completed" -> "مكتمل"
            "cancelled" -> "ملغي"
            
            // Products
            "products" -> "المنتجات"
            "product_name" -> "اسم المنتج"
            "price" -> "السعر"
            "category" -> "الفئة"
            "description" -> "الوصف"
            "available" -> "متوفر"
            "out_of_stock" -> "غير متوفر"
            
            // Reports
            "daily_sales" -> "المبيعات اليومية"
            "top_products" -> "أفضل المنتجات"
            "low_stock" -> "مخزون منخفض"
            "sales_report" -> "تقرير المبيعات"
            "revenue" -> "الإيرادات"
            "quantity_sold" -> "الكمية المباعة"
            
            // Settings
            "backup" -> "النسخ الاحتياطي"
            "restore" -> "الاستعادة"
            "export_backup" -> "تصدير النسخة الاحتياطية"
            "import_backup" -> "استيراد النسخة الاحتياطية"
            "user_management" -> "إدارة المستخدمين"
            "create_user" -> "إنشاء مستخدم"
            "language" -> "اللغة"
            "theme" -> "المظهر"
            "dark_mode" -> "الوضع المظلم"
            "light_mode" -> "الوضع المضيء"
            
            // Messages
            "thank_you" -> "شكراً لزيارتكم!"
            "order_placed" -> "تم تقديم الطلب بنجاح"
            "payment_successful" -> "تم الدفع بنجاح"
            "backup_created" -> "تم إنشاء النسخة الاحتياطية بنجاح"
            "backup_restored" -> "تم استعادة النسخة الاحتياطية بنجاح"
            "user_created" -> "تم إنشاء المستخدم بنجاح"
            "password_changed" -> "تم تغيير كلمة المرور بنجاح"
            
            else -> key
        }
    }
}

/**
 * Language data class
 */
data class Language(
    val code: String,
    val name: String,
    val flag: String
)

/**
 * Composable for providing localization context
 */
@Composable
fun LocalizationProvider(
    localizationManager: LocalizationManager,
    content: @Composable (LocalizationState) -> Unit
) {
    val currentLanguage by localizationManager.getCurrentLanguageFlow().collectAsState(initial = LocalizationManager.LANGUAGE_ENGLISH)
    
    val localizationState = remember(currentLanguage) {
        LocalizationState(
            currentLanguage = currentLanguage,
            isRTL = currentLanguage == LocalizationManager.LANGUAGE_ARABIC,
            getString = { key -> localizationManager.getString(key, currentLanguage) }
        )
    }
    
    content(localizationState)
}

/**
 * Localization state
 */
data class LocalizationState(
    val currentLanguage: String,
    val isRTL: Boolean,
    val getString: (String) -> String
)