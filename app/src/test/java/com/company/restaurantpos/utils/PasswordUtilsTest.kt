package com.company.restaurantpos.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PasswordUtils
 */
class PasswordUtilsTest {
    
    @Test
    fun `hashPassword should return non-empty string`() {
        val password = "testPassword123"
        val hashedPassword = PasswordUtils.hashPassword(password)
        
        assertNotNull(hashedPassword)
        assertTrue(hashedPassword.isNotEmpty())
        assertTrue(hashedPassword.contains(":")) // Should contain salt:hash format
    }
    
    @Test
    fun `hashPassword should generate different hashes for same password`() {
        val password = "testPassword123"
        val hash1 = PasswordUtils.hashPassword(password)
        val hash2 = PasswordUtils.hashPassword(password)
        
        assertNotEquals(hash1, hash2) // Different salts should produce different hashes
    }
    
    @Test
    fun `verifyPassword should return true for correct password`() {
        val password = "testPassword123"
        val hashedPassword = PasswordUtils.hashPassword(password)
        
        assertTrue(PasswordUtils.verifyPassword(password, hashedPassword))
    }
    
    @Test
    fun `verifyPassword should return false for incorrect password`() {
        val password = "testPassword123"
        val wrongPassword = "wrongPassword456"
        val hashedPassword = PasswordUtils.hashPassword(password)
        
        assertFalse(PasswordUtils.verifyPassword(wrongPassword, hashedPassword))
    }
    
    @Test
    fun `verifyPassword should return false for malformed hash`() {
        val password = "testPassword123"
        val malformedHash = "invalidhash"
        
        assertFalse(PasswordUtils.verifyPassword(password, malformedHash))
    }
    
    @Test
    fun `verifyPassword should return false for empty hash`() {
        val password = "testPassword123"
        val emptyHash = ""
        
        assertFalse(PasswordUtils.verifyPassword(password, emptyHash))
    }
    
    @Test
    fun `validatePasswordStrength should return errors for weak passwords`() {
        val weakPasswords = listOf(
            "123", // Too short
            "password", // No digits
            "12345678", // No letters
            "pass", // Too short and no digits
            "" // Empty
        )
        
        weakPasswords.forEach { password ->
            val errors = PasswordUtils.validatePasswordStrength(password)
            assertTrue("Password '$password' should have validation errors", errors.isNotEmpty())
        }
    }
    
    @Test
    fun `validatePasswordStrength should return no errors for strong passwords`() {
        val strongPasswords = listOf(
            "password123",
            "mySecurePass1",
            "TestPassword2024",
            "admin123"
        )
        
        strongPasswords.forEach { password ->
            val errors = PasswordUtils.validatePasswordStrength(password)
            assertTrue("Password '$password' should be valid", errors.isEmpty())
        }
    }
    
    @Test
    fun `validatePasswordStrength should check minimum length`() {
        val shortPassword = "abc12"
        val errors = PasswordUtils.validatePasswordStrength(shortPassword)
        
        assertTrue(errors.any { it.contains("6 characters") })
    }
    
    @Test
    fun `validatePasswordStrength should check for digits`() {
        val passwordWithoutDigits = "passwordonly"
        val errors = PasswordUtils.validatePasswordStrength(passwordWithoutDigits)
        
        assertTrue(errors.any { it.contains("digit") })
    }
    
    @Test
    fun `validatePasswordStrength should check for letters`() {
        val passwordWithoutLetters = "123456789"
        val errors = PasswordUtils.validatePasswordStrength(passwordWithoutLetters)
        
        assertTrue(errors.any { it.contains("letter") })
    }
    
    @Test
    fun `generateSecurePassword should return password of correct length`() {
        val lengths = listOf(8, 12, 16, 20)
        
        lengths.forEach { length ->
            val password = PasswordUtils.generateSecurePassword(length)
            assertEquals("Generated password should have length $length", length, password.length)
        }
    }
    
    @Test
    fun `generateSecurePassword should return different passwords`() {
        val password1 = PasswordUtils.generateSecurePassword(12)
        val password2 = PasswordUtils.generateSecurePassword(12)
        
        assertNotEquals("Generated passwords should be different", password1, password2)
    }
    
    @Test
    fun `generateSecurePassword should contain valid characters`() {
        val password = PasswordUtils.generateSecurePassword(20)
        val validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        
        password.forEach { char ->
            assertTrue("Character '$char' should be valid", validChars.contains(char))
        }
    }
    
    @Test
    fun `generateSecurePassword should pass strength validation`() {
        repeat(10) {
            val password = PasswordUtils.generateSecurePassword(12)
            val errors = PasswordUtils.validatePasswordStrength(password)
            assertTrue("Generated password should be strong: $password", errors.isEmpty())
        }
    }
}