package com.acaris.features.auth.data.mapper

import com.acaris.features.auth.data.remote.model.UserRemoteModel
import com.acaris.features.auth.domain.model.User

fun UserRemoteModel.toDomain(token: String, role: String): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        role = role,
        token = token,
        kodeKelas = this.kodeKelas
    )
}