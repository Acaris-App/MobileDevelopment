// File: domain/repository/UserManagementRepository.kt
package com.acaris.features.user_management.domain.repository

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.model.MahasiswaDocument
import java.io.File

interface UserManagementRepository {
    suspend fun getUsers(
        role: String,
        search: String? = null,
        sortBy: String? = null
    ): Result<List<User>>

    suspend fun getUserDetail(id: String): Result<User>

    suspend fun addAdmin(name: String, email: String, password: String): Result<User>

    suspend fun updateUser(
        id: String,
        name: String?,
        email: String?,
        identifierNumber: String?
    ): Result<User>

    suspend fun changeUserStatus(id: String, isActive: Boolean): Result<Unit>

    suspend fun deleteUserPermanently(id: String): Result<Unit>

    suspend fun getMahasiswaDocuments(userId: String): Result<List<MahasiswaDocument>>

    suspend fun getBimbinganHistory(userId: String): Result<List<BimbinganHistory>>

    suspend fun uploadMahasiswaDocument(userId: String, documentType: String, semester: Int?, file: File): Result<MahasiswaDocument>

    suspend fun updateMahasiswaDocument(documentId: String, semester: Int?, file: File?): Result<MahasiswaDocument>

    suspend fun deleteMahasiswaDocument(documentId: String): Result<Unit>
}