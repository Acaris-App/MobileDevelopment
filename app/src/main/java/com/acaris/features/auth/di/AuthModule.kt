package com.acaris.features.auth.di

import com.acaris.features.auth.data.remote.datasource.AuthApiService
import com.acaris.features.auth.data.repository.AuthRepositoryImpl
import com.acaris.features.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: AuthApiService): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }
}