package com.acaris.features.auth.presentation.mapper

import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.presentation.model.UserPresentationModel

fun User.toPresentation(): UserPresentationModel {
    return UserPresentationModel(
        email = this.email,
        name = this.name,
        role = this.role,
        kodeKelas = this.kodeKelas
    )
}