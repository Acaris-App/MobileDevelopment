package com.acaris.features.user_management.data.mapper

import com.acaris.features.user_management.data.remote.model.ClassInfoResponse
import com.acaris.features.user_management.data.remote.model.UserResponse
import com.acaris.features.user_management.domain.model.ClassInfo
import com.acaris.features.user_management.domain.model.User

fun UserResponse.toDomain(): User {
    return User(
        id = this.id?.toString() ?: "",
        name = this.name.orEmpty(),
        email = this.email.orEmpty(),
        role = this.role.orEmpty(),
        identifier = this.identifier.orEmpty(),
        status = this.status ?: "active",
        profilePictureUrl = this.profilePictureUrl,
        angkatan = this.angkatan,
        currentSemester = this.currentSemester,
        dosenPa = this.dosenPa,
        kodeKelas = this.kodeKelas,
        ipk = this.ipk?.toDoubleOrNull(),
        totalBimbingan = this.totalBimbingan,
        totalMahasiswa = this.totalMahasiswa
    )
}

fun ClassInfoResponse.toDomain(): ClassInfo {
    return ClassInfo(
        kodeKelas = this.kodeKelas.orEmpty(),
        dosenPa = this.dosenPa.orEmpty()
    )
}