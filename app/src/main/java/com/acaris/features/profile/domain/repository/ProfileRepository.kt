package com.acaris.features.profile.domain.repository

import com.acaris.features.profile.domain.model.UserProfile
import java.io.File

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>

    // 🌟 FIX: Tambahkan parameter baru di kontrak ini
    suspend fun updateProfile(
        name: String,
        email: String,
        identifier: String,
        angkatan: Int? = null,
        ipk: Double? = null,
        semester: Int? = null
    ): Result<UserProfile>

    suspend fun updateProfilePhoto(photoFile: File): Result<String>
}