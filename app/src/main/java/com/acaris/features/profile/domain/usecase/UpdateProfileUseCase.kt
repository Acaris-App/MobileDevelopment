package com.acaris.features.profile.domain.usecase

import com.acaris.features.profile.domain.model.UserProfile
import com.acaris.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        identifier: String,
        angkatan: Int? = null,
        ipk: Double? = null,
        semester: Int? = null
    ): Result<UserProfile> {
        if (name.isBlank() || email.isBlank() || identifier.isBlank()) {
            return Result.failure(Exception("Nama, Email, dan Identitas wajib diisi."))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Format email tidak valid."))
        }

        if (ipk != null && (ipk < 0.0 || ipk > 4.0)) {
            return Result.failure(Exception("Nilai IPK harus berada di antara 0.0 hingga 4.0"))
        }

        return repository.updateProfile(name, email, identifier, angkatan, ipk, semester)
    }
}