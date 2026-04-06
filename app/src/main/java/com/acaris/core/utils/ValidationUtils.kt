package com.acaris.core.utils

object ValidationUtils {
    /**
     * Mengecek apakah format email valid (mengandung @ dan domain)
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+\$"
        return email.matches(emailPattern.toRegex())
    }

    /**
     * Mengecek apakah password memenuhi syarat minimal 8 karakter
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}