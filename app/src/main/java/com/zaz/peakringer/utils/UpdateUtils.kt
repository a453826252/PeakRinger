package com.zaz.peakringer.utils

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zaz.peakringer.Constants
import com.zaz.peakringer.MainActivity
import com.zaz.peakringer.R
import com.zaz.support.config.NotificationChannelConfig
import com.zaz.support.config.NotificationConfig
import com.zaz.support.utils.showNotification

object UpdateUtils {
    fun checkUpdate(context: Context){
        val notificationConfig = NotificationConfig(
            R.mipmap.ic_logo_round,
            context.getString(R.string.app_name),
            context.getString(R.string.new_version_can_installed),
            Constants.NotificationId.APP_UPDATE
        ).apply {
            onGoing = true
            val installIntent = Intent(context.applicationContext,MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("checkUpdate",true)
            }
            val installAction = PendingIntent.getActivity(context,0,installIntent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            btnAction = NotificationCompat.Action(0,context.getString(R.string.install),installAction)
        }

        val channelConfig = NotificationChannelConfig(
            Constants.NotificationChannelId.APP_UPDATE,
            context.getString(R.string.app_update_notification),
            NotificationManagerCompat.IMPORTANCE_HIGH
            )

        context.showNotification(notificationConfig, channelConfig)
    }
}