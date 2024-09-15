package com.zaz.google

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.requestCompleteUpdate
import com.google.android.play.core.ktx.totalBytesToDownload
import com.zaz.update.IUpdate
import com.zaz.update.StartUpdateConfig
import com.zaz.update.UpdateCheckResult
import com.zaz.update.UpdateDownloadState

object GPAppUpdate : IUpdate {
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateInfo: AppUpdateInfo
    private var updateCheckResult = UpdateCheckResult.NO_UPDATE
    private const val TAG = "AppUpdate"
    private var downloadCallback: ((UpdateCheckResult, UpdateDownloadState, Long, Long) -> Unit)? =
        null
    private val listener = InstallStateUpdatedListener {
        Log.d(TAG, "InstallStateUpdatedListener,state=${it.installStatus},bytesDownloaded=${it.bytesDownloaded},totalBytesToDownload=${it.totalBytesToDownload}")
        when (it.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                //下载中
                downloadCallback?.invoke(
                    updateCheckResult,
                    UpdateDownloadState.DOWNLOADING,
                    it.bytesDownloaded(),
                    it.totalBytesToDownload()
                )
            }

            InstallStatus.DOWNLOADED -> {
                //下载完成
                downloadCallback?.invoke(
                    updateCheckResult,
                    UpdateDownloadState.SUCCEED,
                    it.bytesDownloaded(),
                    it.totalBytesToDownload()
                )
            }

            InstallStatus.CANCELED -> {
                //取消下载
                downloadCallback?.invoke(
                    updateCheckResult,
                    UpdateDownloadState.CANCELED,
                    it.bytesDownloaded(),
                    it.totalBytesToDownload()
                )
            }

            InstallStatus.FAILED -> {
                //下载失败
                downloadCallback?.invoke(
                    updateCheckResult,
                    UpdateDownloadState.FAILED,
                    it.bytesDownloaded(),
                    it.totalBytesToDownload()
                )
            }
        }
    }

    override fun checkUpdate(context: Context, resultCallback: (UpdateCheckResult) -> Unit) {
        appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(TAG, "checkUpdate: success listener")
            this.appUpdateInfo = appUpdateInfo
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                //有可用更新
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    //强制更新
                    updateCheckResult = UpdateCheckResult.UPDATE_IMMEDIATE
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    //灵活更新
                    if ((appUpdateInfo.clientVersionStalenessDays() ?: Int.MAX_VALUE) >= 15) {
                        //超过15还未更新，强制更新
                        updateCheckResult = UpdateCheckResult.UPDATE_IMMEDIATE
                    } else {
                        updateCheckResult = UpdateCheckResult.UPDATE_FLEX
                    }
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                updateCheckResult = UpdateCheckResult.UPDATE_STOPPED
            } else {
                updateCheckResult = UpdateCheckResult.NO_UPDATE
            }
            Log.d(TAG, "checkUpdate: updateCheckResult=$updateCheckResult")
            resultCallback(updateCheckResult)
        }
    }

    override fun startUpdate(type: UpdateCheckResult, startUpdateConfig: StartUpdateConfig) {
        Log.d(TAG, "startUpdate: type=$type")
        downloadCallback = startUpdateConfig.callback
        if (type == UpdateCheckResult.UPDATE_FLEX) {
            //灵活更新
            appUpdateManager.unregisterListener(listener)
            appUpdateManager.registerListener(listener)
            startUpdate(AppUpdateType.FLEXIBLE, startUpdateConfig.googleUpdateLauncher!!)
        } else if (type == UpdateCheckResult.UPDATE_IMMEDIATE || type == UpdateCheckResult.UPDATE_STOPPED) {
            //强制更新
            startUpdate(AppUpdateType.IMMEDIATE, startUpdateConfig.googleUpdateLauncher!!)
        } else {
            throw IllegalArgumentException("update type not UPDATE_FLEX/UPDATE_IMMEDIATE/UPDATE_STOPPED")
        }
    }

    override fun install(context: Context): Boolean {
        return if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
            Log.d(TAG, "install: installStatus=${appUpdateInfo.installStatus()}")
            appUpdateManager.completeUpdate()
            true
        } else {
            false
        }
    }

    private fun startUpdate(
        type: Int,
        googleUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // an activity result launcher registered via registerForActivityResult
            googleUpdateLauncher,
            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
            // flexible updates.
            AppUpdateOptions.newBuilder(type).build()
        )
    }
}