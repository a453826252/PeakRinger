package com.zaz.peakringer.fragment.contacts

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ContactsFragmentVM: ViewModel() {
    private val TAG = "ContactsFragmentVM"
    private val _contacts = MutableLiveData<List<ContactsBean>>()
    val contacts:LiveData<List<ContactsBean>> = MutableLiveData<List<ContactsBean>>()

    fun addContacts(context: Context,uri:Uri){
        viewModelScope.launch {
            context.contentResolver.query(uri,null,null,null,null)?.use { cursor->
                if (cursor.moveToNext()){
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    //获取联系人姓名
                    val displayName = cursor.getString(nameIndex)
                    var phoneNumber = ""
                    //获取id
                    val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    val id = cursor.getString(idIndex)
                    //判断是否有手机号
                    val hasPhoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
                    val hasPhone = cursor.getString(hasPhoneIndex) //等于1就是有手机号
                    if(hasPhone == "1"){
                        //重新查询手机号
                        context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,"${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id",null,null)?.use { phonesCursor->
                            while (phonesCursor.moveToNext()){
                                val phoneIndex = phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                phoneNumber = phonesCursor.getString(phoneIndex)
                            }
                        }?:Log.e(TAG, "get contacts phoneNum from uri($uri) failed")
                    }
                    Log.e(TAG, "姓名:$displayName  手机号:$phoneNumber")
                }
            }?:Log.e(TAG, "add contacts from uri($uri) failed")
        }
    }

}