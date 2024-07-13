package com.zaz.support.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/********************DataStore*********************/
val Context.prConfig: DataStore<Preferences> by preferencesDataStore(name = "pr_config")
val DATA_STORE_KEY_INSTALL_VERSION =
    booleanPreferencesKey("data_store_key_install_version")