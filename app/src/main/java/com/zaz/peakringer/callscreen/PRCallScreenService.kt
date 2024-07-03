package com.zaz.peakringer.callscreen

import android.app.Service
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.zaz.peakringer.repository.db.PRDbRepository
import com.zaz.support.utils.pickPhoneNum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

class PRCallScreenService: CallScreeningService() {
    companion object{
        const val TAG = "PRCallScreenService"
    }
    override fun onScreenCall(details: Call.Details) {
        if(details.callDirection != Call.Details.DIRECTION_INCOMING){
            return
        }
        val phoneNumber = details.handle.schemeSpecificPart?.pickPhoneNum
        Log.d(TAG, "onScreenCall: phoneNum=${phoneNumber}")
        if(!phoneNumber.isNullOrBlank() && PRDbRepository.findContact(phoneNumber) != null){
            //将铃声调整到最大
            RingerSteamManager.maxSteam()
            val tm = getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //android12以上
                tm.registerTelephonyCallback(Dispatchers.Main.asExecutor(),PhoneStateCallbackListener)
            }else{
                tm.listen(PrPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }

        }
        respondToCall(details,CallResponse.Builder().setDisallowCall(false).build()) //5s内要调用该方法
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    object PhoneStateCallbackListener: TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            Log.d(TAG, "PhoneStateCallbackListener/onCallStateChanged: state=$state")
            if(TelephonyManager.CALL_STATE_IDLE == state){
                RingerSteamManager.recover()
            }
        }
    }
    object PrPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            Log.d(TAG, "PrPhoneStateListener/onCallStateChanged: state=$state")
            if(TelephonyManager.CALL_STATE_IDLE == state){
                RingerSteamManager.recover()
            }
        }
    }
}