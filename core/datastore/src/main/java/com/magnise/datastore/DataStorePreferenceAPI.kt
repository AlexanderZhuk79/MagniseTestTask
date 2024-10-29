package com.magnise.datastore

import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.Preferences

interface DataStorePreferenceAPI {
    suspend fun<T> getPreference(key: Preferences.Key<T>, defaultValue : T): Flow<T>
    suspend fun<T> putPreference(key: Preferences.Key<T>, value: T)
    suspend fun<T> removePreference(key: Preferences.Key<T>)
    suspend fun clearAllPreference()
    suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T?): T?
}