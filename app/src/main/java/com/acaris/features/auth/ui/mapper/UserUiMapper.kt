package com.acaris.features.auth.ui.mapper

import com.acaris.features.auth.presentation.model.UserPresentationModel
import com.acaris.features.auth.ui.model.UserUiModel

fun UserPresentationModel.toUiModel(): UserUiModel {
    return UserUiModel(
        name = this.name,
        role = this.role,
        kodeKelas = this.kodeKelas
    )
}