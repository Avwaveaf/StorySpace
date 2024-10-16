package com.avwaveaf.storyspace.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create a DataStore instance using a property delegate
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id") // New key
        private val USER_NAME = stringPreferencesKey("user_name") // New key
    }

    suspend fun saveLoginSession(userId: String, name: String, token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId // Save user ID
            preferences[USER_NAME] = name // Save user name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear() // Clear all preferences
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val token: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN]
        }

    val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    val userName: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME]
        }
}
