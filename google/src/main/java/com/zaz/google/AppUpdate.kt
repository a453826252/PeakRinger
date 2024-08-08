package com.zaz.google

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

object AppUpdate {
    const val APP_UPDATE_IMMEDIATE = AppUpdateType.IMMEDIATE
    const val APP_UPDATE_FLEX = AppUpdateType.FLEXIBLE

    private const val TAG = "AppUpdate"
    private const val CHANNEL_ID = "app_update"
    private val listener = InstallStateUpdatedListener {
        when (it.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                //下载中
            }

            InstallStatus.DOWNLOADED -> {
                //下载完成
            }

            InstallStatus.CANCELED -> {
                //取消下载
            }

            InstallStatus.FAILED -> {
                //下载失败
            }
        }
    }

    fun checkUpdate(context: Context, updateAvailability: (Int) -> Unit) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                //有可用更新
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    //强制更新
                    updateAvailability(APP_UPDATE_IMMEDIATE)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    //灵活更新
                    if ((appUpdateInfo.clientVersionStalenessDays() ?: Int.MAX_VALUE) >= 15) {
                        //超过15还未更新，强制更新
                        updateAvailability(APP_UPDATE_IMMEDIATE)
                    } else {
                        updateAvailability(APP_UPDATE_FLEX)
                    }
                }
            }
        }
    }

    private fun forceUpdate(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo, activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // an activity result launcher registered via registerForActivityResult
            activityResultLauncher,
            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
            // flexible updates.
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                .build())
    }

    private fun flexUpdate(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo, activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        appUpdateManager.unregisterListener(listener)
        appUpdateManager.registerListener(listener)
    }

    private fun notify(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            Log.e(TAG, "notify not enabled")
            return
        }
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.google_update_notify_name), NotificationManager.IMPORTANCE_HIGH)
        with(notificationManager) {
            createNotificationChannel(channel)
        }

    }
}