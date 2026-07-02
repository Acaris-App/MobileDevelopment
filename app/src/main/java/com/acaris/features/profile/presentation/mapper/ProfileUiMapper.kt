package com.acaris.features.profile.presentation.mapper

import com.acaris.features.profile.domain.model.UserProfile
import com.acaris.features.profile.presentation.model.ProfileUiModel

fun UserProfile.toUiModel(): ProfileUiModel {
    val isMhs = this.role.lowercase() == "mahasiswa"

    return ProfileUiModel(
        id = this.id,
        name = this.name,
        email = this.email,
        identifier = this.identifier,
        identifierLabel = if (isMhs) "NPM" else "NIP",
        role = this.role,
        displayRole = this.role.replaceFirstChar { it.uppercase() },
        profilePictureUrl = this.profilePictureUrl ?: "",
        angkatan = this.angkatan?.toString() ?: "-",
        currentSemester = this.currentSemester?.toString() ?: "-",
        ipk = this.ipk?.toString() ?: "-",
        dosenPa = this.dosenPa ?: "-",
        isMahasiswa = isMhs,
        rawAngkatan = this.angkatan?.toString() ?: "",
        rawSemester = this.currentSemester?.toString() ?: "",
        rawIpk = this.ipk?.toString() ?: ""
    )
}