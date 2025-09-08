package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.daos.IngredientDao
import com.company.restaurantpos.data.local.daos.RecipeDao
import com.company.restaurantpos.data.local.daos.RecipeIngredientDao
import com.company.restaurantpos.data.local.entities.*
import com.company.restaurantpos.ui.models.CartItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RecipeManagerTest {
    
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeIngredientDao: RecipeIngredientDao
    private lateinit var ingredientDao: IngredientDao
    private lateinit var recipeManager: RecipeManager
    
    @Before
    fun setup() {
        recipeDao = mockk()
        recipeIngredientDao = mockk()
        ingredientDao = mockk()
        recipeManager = RecipeManager(recipeDao, recipeIngredientDao, ingredientDao)
    }
    
    @Test
    fun `validateStock returns valid when sufficient stock available`() = runTest {
        // Given
        val product = Product(
            id = 1,
            nameEn = "Burger",
            nameAr = "برجر",
            sku = "BURGER001",
            price = 10.0,
            category = "Food",
            isRecipe = true,
            isActive = true,
            stock = 0.0
        )
        val cartItem = CartItem(product = product, quantity = 2.0)
        val cartItems = listOf(cartItem)
        
        val recipe = Recipe(
            id = 1,
            productId = 1,
            nameEn = "Burger Recipe",
            nameAr = "وصفة البرجر",
            instructions = "Cook burger"
        )
        
        val ingredient = Ingredient(
            id = 1,
            nameEn = "Beef Patty",
            nameAr = "قطعة لحم",
            unit = "piece",
            stock = 20.0,
            minStock = 5.0,
            cost = 2.0
        )
        
        val recipeIngredient = RecipeIngredient(
            id = 1,
            recipeId = 1,
            ingredientId = 1,
            quantity = 1.0,
            unit = "piece"
        )
        
        coEvery { recipeDao.getByProductId(1) } returns recipe
        coEvery { recipeIngredientDao.getByRecipeId(1) } returns listOf(recipeIngredient)
        coEvery { ingredientDao.getById(1) } returns ingredient
        
        // When
        val result = recipeManager.validateStock(cartItems)
        
        // Then
        assertTrue(result.isValid)
        assertTrue(result.insufficientIngredients.isEmpty())
    }
    
    @Test
    fun `validateStock returns invalid when insufficient stock`() = runTest {
        // Given
        val product = Product(
            id = 1,
            nameEn = "Burger",
            nameAr = "برجر",
            sku = "BURGER001",
            price = 10.0,
            category = "Food",
            isRecipe = true,
            isActive = true,
            stock = 0.0
        )
        val cartItem = CartItem(product = product, quantity = 10.0) // Need 10 burgers
        val cartItems = listOf(cartItem)
        
        val recipe = Recipe(
            id = 1,
            productId = 1,
            nameEn = "Burger Recipe",
            nameAr = "وصفة البرجر",
            instructions = "Cook burger"
        )
        
        val ingredient = Ingredient(
            id = 1,
            nameEn = "Beef Patty",
            nameAr = "قطعة لحم",
            unit = "piece",
            stock = 5.0, // Only 5 available
            minStock = 2.0,
            cost = 2.0
        )
        
        val recipeIngredient = RecipeIngredient(
            id = 1,
            recipeId = 1,
            ingredientId = 1,
            quantity = 1.0, // Need 1 per burger
            unit = "piece"
        )
        
        coEvery { recipeDao.getByProductId(1) } returns recipe
        coEvery { recipeIngredientDao.getByRecipeId(1) } returns listOf(recipeIngredient)
        coEvery { ingredientDao.getById(1) } returns ingredient
        
        // When
        val result = recipeManager.validateStock(cartItems)
        
        // Then
        assertFalse(result.isValid)
        assertEquals(1, result.insufficientIngredients.size)
        
        val insufficientIngredient = result.insufficientIngredients.first()
        assertEquals("Beef Patty", insufficientIngredient.nameEn)
        assertEquals(10.0, insufficientIngredient.required, 0.01)
        assertEquals(5.0, insufficientIngredient.available, 0.01)
    }
    
    @Test
    fun `deductStock returns true when successful`() = runTest {
        // Given
        val product = Product(
            id = 1,
            nameEn = "Burger",
            nameAr = "برجر",
            sku = "BURGER001",
            price = 10.0,
            category = "Food",
            isRecipe = true,
            isActive = true,
            stock = 0.0
        )
        val cartItem = CartItem(product = product, quantity = 2.0)
        val cartItems = listOf(cartItem)
        
        val recipe = Recipe(
            id = 1,
            productId = 1,
            nameEn = "Burger Recipe",
            nameAr = "وصفة البرجر",
            instructions = "Cook burger"
        )
        
        val recipeIngredient = RecipeIngredient(
            id = 1,
            recipeId = 1,
            ingredientId = 1,
            quantity = 1.0,
            unit = "piece"
        )
        
        coEvery { recipeDao.getByProductId(1) } returns recipe
        coEvery { recipeIngredientDao.getByRecipeId(1) } returns listOf(recipeIngredient)
        coEvery { ingredientDao.reduceStock(1, 2.0) } returns true
        
        // When
        val result = recipeManager.deductStock(cartItems)
        
        // Then
        assertTrue(result)
        coVerify { ingredientDao.reduceStock(1, 2.0) }
    }
    
    @Test
    fun `deductStock returns false when reduction fails`() = runTest {
        // Given
        val product = Product(
            id = 1,
            nameEn = "Burger",
            nameAr = "برجر",
            sku = "BURGER001",
            price = 10.0,
            category = "Food",
            isRecipe = true,
            isActive = true,
            stock = 0.0
        )
        val cartItem = CartItem(product = product, quantity = 2.0)
        val cartItems = listOf(cartItem)
        
        val recipe = Recipe(
            id = 1,
            productId = 1,
            nameEn = "Burger Recipe",
            nameAr = "وصفة البرجر",
            instructions = "Cook burger"
        )
        
        val recipeIngredient = RecipeIngredient(
            id = 1,
            recipeId = 1,
            ingredientId = 1,
            quantity = 1.0,
            unit = "piece"
        )
        
        coEvery { recipeDao.getByProductId(1) } returns recipe
        coEvery { recipeIngredientDao.getByRecipeId(1) } returns listOf(recipeIngredient)
        coEvery { ingredientDao.reduceStock(1, 2.0) } returns false
        
        // When
        val result = recipeManager.deductStock(cartItems)
        
        // Then
        assertFalse(result)
        coVerify { ingredientDao.reduceStock(1, 2.0) }
    }
}