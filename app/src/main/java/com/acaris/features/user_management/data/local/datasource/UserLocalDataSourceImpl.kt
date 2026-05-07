package com.acaris.features.user_management.data.local.datasource

import com.acaris.features.user_management.domain.model.User

class UserLocalDataSourceImpl : UserLocalDataSource {
    private var cachedUsers: List<User> = emptyList()

    override fun saveUsersToCache(users: List<User>) {
        cachedUsers = users
    }

    override fun getUserFromCache(id: String): User? {
        return cachedUsers.find { it.id == id }
    }
}