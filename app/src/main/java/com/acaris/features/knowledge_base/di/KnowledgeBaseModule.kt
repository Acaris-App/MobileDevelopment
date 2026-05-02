package com.acaris.features.knowledge_base.di

import com.acaris.features.knowledge_base.data.remote.datasource.KnowledgeApiService
import com.acaris.features.knowledge_base.data.repository.KnowledgeRepositoryImpl
import com.acaris.features.knowledge_base.domain.repository.KnowledgeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KnowledgeBaseModule {

    @Provides
    @Singleton
    fun provideKnowledgeApiService(retrofit: Retrofit): KnowledgeApiService {
        return retrofit.create(KnowledgeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideKnowledgeRepository(
        apiService: KnowledgeApiService
    ): KnowledgeRepository {
        return KnowledgeRepositoryImpl(apiService)
    }
}