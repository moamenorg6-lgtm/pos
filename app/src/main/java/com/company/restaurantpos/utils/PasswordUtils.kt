package com.company.restaurantpos.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory

/**
 * Utility class for secure password handling
 * Uses PBKDF2 with SHA-256 for password hashing
 */
object PasswordUtils {
    
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    
    /**
     * Hash a password with a random salt using PBKDF2
     * @param password Plain text password
     * @return Hashed password with salt (format: salt:hash)
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hashPasswordWithSalt(password, salt)
        return "${bytesToHex(salt)}:${bytesToHex(hash)}"
    }
    
    /**
     * Verify a password against a stored hash
     * @param password Plain text password to verify
     * @param storedHash Stored hash (format: salt:hash)
     * @return True if password matches, false otherwise
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(":")
            if (parts.size != 2) return false
            
            val salt = hexToBytes(parts[0])
            val storedHashBytes = hexToBytes(parts[1])
            val computedHash = hashPasswordWithSalt(password, salt)
            
            // Use constant-time comparison to prevent timing attacks
            constantTimeEquals(storedHashBytes, computedHash)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generate a random salt
     * @return Random salt bytes
     */
    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
    
    /**
     * Hash password with given salt using PBKDF2
     * @param password Plain text password
     * @param salt Salt bytes
     * @return Hashed password bytes
     */
    private fun hashPasswordWithSalt(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
    
    /**
     * Convert byte array to hexadecimal string
     * @param bytes Byte array
     * @return Hexadecimal string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Convert hexadecimal string to byte array
     * @param hex Hexadecimal string
     * @return Byte array
     */
    private fun hexToBytes(hex: String): ByteArray {
        return hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
    
    /**
     * Constant-time comparison to prevent timing attacks
     * @param a First byte array
     * @param b Second byte array
     * @return True if arrays are equal, false otherwise
     */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return List of validation errors (empty if valid)
     */
    fun validatePasswordStrength(password: String): List<String> {
        val errors = mutableListOf<String>()
        
        if (password.length < 6) {
            errors.add("Password must be at least 6 characters long")
        }
        
        if (!password.any { it.isDigit() }) {
            errors.add("Password must contain at least one digit")
        }
        
        if (!password.any { it.isLetter() }) {
            errors.add("Password must contain at least one letter")
        }
        
        return errors
    }
    
    /**
     * Generate a secure random password
     * @param length Password length (default: 12)
     * @return Generated password
     */
    fun generateSecurePassword(length: Int = 12): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        val random = SecureRandom()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
}