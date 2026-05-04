package com.acaris.features.user_management.data.repository

import com.acaris.features.user_management.data.mapper.toDomain
import com.acaris.features.user_management.data.remote.datasource.UserManagementApiService
import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class UserManagementRepositoryImpl @Inject constructor(
    private val apiService: UserManagementApiService
) : UserManagementRepository {

    override suspend fun getUsers(role: String, search: String?, sortBy: String?): Result<List<User>> {
        return try {
            val response = apiService.getUsers(role, search, sortBy)
            if (response.status == "success" || response.status == "200") {
                val list = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil data pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil data. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun addAdmin(name: String, email: String, password: String): Result<User> {
        return try {
            val response = apiService.addAdmin(name, email, password)
            if (response.status == "success" || response.status == "200") {
                val user = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(user)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menambahkan Admin"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menambahkan Admin. Periksa koneksi Anda."))
        }
    }

    override suspend fun updateUser(id: String, name: String?, email: String?, identifierNumber: String?): Result<User> {
        return try {
            val response = apiService.updateUser(id, name, email, identifierNumber)
            if (response.status == "success" || response.status == "200") {
                val user = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(user)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memperbarui pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui pengguna. Periksa koneksi Anda."))
        }
    }

    override suspend fun changeUserStatus(id: String, isActive: Boolean): Result<Unit> {
        return try {
            val response = apiService.changeUserStatus(id, isActive)
            if (response.status == "success" || response.status == "200") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengubah status pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengubah status pengguna. Periksa koneksi Anda."))
        }
    }

    override suspend fun deleteUserPermanently(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteUserPermanently(id)
            if (response.status == "success" || response.status == "200") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menghapus pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menghapus pengguna. Periksa koneksi Anda."))
        }
    }
}