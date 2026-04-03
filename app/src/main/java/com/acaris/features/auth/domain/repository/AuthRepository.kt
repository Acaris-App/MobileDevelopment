package com.acaris.features.auth.domain.repository

import com.acaris.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}