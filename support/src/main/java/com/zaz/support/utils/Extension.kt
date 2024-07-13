package com.zaz.support.utils

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.zaz.support.Clone
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
    requireActivity().supportFragmentManager.commit {
        remove(this@finishFragment)
    }
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

