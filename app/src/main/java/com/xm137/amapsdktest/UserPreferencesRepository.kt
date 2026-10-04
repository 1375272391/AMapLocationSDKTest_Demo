package com.xm137.amapsdktest

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_A_MAP = booleanPreferencesKey("is_a_map")
        const val TAG = "UserPreferencesRepo"
    }

    val isAMapPrivacyAgree : Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_A_MAP] ?: false
        }

    suspend fun saveIsAMapPrivacy(aMapPrivacyAgree: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_A_MAP] = aMapPrivacyAgree
        }
    }
}