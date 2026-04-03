package com.acaris.features.auth.ui.mapper

import com.acaris.features.auth.presentation.model.UserPresentationModel
import com.acaris.features.auth.ui.model.UserUiModel

/**
 * Memetakan Presentation Model menjadi UI Model.
 */
fun UserPresentationModel.toUiModel(): UserUiModel {
    return UserUiModel(
        name = this.name,
        role = this.role
    )
}