package com.zaz.support.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    suspend fun saveBitmap(context: Context,bitmap: Bitmap,path:String):Uri{
        val target = File(path)
        target.parentFile?.mkdirs()
       return withContext(Dispatchers.IO) {
            FileOutputStream(target).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
            }
           target.toUri(context)
        }
    }
}