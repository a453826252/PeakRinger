package com.zaz.support.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/********************DataStore*********************/
val Context.prConfig: DataStore<Preferences> by preferencesDataStore(name = "pr_config")
val requestRolePermissionDialogNotShowAgain =
    booleanPreferencesKey("requestRolePermissionDialogNotShowAgain")


/**Density**/
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
val Int.dp
    get() = this.toFloat().dp

fun Int.string(context: Context,vararg obj:Any):String = context.getString(this,obj)

