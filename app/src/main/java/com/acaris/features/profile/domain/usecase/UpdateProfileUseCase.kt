package com.acaris.features.profile.domain.usecase

import com.acaris.features.profile.domain.model.UserProfile
import com.acaris.features.profile.domain.repository.ProfileRepository
import java.time.Year
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

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.failure(Exception("Format email tidak valid."))
        }

        if (ipk != null && (ipk < 0.0 || ipk > 4.0)) {
            return Result.failure(Exception("Nilai IPK harus berada di antara 0.0 hingga 4.0"))
        }

        if (semester != null && (semester < 1 || semester > 14)) {
            return Result.failure(Exception("Semester tidak valid. Harap masukkan semester antara 1 - 14."))
        }

        if (angkatan != null) {
            val currentYear = Year.now().value
            if (angkatan < 1990 || angkatan > currentYear) {
                return Result.failure(Exception("Tahun angkatan tidak valid. Harap masukkan tahun yang benar."))
            }
        }

        return repository.updateProfile(name, email, identifier, angkatan, ipk, semester)
    }
}