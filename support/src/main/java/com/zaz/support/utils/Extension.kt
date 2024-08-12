package com.zaz.support.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.zaz.support.Clone
import com.zaz.support.config.NotificationChannelConfig
import com.zaz.support.config.NotificationConfig
import java.io.File


/**Density**/
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
val Int.dp
    get() = this.toFloat().dp

/**Resource**/
fun Int.string(context: Context,vararg obj:Any):String = context.getString(this,*obj)

fun Context.gotoActivity(target:Class<*>){
    val intent = Intent(this,target)
    if(this !is Activity){
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

@SuppressLint("MissingPermission")
fun Context.showNotification(notificationConfig: NotificationConfig, channelConfig:NotificationChannelConfig){
    val notificationManagerCompat = NotificationManagerCompat.from(this)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManagerCompat.areNotificationsEnabled()){
        Log.e("showNotification", "showNotification: no permission")
        return
    }

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(channelConfig.channelId, channelConfig.channelName, channelConfig.channelImportance)
        if(channelConfig.channelDesc.isNotBlank()){
            channel.description = channelConfig.channelDesc
        }
        if(channelConfig.channelImportance >= NotificationManagerCompat.IMPORTANCE_HIGH){
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManagerCompat.createNotificationChannel(channel)
    }
   val notificationBuilder = NotificationCompat.Builder(this,channelConfig.channelId)
        .setSmallIcon(notificationConfig.smallIcon)
        .setContentTitle(notificationConfig.title)
        .setContentText(notificationConfig.content)
        .setAutoCancel(true)
       .setOngoing(notificationConfig.onGoing)
    if(notificationConfig.currentProgress != null){
        notificationBuilder.setAutoCancel(false)
            .setOngoing(true)
            .setProgress(100,notificationConfig.currentProgress!!,false)
    }
    if(notificationConfig.btnAction != null){
        notificationBuilder.addAction(notificationConfig.btnAction)
    }
    if(notificationConfig.clickAction != null){
        notificationBuilder.setContentIntent(notificationConfig.clickAction)
    }else if(notificationConfig.btnAction != null){
        notificationBuilder.setContentIntent(notificationConfig.btnAction!!.actionIntent)
    }
    val notification = notificationBuilder.build()
    notificationManagerCompat.notify(notificationConfig.notificationId,notification)
}

val Context.myVerCode: Int
    get() = this.packageManager.getPackageInfo(this.packageName,0).versionCode


fun Int.color(context: Context):Int = ContextCompat.getColor(context,this)


fun <T> List<T>.deepClone():List<T>{
    val result = mutableListOf<T>()
    this.forEach {
        if(it is Clone){
            result.add(it.clone() as T)
        }
    }
    return result
}

fun Fragment.finishFragment(){
    requireActivity().supportFragmentManager.popBackStack()
}
fun Fragment.finishActivity(){
    requireActivity().finish()
}

val String.pickPhoneNum
    get() =this.filter { it.isDigit() || it == '+' }

fun File.toUri(context:Context):Uri{
    return FileProvider.getUriForFile(context,"${context.packageName}.fileprovider",this)
}
fun File.insertToMedia(context: Context,mimeType:String):Uri?{
    val values = ContentValues();
    values.put(MediaStore.Images.Media.DATA, absolutePath)
    values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}

