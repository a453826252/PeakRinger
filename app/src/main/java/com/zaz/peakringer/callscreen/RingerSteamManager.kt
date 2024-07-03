package com.zaz.peakringer.callscreen

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.zaz.peakringer.PRApp

object RingerSteamManager {
    private const val TAG = "RingerSteamManager"
    private val mAudioManager: AudioManager = PRApp.application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mCurrentVolume = -1
    private var mCurrentRingModel = -1
    /**
     * 将铃声音量调整至最大
     */
    fun maxSteam(){
        Log.d(TAG, "maxSteam: ")
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING)
        mCurrentRingModel = mAudioManager.ringerMode
        val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        mAudioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, max, AudioManager.FLAG_SHOW_UI)
    }
    
    /**
     * 恢复来电前的铃声状态
     */
    fun recover(){
        Log.d(TAG, "recover: ")
        if(mCurrentVolume != -1 && mCurrentRingModel != -1){
            mAudioManager.ringerMode = mCurrentRingModel
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mCurrentVolume, AudioManager.FLAG_SHOW_UI)
            reset()
        }
    }

    private fun reset(){
        mCurrentVolume = -1
        mCurrentRingModel = -1
    }
}