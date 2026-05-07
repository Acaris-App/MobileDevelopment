// File: di/UserManagementModule.kt
package com.acaris.features.user_management.di

import com.acaris.features.user_management.data.local.datasource.UserLocalDataSource
import com.acaris.features.user_management.data.local.datasource.UserLocalDataSourceImpl
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
@InstallIn(SingletonComponent::class)
object UserManagementModule {

    @Provides
    @Singleton
    fun provideUserManagementApiService(retrofit: Retrofit): UserManagementApiService {
        return retrofit.create(UserManagementApiService::class.java)
    }

    // 🌟 PENYEDIA GUDANG MEMORI LOKAL
    @Provides
    @Singleton
    fun provideUserLocalDataSource(): UserLocalDataSource {
        return UserLocalDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideUserManagementRepository(
        apiService: UserManagementApiService,
        localDataSource: UserLocalDataSource
    ): UserManagementRepository {
        return UserManagementRepositoryImpl(apiService, localDataSource)
    }

    @Provides
    @Singleton
    fun provideUserManagementUseCases(
        repository: UserManagementRepository
    ): UserManagementUseCases {
        return UserManagementUseCases(
            getUserDetail = GetUserDetailUseCase(repository),
            getUsers = GetUsersUseCase(repository),
            addAdmin = AddAdminUseCase(repository),
            updateUser = UpdateUserUseCase(repository),
            changeUserStatus = ChangeUserStatusUseCase(repository),
            deleteUser = DeleteUserUseCase(repository),
            getMahasiswaDocuments = GetMahasiswaDocumentsUseCase(repository),
            getBimbinganHistory = GetBimbinganHistoryUseCase(repository),
            uploadMahasiswaDocument = UploadMahasiswaDocumentUseCase(repository),
            updateMahasiswaDocument = UpdateMahasiswaDocumentUseCase(repository),
            deleteMahasiswaDocument = DeleteMahasiswaDocumentUseCase(repository)
        )
    }
}