package com.zaz.support.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/********************DataStore*********************/
val Context.prDataStore: DataStore<Preferences> by preferencesDataStore(name = "pr_config")
val DATA_STORE_KEY_INSTALL_VERSION =
    intPreferencesKey("data_store_key_install_version")