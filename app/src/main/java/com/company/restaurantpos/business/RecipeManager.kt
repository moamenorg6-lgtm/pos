package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.daos.IngredientDao
import com.company.restaurantpos.data.local.daos.RecipeDao
import com.company.restaurantpos.data.local.daos.RecipeIngredientDao
import com.company.restaurantpos.ui.models.CartItem
import com.company.restaurantpos.ui.models.InsufficientIngredient
import com.company.restaurantpos.ui.models.StockValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages recipe operations including stock validation and deduction
 */
@Singleton
class RecipeManager @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val ingredientDao: IngredientDao
) {
    
    /**
     * Validates if there's sufficient stock for all recipe ingredients in the cart
     */
    suspend fun validateStock(cartItems: List<CartItem>): StockValidationResult = withContext(Dispatchers.IO) {
        val insufficientIngredients = mutableListOf<InsufficientIngredient>()
        
        for (cartItem in cartItems) {
            if (cartItem.product.isRecipe) {
                val recipe = recipeDao.getByProductId(cartItem.product.id)
                if (recipe != null) {
                    val recipeIngredients = recipeIngredientDao.getByRecipeId(recipe.id)
                    
                    for (recipeIngredient in recipeIngredients) {
                        val ingredient = ingredientDao.getById(recipeIngredient.ingredientId)
                        if (ingredient != null) {
                            val requiredQuantity = recipeIngredient.quantity * cartItem.quantity
                            
                            if (ingredient.stock < requiredQuantity) {
                                insufficientIngredients.add(
                                    InsufficientIngredient(
                                        nameEn = ingredient.nameEn,
                                        nameAr = ingredient.nameAr,
                                        required = requiredQuantity,
                                        available = ingredient.stock,
                                        unit = ingredient.unit
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        
        StockValidationResult(
            isValid = insufficientIngredients.isEmpty(),
            insufficientIngredients = insufficientIngredients
        )
    }
    
    /**
     * Deducts ingredient stock for recipe items in the cart
     * This should be called atomically during order creation
     */
    suspend fun deductStock(cartItems: List<CartItem>): Boolean = withContext(Dispatchers.IO) {
        try {
            for (cartItem in cartItems) {
                if (cartItem.product.isRecipe) {
                    val recipe = recipeDao.getByProductId(cartItem.product.id)
                    if (recipe != null) {
                        val recipeIngredients = recipeIngredientDao.getByRecipeId(recipe.id)
                        
                        for (recipeIngredient in recipeIngredients) {
                            val requiredQuantity = recipeIngredient.quantity * cartItem.quantity
                            val rowsUpdated = ingredientDao.reduceStock(
                                recipeIngredient.ingredientId,
                                requiredQuantity
                            )
                            
                            if (rowsUpdated == 0) {
                                // If any deduction fails, we should rollback
                                // For now, we'll return false and let the caller handle it
                                return@withContext false
                            }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets recipe ingredients for a product (for display purposes)
     */
    suspend fun getRecipeIngredients(productId: Int): List<RecipeIngredientInfo> = withContext(Dispatchers.IO) {
        val recipe = recipeDao.getByProductId(productId) ?: return@withContext emptyList()
        val recipeIngredients = recipeIngredientDao.getByRecipeId(recipe.id)
        
        recipeIngredients.mapNotNull { recipeIngredient ->
            val ingredient = ingredientDao.getById(recipeIngredient.ingredientId)
            ingredient?.let {
                RecipeIngredientInfo(
                    nameEn = it.nameEn,
                    nameAr = it.nameAr,
                    quantity = recipeIngredient.quantity,
                    unit = recipeIngredient.unit,
                    availableStock = it.stock
                )
            }
        }
    }
}

/**
 * Information about a recipe ingredient
 */
data class RecipeIngredientInfo(
    val nameEn: String,
    val nameAr: String,
    val quantity: Double,
    val unit: String,
    val availableStock: Double
)