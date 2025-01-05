package com.zaz.peakringer.fragment.contacts.display

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zaz.peakringer.R
import com.zaz.peakringer.config.Config
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.repository.db.PRDbRepository
import com.zaz.peakringer.utils.PRPhoneNumberUtil
import com.zaz.peakringer.utils.formatToPhoneNumber
import com.zaz.support.base.BaseViewModel
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.string
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class ContactsFragmentVM: BaseViewModel() {
    private val TAG = "ContactsFragmentVM"
    private val _contacts = MutableLiveData<List<ContactsBean>?>()
    val contacts:LiveData<List<ContactsBean>?> = _contacts

    fun addContacts(context: Context,uri:Uri){
        viewModelScope.launch {
            try {
                context.contentResolver.query(uri,null,null,null,null)?.use { cursor->
                    if (cursor.moveToNext()){
                        val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        //获取联系人姓名
                        var displayName = cursor.getString(nameIndex)
                        if(displayName.isEmpty()){
                            displayName = "No Name"
                        }
                        //获取id
                        val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                        val id = cursor.getLong(idIndex)
                        //判断是否有手机号
                        val hasPhoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
                        val hasPhone = cursor.getString(hasPhoneIndex) //等于1就是有手机号
                        val phoneNumbers = mutableListOf<String>()
                        if(hasPhone == "1"){
                            //重新查询手机号
                            context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,"${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id",null,null)?.use { phonesCursor->
                                while (phonesCursor.moveToNext()){
                                    val phoneIndex = phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val phoneNumber = phonesCursor.getString(phoneIndex)
                                    if(phoneNumber.isBlank() || phoneNumber.formatToPhoneNumber.isBlank()){
                                        continue
                                    }
                                    if(phoneNumber.isNotBlank()){
                                        phoneNumbers.add(phoneNumber)
                                    }
                                }
                            }?:Log.e(TAG, "get contacts phoneNum from uri($uri) failed")
                        }
                        if(phoneNumbers.isEmpty()){
                            Log.e(TAG, "addContacts: failed to get phone number")
                            PRToast.show(context,context.getString(R.string.get_contact_phone_number_failed))
                            return@launch
                        }
                        var existIconPath:String? = ""
                        val iterator = phoneNumbers.iterator()
                        while (iterator.hasNext()){
                            val phoneNumber = iterator.next()
                            val dbContact = PRPhoneNumberUtil.getContact(phoneNumber)
                            if(dbContact != null){
                                existIconPath = dbContact.icon
                                Log.d(TAG, "addContacts: phoneNumber already exists in db")
                                iterator.remove()
                                continue
                            }
                        }

                        if(phoneNumbers.isEmpty()){
                            PRToast.show(context,context.getString(R.string.contact_phone_exist_yet))
                            return@launch
                        }
                        if(existIconPath.isNullOrEmpty()){
                            val avatarUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)
                            val input = ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, avatarUri)
                            var avatarPath = Config.getAvatarDefaultPath(context,displayName,phoneNumbers[0],MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(avatarUri))?:"")
                            val avatarFile = File(avatarPath)
                            if(avatarFile.exists()){
                                avatarFile.delete()
                            }
                            input?.use {
                                val mkdir = avatarFile.parentFile?.mkdirs()
                                Log.d(TAG, "addContacts: mkdir result=$mkdir,path=${avatarFile.parentFile?.absolutePath}")
                                avatarFile.outputStream().use { target->
                                    input.copyTo(target)
                                }
                            }
                            if(avatarFile.exists() && avatarFile.length() > 0){
                                existIconPath = avatarFile.absolutePath
                            }
                        }
                        val contacts = mutableListOf<ContactsBean>()
                        for (phoneNumber in phoneNumbers){
                            contacts.add(ContactsBean(phoneNumber, displayName,existIconPath))
                        }
                        PRDbRepository.addContacts(contacts)
                        if(!isActive) return@launch
                        PRDbRepository.getContactsFlow().collect{
                            _contacts.postValue(it)
                        }
                    }
                }?:Log.e(TAG, "add contacts from uri($uri) failed")
            }catch (_:CancellationException){

            } catch (e: Exception) {
                Log.e(TAG, "addContacts: ex=${e.message}",e)
                PRToast.show(context, R.string.pick_contacts_failed.string(context))
            }
        }
    }

    fun refreshContacts(){
        viewModelScope.launch {
            PRDbRepository.getContactsFlow().collect{
                _contacts.postValue(it)
            }
        }
    }

    fun delContact(contactsBean: ContactsBean):Boolean{
        return PRDbRepository.delContact(contactsBean) > 0
    }

}