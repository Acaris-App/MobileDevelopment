package com.acaris.features.profile.data.mapper

import com.acaris.features.profile.data.remote.model.ProfileResponse
import com.acaris.features.profile.domain.model.UserProfile

fun ProfileResponse.toDomain(): UserProfile {
    return UserProfile(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        email = this.email.orEmpty(),
        role = this.role ?: "mahasiswa",
        identifier = this.identifier.orEmpty(),
        profilePictureUrl = this.profilePicture,
        angkatan = this.angkatan,
        ipk = this.ipk?.toDoubleOrNull(),
        currentSemester = this.currentSemester,
        dosenPa = this.dosenPa ?: "Belum diatur"
    )
}