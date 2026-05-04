package com.acaris.features.user_management.domain.repository

import com.acaris.features.user_management.domain.model.User

interface UserManagementRepository {
    suspend fun getUsers(
        role: String,
        search: String? = null,
        sortBy: String? = null
    ): Result<List<User>>

    suspend fun addAdmin(name: String, email: String, password: String): Result<User>

    suspend fun updateUser(
        id: String,
        name: String?,
        email: String?,
        identifierNumber: String?
    ): Result<User>

    suspend fun changeUserStatus(id: String, isActive: Boolean): Result<Unit>

    suspend fun deleteUserPermanently(id: String): Result<Unit>
}