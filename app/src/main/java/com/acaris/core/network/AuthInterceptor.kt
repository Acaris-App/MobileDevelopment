package com.acaris.core.network

import android.util.Log
import com.acaris.core.network.datastore.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val authEventBus: AuthEventBus
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { authPreferences.getAuthToken().first() }

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder().header("Authorization", "Bearer $token").build()
        } else originalRequest

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            Log.e("AuthInterceptor", "Token Expired! Menghapus sesi...")
            runBlocking {
                authPreferences.clearAuthSession()
                authEventBus.emitLogoutEvent()
            }
        }
        return response
    }
}