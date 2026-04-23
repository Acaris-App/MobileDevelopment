package com.acaris.features.schedule.di

import com.acaris.features.schedule.data.remote.datasource.ScheduleApiService
import com.acaris.features.schedule.data.repository.ScheduleRepositoryImpl
import com.acaris.features.schedule.domain.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {

    @Provides
    @Singleton
    fun provideScheduleApiService(retrofit: Retrofit): ScheduleApiService {
        return retrofit.create(ScheduleApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(apiService: ScheduleApiService): ScheduleRepository {
        return ScheduleRepositoryImpl(apiService)
    }
}