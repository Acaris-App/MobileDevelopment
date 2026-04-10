package com.acaris.features.profile.di

import com.acaris.features.profile.data.remote.datasource.ProfileApiService
import com.acaris.features.profile.data.repository.ProfileRepositoryImpl
import com.acaris.features.profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ProfileApiService
    ): ProfileRepository {
        return ProfileRepositoryImpl(apiService)
    }
}