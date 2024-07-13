package com.zaz.support.acitivityresultcontract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.zaz.support.utils.insertToMedia
import java.io.File

class CropImage: ActivityResultContract<CropImage.CropRequest, Uri?>() {
    companion object{
        const val TAG = "CropImage"
    }
    override fun createIntent(context: Context, input: CropRequest): Intent {
        val cropIntent = Intent("com.android.camera.action.CROP")
        return cropIntent.apply {
            setDataAndType(input.srcUri,"image/*")
            putExtra("crop",true)
            putExtra("scale",true)
            putExtra("aspectX",input.aspectX)
            putExtra("aspectY",input.aspectY)
            putExtra("outputX",input.outputX)
            putExtra("outputY",input.outputY)
            putExtra("return-data",true)
            putExtra("noFaceDetection",true)
            putExtra("outputFormat",Bitmap.CompressFormat.PNG.toString())
            putExtra(MediaStore.EXTRA_OUTPUT,input.target.insertToMedia(context,"image/*"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        Log.d(TAG, "parseResult: resultCode=$resultCode,intentData=${intent?.data}")
        if(resultCode == Activity.RESULT_OK){
            return intent?.data
        }
        return null
    }

    class CropRequest(val srcUri:Uri,val target: File){
        var aspectX = 1
        var aspectY = 1
        var outputX = 512
        var outputY = 512
    }
}