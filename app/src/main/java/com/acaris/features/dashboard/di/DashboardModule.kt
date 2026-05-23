package com.acaris.features.dashboard.di

import com.acaris.features.dashboard.data.remote.datasource.DashboardApiService
import com.acaris.features.dashboard.data.repository.DashboardRepositoryImpl
import com.acaris.features.dashboard.domain.repository.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService {
        return retrofit.create(DashboardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(apiService: DashboardApiService): DashboardRepository {
        return DashboardRepositoryImpl(apiService)
    }
}