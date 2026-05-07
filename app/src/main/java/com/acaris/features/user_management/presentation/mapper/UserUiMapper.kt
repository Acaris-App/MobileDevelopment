package com.acaris.features.user_management.presentation.mapper

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.presentation.model.UserUiModel

fun User.toUiModel(): UserUiModel {
    return UserUiModel(
        id = this.id,
        name = this.name,
        email = this.email,
        role = this.role,
        identifier = this.identifier,
        isActive = this.status.lowercase() == "active",
        profilePictureUrl = this.profilePictureUrl,
        angkatan = this.angkatan,
        currentSemester = this.currentSemester,
        dosenPa = this.dosenPa,
        kodeKelas = this.kodeKelas,
        totalBimbingan = this.totalBimbingan,
        totalMahasiswa = this.totalMahasiswa,
        ipk = this.ipk
    )
}