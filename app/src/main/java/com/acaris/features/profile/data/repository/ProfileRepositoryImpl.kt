package com.acaris.features.profile.data.repository

import com.acaris.features.profile.data.mapper.toDomain
import com.acaris.features.profile.data.remote.datasource.ProfileApiService
import com.acaris.features.profile.data.remote.model.UpdateProfileRequest
import com.acaris.features.profile.domain.model.UserProfile
import com.acaris.features.profile.domain.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = apiService.getProfile()
            if (response.status == "success" || response.status == "200") {
                val userProfile = response.data?.toDomain() ?: throw Exception("Data profil kosong.")
                Result.success(userProfile)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil data profil. Periksa koneksi Anda."))
        }
    }

    override suspend fun updateProfile(
        name: String,
        email: String,
        identifier: String,
        angkatan: Int?,
        ipk: Double?,
        semester: Int?
    ): Result<UserProfile> {
        return try {
            val request = UpdateProfileRequest(
                name = name,
                email = email,
                identifier = identifier,
                angkatan = angkatan,
                ipk = ipk,
                currentSemester = semester
            )
            val response = apiService.updateProfile(request)

            if (response.status == "success" || response.status == "200") {
                val updatedProfile = response.data?.toDomain() ?: throw Exception("Data profil gagal diperbarui.")
                Result.success(updatedProfile)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui profil. Periksa koneksi Anda."))
        }
    }

    override suspend fun updateProfilePhoto(photoFile: File): Result<String> {
        return try {
            // Tentukan tipe file gambar
            val mimeType = when (photoFile.extension.lowercase()) {
                "png" -> "image/png"
                "webp" -> "image/webp"
                else -> "image/jpeg"
            }

            val requestFile = photoFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("profile_picture", photoFile.name, requestFile)

            val response = apiService.updateProfilePhoto(filePart)

            if (response.status == "success" || response.status == "200") {
                val newPhotoUrl = response.data?.profilePictureUrl ?: throw Exception("URL foto tidak ditemukan.")
                Result.success(newPhotoUrl)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengunggah foto. Periksa koneksi Anda."))
        }
    }
}