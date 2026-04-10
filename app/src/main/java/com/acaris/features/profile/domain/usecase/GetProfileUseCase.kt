package com.acaris.features.profile.domain.usecase

import com.acaris.features.profile.domain.model.UserProfile
import com.acaris.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getProfile()
    }
}