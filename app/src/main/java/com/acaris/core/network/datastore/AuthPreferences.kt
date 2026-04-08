package com.acaris.core.network.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Membuat instance DataStore bernama "acaris_auth_prefs"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "acaris_auth_prefs")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Kunci brankas untuk menyimpan data
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
    }

    // Fungsi untuk MENGAMBIL Token (Berupa Flow agar bisa diobservasi secara reaktif)
    fun getAuthToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    // Fungsi untuk MENYIMPAN Token dan Role setelah sukses login
    suspend fun saveAuthSession(token: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROLE_KEY] = role
        }
    }

    // Fungsi untuk MENGHAPUS Token (Digunakan saat Logout)
    suspend fun clearAuthSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}