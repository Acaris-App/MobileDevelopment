package com.acaris.features.profile.presentation.model

import com.acaris.features.profile.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val userProfile: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)