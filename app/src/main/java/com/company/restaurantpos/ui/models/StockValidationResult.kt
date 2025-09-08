package com.company.restaurantpos.ui.models

/**
 * Result of stock validation for recipe ingredients
 */
data class StockValidationResult(
    val isValid: Boolean,
    val insufficientIngredients: List<InsufficientIngredient> = emptyList()
)

/**
 * Represents an ingredient with insufficient stock
 */
data class InsufficientIngredient(
    val nameEn: String,
    val nameAr: String,
    val required: Double,
    val available: Double,
    val unit: String
)