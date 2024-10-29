package com.magnise.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token_key")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_key")

}