package com.zaz.update

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

class StartUpdateConfig {
    var context:Context?=null
    var callback: ((UpdateCheckResult,UpdateDownloadState, Long,Long) -> Unit)?=null
    var googleUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>?=null
}