package com.acaris.features.user_management.data.repository

import com.acaris.features.user_management.data.local.datasource.UserLocalDataSource
import com.acaris.features.user_management.data.mapper.toDomain
import com.acaris.features.user_management.data.remote.datasource.UserManagementApiService
import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.model.MahasiswaDocument
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserManagementRepositoryImpl @Inject constructor(
    private val apiService: UserManagementApiService,
    private val localDataSource: UserLocalDataSource
) : UserManagementRepository {

    // 🌟 Aliran data reaktif (Single Source of Truth)
    private val _usersFlow = MutableStateFlow<List<User>>(emptyList())
    override val usersFlow = _usersFlow.asStateFlow()

    private var currentUsersList = mutableListOf<User>()

    override suspend fun getUsers(role: String, search: String?, sortBy: String?, page: Int): Result<List<User>> {
        return try {
            val response = apiService.getUsers(role, search, sortBy, page)
            if (response.status == "success" || response.status == "200") {
                val list = response.data?.map { it.toDomain() } ?: emptyList()

                if (page == 1) currentUsersList.clear()
                currentUsersList.addAll(list)

                _usersFlow.emit(currentUsersList.toList())
                localDataSource.saveUsersToCache(currentUsersList)

                Result.success(list)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserDetail(id: String): Result<User> {
        val user = localDataSource.getUserFromCache(id)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Data pengguna tidak ditemukan di memori."))
        }
    }

    override suspend fun addAdmin(
        name: String,
        email: String,
        identifier: String,
        password: String,
        profilePicture: File?
    ): Result<User> {
        return try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val identifierBody = identifier.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

            var filePart: MultipartBody.Part? = null
            if (profilePicture != null) {
                val requestFile = profilePicture.asRequestBody("image/*".toMediaTypeOrNull())
                filePart = MultipartBody.Part.createFormData("profile_picture", profilePicture.name, requestFile)
            }

            val response = apiService.addAdmin(nameBody, emailBody, identifierBody, passwordBody, filePart)

            if (response.status == "success" || response.status == "200") {
                val user = response.data?.toDomain() ?: throw Exception("Data kosong")

                // 🌟 UPDATE LOKAL & REAKTIF: Masukkan Admin baru ke paling atas daftar
                currentUsersList.add(0, user)
                _usersFlow.emit(currentUsersList.toList())
                localDataSource.saveUsersToCache(currentUsersList)

                Result.success(user)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menambahkan Admin"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menambahkan Admin. Periksa koneksi Anda."))
        }
    }

    override suspend fun updateUser(
        id: String, name: String?, email: String?, identifierNumber: String?,
        angkatan: Int?, currentSemester: Int?, dosenPa: String?, kodeKelas: String?, ipk: Double?,
        profilePicture: File?
    ): Result<User> {
        return try {
            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email?.toRequestBody("text/plain".toMediaTypeOrNull())
            val nipBody = identifierNumber?.toRequestBody("text/plain".toMediaTypeOrNull())

            val angkatanBody = angkatan?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val semesterBody = currentSemester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val dosenPaBody = dosenPa?.toRequestBody("text/plain".toMediaTypeOrNull())
            val kelasBody = kodeKelas?.toRequestBody("text/plain".toMediaTypeOrNull())
            val ipkBody = ipk?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            var filePart: MultipartBody.Part? = null
            if (profilePicture != null) {
                val requestFile = profilePicture.asRequestBody("image/*".toMediaTypeOrNull())
                filePart = MultipartBody.Part.createFormData("profile_picture", profilePicture.name, requestFile)
            }

            val response = apiService.updateUser(
                id, nameBody, emailBody, nipBody,
                angkatanBody, semesterBody, dosenPaBody, kelasBody, ipkBody,
                filePart
            )

            if (response.status == "success" || response.status == "200") {
                val updatedUserDomain = response.data?.toDomain() ?: throw Exception("Data kosong")

                // 🌟 FIX: Menggunakan currentUsersList dan me-trigger update ke UI
                val index = currentUsersList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentUsersList[index] = updatedUserDomain
                    _usersFlow.emit(currentUsersList.toList()) // Teriak ke Observer (Daftar Pengguna)
                    localDataSource.saveUsersToCache(currentUsersList)
                }

                Result.success(updatedUserDomain)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memperbarui pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui pengguna. Periksa koneksi Anda."))
        }
    }

    override suspend fun getAllClasses(): Result<List<String>> {
        return try {
            val response = apiService.getAllClasses()
            if (response.status == "success") Result.success(response.data ?: emptyList())
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changeUserStatus(id: String, isActive: Boolean): Result<Unit> {
        return try {
            val response = apiService.changeUserStatus(id, isActive)
            if (response.status == "success" || response.status == "200") {

                val index = currentUsersList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentUsersList[index] = currentUsersList[index].copy(
                        status = if (isActive) "active" else "inactive"
                    )
                    _usersFlow.emit(currentUsersList.toList())
                    localDataSource.saveUsersToCache(currentUsersList)
                }

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

                // 🌟 Bonus: Otomatis hilangkan user dari UI saat dihapus
                val index = currentUsersList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentUsersList.removeAt(index)
                    _usersFlow.emit(currentUsersList.toList())
                    localDataSource.saveUsersToCache(currentUsersList)
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menghapus pengguna"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menghapus pengguna. Periksa koneksi Anda."))
        }
    }

    override suspend fun getMahasiswaDocuments(userId: String): Result<List<MahasiswaDocument>> {
        return try {
            val response = apiService.getMahasiswaDocuments(userId)
            if (response.status == "success" || response.status == "200") {
                val list = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil data dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil data dokumen. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun getBimbinganHistory(userId: String): Result<List<BimbinganHistory>> {
        return try {
            val response = apiService.getBimbinganHistory(userId)
            if (response.status == "success" || response.status == "200") {
                val list = response.data?.map { it.toDomain() } ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengambil riwayat bimbingan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil riwayat bimbingan. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun uploadMahasiswaDocument(userId: String, documentType: String, semester: Int?, file: File): Result<MahasiswaDocument> {
        return try {
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val typePart = documentType.toRequestBody("text/plain".toMediaTypeOrNull())
            val semesterPart = semester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadMahasiswaDocument(userId, typePart, semesterPart, filePart)
            if (response.status == "success" || response.status == "200") {
                val document = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(document)
            } else {
                Result.failure(Exception(response.message ?: "Gagal mengunggah dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengunggah dokumen. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun updateMahasiswaDocument(documentId: String, semester: Int?, file: File?): Result<MahasiswaDocument> {
        return try {
            val semesterPart = semester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            var filePart: MultipartBody.Part? = null
            if (file != null) {
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            }

            val response = apiService.updateMahasiswaDocument(documentId, semesterPart, filePart)
            if (response.status == "success" || response.status == "200") {
                val document = response.data?.toDomain() ?: throw Exception("Data kosong")
                Result.success(document)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memperbarui dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui dokumen. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun deleteMahasiswaDocument(documentId: String): Result<Unit> {
        return try {
            val response = apiService.deleteMahasiswaDocument(documentId)
            if (response.status == "success" || response.status == "200") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Gagal menghapus dokumen"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menghapus dokumen. Periksa koneksi internet Anda."))
        }
    }
}