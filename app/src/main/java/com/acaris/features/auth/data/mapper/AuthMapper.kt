package com.acaris.features.auth.data.mapper

import com.acaris.features.auth.data.remote.model.UserRemoteModel
import com.acaris.features.auth.domain.model.User

fun UserRemoteModel.toDomain(token: String): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        role = this.role,
        token = token
    )
}