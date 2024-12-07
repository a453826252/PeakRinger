package com.zaz.peakringer.fragment.contacts.edit

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaz.peakringer.config.Config
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.repository.db.PRDbRepository
import com.zaz.support.base.BaseViewModel
import com.zaz.support.utils.toUri
import kotlinx.coroutines.launch
import java.io.File

class EditOrAddContactVM : BaseViewModel() {
    private val _avatarUpdate = MutableLiveData<Uri>()
    val avatarUpdate:LiveData<Uri> = _avatarUpdate
    fun addContact(context: Context, contactsBean: ContactsBean,cropAvatar: Uri?): Boolean {
        var bean = contactsBean
        cropAvatar?.let {
            context.contentResolver.openInputStream(it)?.use {
                val avatarPathDesired = Config.getAvatarDefaultPath(
                    context,
                    contactsBean.name,
                    contactsBean.phoneNumber,
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(cropAvatar))?:""
                )
                val avatarFile = File(avatarPathDesired)
                if(avatarFile.exists()){
                    avatarFile.delete()
                }
                avatarFile.parentFile?.mkdirs()
                avatarFile.outputStream().use { target->
                    it.copyTo(target)
                }
                bean = ContactsBean(contactsBean.phoneNumber,contactsBean.phoneNumber,contactsBean.name,avatarPathDesired, id = bean.id)
            }
            context.contentResolver.delete(it,null,null)
        }

        return PRDbRepository.addContact(bean) > 0
    }
    fun addAvatar(context: Context,uri: Uri){
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri).use {
                    Log.d(TAG, "addAvatar: inputSteam is null? ${it==null}")
                    it?.let { input->
                        val suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
                        Log.d(TAG, "addAvatar: suffix=$suffix")
                        val cachePath = Config.getAvatarCachePath(context,"${System.currentTimeMillis()}.$suffix")
                        val cacheAvatarFile = File(cachePath)
                        cacheAvatarFile.parentFile?.mkdirs()
                        cacheAvatarFile.outputStream().use { target->
                            input.copyTo(target)
                        }
//                        _avatarUpdate.postValue(cachePath)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "addAvatar: ex=${e.message}",e )
            }
        }
    }

    fun getWaitCropUri(context: Context):Uri?{
        val file = File(Config.getAvatarWaitCropPath(context,"${System.currentTimeMillis()}.png"))
        return getFileUri(context,file)
    }

    fun getTmpAvatarUri():File{
        return  File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).absolutePath + File.separator + "${System.currentTimeMillis()}.png")
    }
    private fun getFileUri(context: Context,file:File):Uri?{
        val mkdirResult = file.parentFile?.mkdirs() ?: false
        Log.d(TAG, "getWaitCropUri: mkdirResult=$mkdirResult")
        return file.toUri(context)
    }
    companion object{
        const val TAG = "EditOrAddContactVM"
    }
}