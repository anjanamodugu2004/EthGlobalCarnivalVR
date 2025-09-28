package com.eth.vrcarnival.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthManager @Inject constructor(
    private val context: Context
) {
    private val tokenKey = stringPreferencesKey("auth_token")
    private val walletAddressKey = stringPreferencesKey("wallet_address")
    private val emailKey = stringPreferencesKey("email")

    val authFlow: Flow<AuthState> = context.dataStore.data
        .map { preferences ->
            AuthState(
                token = preferences[tokenKey],
                walletAddress = preferences[walletAddressKey],
                email = preferences[emailKey]
            )
        }

    suspend fun saveAuth(token: String, walletAddress: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[walletAddressKey] = walletAddress
            preferences[emailKey] = email
        }
    }

    suspend fun getCurrentUserEmail(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[emailKey]
        }.first()
    }

    suspend fun clearAuth() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class AuthState(
    val token: String? = null,
    val walletAddress: String? = null,
    val email: String? = null
) {
    val isAuthenticated: Boolean
        get() = !token.isNullOrBlank() && !walletAddress.isNullOrBlank()
}

