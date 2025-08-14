package com.bersamadapa.recylefood.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {

    // Hash a password using bcrypt
    fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt(12) // 12 is the work factor (can be adjusted)
        return BCrypt.hashpw(password, salt)
    }

    // Verify if the given password matches the stored hash
    fun verifyPassword(storedHash: String, password: String): Boolean {
        return BCrypt.checkpw(password, storedHash)
    }
}
