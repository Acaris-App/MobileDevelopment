package com.acaris.features.auth.presentation.mapper

import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.presentation.model.UserUiModel

// 🌟 REVISI: Langsung map ke UserUiModel
fun User.toPresentation(): UserUiModel {
    return UserUiModel(
        email = this.email,
        name = this.name,
        role = this.role,
        kodeKelas = this.kodeKelas
    )
}