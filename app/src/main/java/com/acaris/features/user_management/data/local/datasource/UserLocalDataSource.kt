package com.acaris.features.user_management.data.local.datasource

import com.acaris.features.user_management.domain.model.User

interface UserLocalDataSource {
    fun saveUsersToCache(users: List<User>)
    fun getUserFromCache(id: String): User?
}

