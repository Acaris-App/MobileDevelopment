package com.acaris.features.user_management.domain.repository

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.model.ClassInfo
import com.acaris.features.user_management.domain.model.MahasiswaDocument
import java.io.File

interface UserManagementRepository {

    val usersFlow: kotlinx.coroutines.flow.StateFlow<List<User>>

    suspend fun getUsers(role: String, search: String? = null, sortBy: String? = null, page: Int = 1): Result<List<User>>

    suspend fun getUserDetail(id: String): Result<User>

    suspend fun addAdmin(
        name: String,
        email: String,
        identifier: String,
        password: String,
        profilePicture: File?
    ): Result<User>

    suspend fun updateUser(
        id: String,
        name: String?,
        email: String?,
        identifierNumber: String?,
        angkatan: Int?,
        currentSemester: Int?,
        dosenPa: String?,
        kodeKelas: String?,
        ipk: Double?,
        profilePicture: File?
    ): Result<User>

    suspend fun getAllClasses(): Result<List<ClassInfo>>

    suspend fun changeUserStatus(id: String, isActive: Boolean): Result<Unit>

    suspend fun deleteUserPermanently(id: String): Result<Unit>

    suspend fun getMahasiswaDocuments(userId: String): Result<List<MahasiswaDocument>>

    suspend fun getBimbinganHistory(userId: String): Result<List<BimbinganHistory>>

    suspend fun uploadMahasiswaDocument(userId: String, documentType: String, semester: Int?, file: File): Result<MahasiswaDocument>

    suspend fun updateMahasiswaDocument(documentId: String, semester: Int?, file: File?): Result<MahasiswaDocument>

    suspend fun deleteMahasiswaDocument(documentId: String): Result<Unit>

    suspend fun getMahasiswaChatbotHistory(userId: String): Result<List<ChatHistoryDomain>>

    suspend fun getMahasiswaChatbotDetail(userId: String, sessionId: String): Result<ChatSessionDomain>
}