package com.zaz.peakringer.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.zaz.google.GPAppUpdate
import com.zaz.peakringer.Constant
import com.zaz.peakringer.activity.main.MainActivity
import com.zaz.peakringer.R
import com.zaz.support.config.NotificationChannelConfig
import com.zaz.support.config.NotificationConfig
import com.zaz.support.dialog.PRDialog
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.showNotification
import com.zaz.update.IUpdate
import com.zaz.update.StartUpdateConfig
import com.zaz.update.UpdateCheckResult
import com.zaz.update.UpdateDownloadState

object UpdateUtils : IUpdate {
    private const val TAG = "UpdateUtils"
    private val instance: IUpdate = GPAppUpdate

    override  fun checkUpdate(context: Context, resultCallback: (UpdateCheckResult) -> Unit) {
        instance.checkUpdate(context, resultCallback)
    }

    override fun startUpdate(type: UpdateCheckResult, startUpdateConfig: StartUpdateConfig) {
        instance.startUpdate(type, startUpdateConfig)
    }

    override fun install(context: Context): Boolean {
        val installResult = instance.install(context)
        Log.d(TAG, "newVer installResult: $installResult")
        with(context) {
            if (!installResult) {
                PRToast.show(
                    applicationContext,
                    getString(R.string.err_retry, Constant.ErrorCode.ERR_INSTALL_NEW_VER)
                )
            }
        }
        return installResult
    }


    fun showDownloadedNotification(
        context: Context,
        downloadState: UpdateDownloadState,
        byteDownload: Long,
        byteNeedDown: Long,
        callback:((UpdateDownloadState)->Unit)?
    ) {
        val process = byteDownload * 100f / byteNeedDown
        when (downloadState) {
            UpdateDownloadState.DOWNLOADING -> {
                showDownloadingNotification(context, process)
            }

            UpdateDownloadState.PAUSED -> {

            }

            UpdateDownloadState.CANCELED ,
            UpdateDownloadState.FAILED ,
            UpdateDownloadState.SUCCEED -> {
                showSucceedNotification(context,downloadState,callback)
            }

            else -> {

            }
        }

    }

    private fun showDownloadingNotification(context: Context, process: Float) {
        with(context) {
            val notificationConfig = NotificationConfig(
                R.mipmap.ic_launcher_round,
                getString(R.string.app_name),
                getString(R.string.new_version_downloading),
                Constant.NotificationId.APP_UPDATE
            ).apply {
                val installIntent =
                    Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                clickAction = PendingIntent.getActivity(
                    context,
                    0,
                    installIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                currentProgress = process.toInt()
            }

            val channelConfig = NotificationChannelConfig(
                Constant.NotificationChannelId.APP_UPDATE,
                getString(R.string.app_update_notification),
                NotificationManagerCompat.IMPORTANCE_HIGH
            )
            showNotification(notificationConfig, channelConfig)
        }
    }


    private fun showSucceedNotification(context: Context, downloadState: UpdateDownloadState, callback:((UpdateDownloadState)->Unit)?) {
        with(context) {
            if (context is FragmentActivity) {
                val builder = PRDialog.Builder()
                    .setTitle(getString(R.string.tips))
                    .setLeftBtnName(getString(R.string.cancel))
                    .setRightBtnListener {
                        NotificationManagerCompat.from(context)
                            .cancel(Constant.NotificationId.APP_UPDATE)
                        callback?.invoke(downloadState)
                    }
                if (downloadState == UpdateDownloadState.SUCCEED) {
                    builder.setContent(getString(R.string.install_new_ver))
                        .setRightBtnName(getString(R.string.install))

                } else {
                    builder.setContent(getString(R.string.download_fail_click_retry))
                        .setRightBtnName(getString(R.string.retry))
                }

                builder.show(context.supportFragmentManager)
            }


            val notificationConfig = NotificationConfig(
                R.mipmap.ic_launcher_round,
                getString(R.string.app_name),
                getString(R.string.new_version_can_installed),
                Constant.NotificationId.APP_UPDATE
            ).apply {
                onGoing = false
                val installIntent =
                    Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        if (downloadState == UpdateDownloadState.SUCCEED){
                            putExtra("installNewVer", true)
                        }else{
                            putExtra("retry", true)
                        }
                    }
                val installAction = PendingIntent.getActivity(
                    context,
                    0,
                    installIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                btnYesAction = androidx.core.app.NotificationCompat.Action(
                    0,
                    if(downloadState == UpdateDownloadState.SUCCEED) getString(R.string.install) else getString(R.string.retry),
                    installAction
                )
            }

            val channelConfig = NotificationChannelConfig(
                Constant.NotificationChannelId.APP_UPDATE,
                getString(R.string.app_update_notification),
                NotificationManagerCompat.IMPORTANCE_HIGH
            )

            showNotification(notificationConfig, channelConfig)
        }
    }
}