package com.zaz.peakringer.callscreen

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.zaz.peakringer.Constant
import com.zaz.peakringer.R
import com.zaz.peakringer.activity.main.MainActivity
import com.zaz.peakringer.receiver.StaticsBroadcast
import com.zaz.peakringer.repository.db.PRDbRepository
import com.zaz.peakringer.utils.isFeatureOpen
import com.zaz.support.config.NotificationChannelConfig
import com.zaz.support.config.NotificationConfig
import com.zaz.support.utils.isPermissionGranted
import com.zaz.support.utils.pickPhoneNum
import com.zaz.support.utils.showNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

class PRCallScreenService: CallScreeningService() {
    companion object{
        const val TAG = "PRCallScreenService"
    }
    override fun onScreenCall(details: Call.Details) {
        if(details.callDirection == Call.Details.DIRECTION_INCOMING && isFeatureOpen()){
            val phoneNumber = details.handle.schemeSpecificPart?.pickPhoneNum
            Log.d(TAG, "onScreenCall: phoneNum=${phoneNumber}")
            if(!phoneNumber.isNullOrBlank() && PRDbRepository.findContact(phoneNumber) != null){
                //将铃声调整到最大
                RingerSteamManager.maxSteam()
                if(isPermissionGranted(android.Manifest.permission.READ_PHONE_STATE)){
                    val tm = getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        //android12以上
                        tm.registerTelephonyCallback(Dispatchers.Main.asExecutor(),PhoneStateCallbackListener)
                    }else{
                        tm.listen(PrPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
                    }
                }else{
                    val notificationConfig = NotificationConfig(
                        R.mipmap.ic_launcher_round,
                        getString(R.string.app_name),
                        getString(R.string.no_recover_volume_permission),
                        Constant.NotificationId.NO_READ_PHONE_STATE_PERMISSION
                    ).apply {
                        onGoing = true
                        val installIntent =
                            Intent(applicationContext, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                        clickAction = PendingIntent.getActivity(
                            this@PRCallScreenService,
                            0,
                            installIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        btnYesAction = androidx.core.app.NotificationCompat.Action(
                            0,
                            getString(com.zaz.support.R.string.authorize),
                            clickAction
                        )
                        btnNoAction = androidx.core.app.NotificationCompat.Action(
                            0,
                            getString(com.zaz.support.R.string.cancel),
                            PendingIntent.getBroadcast(
                                this@PRCallScreenService,
                                0,
                                Intent(StaticsBroadcast.ACTION_CANCEL_NOTIFICATION).apply {
                                    putExtra(
                                        StaticsBroadcast.Notification_ID,
                                        notificationId
                                    )
                                    setPackage(packageName)
                                },
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                    }
                    val channelConfig = NotificationChannelConfig(
                        Constant.NotificationChannelId.READ_PHONE_STATE_PERMISSION,
                        getString(R.string.permission_notification),
                        NotificationManagerCompat.IMPORTANCE_HIGH
                    )
                  showNotification(notificationConfig,channelConfig)
                }
            }
            respondToCall(details,CallResponse.Builder().setDisallowCall(false).build()) //5s内要调用该方法
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    object PhoneStateCallbackListener: TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            Log.d(TAG, "PhoneStateCallbackListener/onCallStateChanged: state=$state")
            if(TelephonyManager.CALL_STATE_RINGING != state){
                RingerSteamManager.recover()
            }
        }
    }
    object PrPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            Log.d(TAG, "PrPhoneStateListener/onCallStateChanged: state=$state")
            if(TelephonyManager.CALL_STATE_RINGING != state){
                RingerSteamManager.recover()
            }
        }
    }
}