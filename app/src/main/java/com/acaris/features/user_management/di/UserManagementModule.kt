package com.acaris.features.user_management.di

import com.acaris.features.user_management.data.remote.datasource.UserManagementApiService
import com.acaris.features.user_management.data.repository.UserManagementRepositoryImpl
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import com.acaris.features.user_management.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Modul ini akan hidup selama aplikasi berjalan
object UserManagementModule {

    @Provides
    @Singleton
    fun provideUserManagementApiService(retrofit: Retrofit): UserManagementApiService {
        return retrofit.create(UserManagementApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserManagementRepository(
        apiService: UserManagementApiService
    ): UserManagementRepository {
        return UserManagementRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideUserManagementUseCases(
        repository: UserManagementRepository
    ): UserManagementUseCases {
        return UserManagementUseCases(
            getUsers = GetUsersUseCase(repository),
            addAdmin = AddAdminUseCase(repository),
            updateUser = UpdateUserUseCase(repository),
            changeUserStatus = ChangeUserStatusUseCase(repository),
            deleteUser = DeleteUserUseCase(repository)
        )
    }
}