package com.zaz.peakringer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zaz.support.config.NotificationConfig

class StaticsBroadcast: BroadcastReceiver() {
    companion object{
        const val Notification_ID = "notification_id"
        const val ACTION_CANCEL_NOTIFICATION = "com.peakringer.cancel_notification"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null) return
        val action = intent?.action
        action?.let {
            when(it){
                ACTION_CANCEL_NOTIFICATION->{
                    val id = intent.getIntExtra(Notification_ID,0)
                    if(id != 0){
                        NotificationManagerCompat.from(context).cancel(id)
                    }
                }
            }
        }
    }
}