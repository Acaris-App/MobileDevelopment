package com.acaris.features.profile.domain.usecase

import com.acaris.features.profile.domain.repository.ProfileRepository
import java.io.File
import javax.inject.Inject

class UpdatePhotoUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(photoFile: File): Result<String> {
        if (!photoFile.exists() || photoFile.length() == 0L) {
            return Result.failure(Exception("File foto tidak ditemukan atau kosong."))
        }

        if (photoFile.length() > 2097152) {
            return Result.failure(Exception("Ukuran foto maksimal adalah 2 MB."))
        }

        val validExtensions = listOf("jpg", "jpeg", "png", "webp")
        if (photoFile.extension.lowercase() !in validExtensions) {
            return Result.failure(Exception("Format foto harus JPG, PNG, atau WEBP."))
        }

        return repository.updateProfilePhoto(photoFile)
    }
}