package com.zaz.support.utils

import android.content.Context
import android.preference.PreferenceDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/********************DataStore*********************/
val Context.prDataStore: DataStore<Preferences> by preferencesDataStore(name = "pr_config")
val agreed_privacy =
    booleanPreferencesKey("agreed_privacy")