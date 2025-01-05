package com.zaz.peakringer.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.repository.db.PRDbRepository
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.util.Locale

object PRPhoneNumberUtil {
    private lateinit var phoneNumberUtils: PhoneNumberUtil
    private lateinit var networkCountryIso: String
    private const val TAG = "NumberMatchUtil"
    fun init(context: Context){
        phoneNumberUtils = PhoneNumberUtil.createInstance(context)
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        networkCountryIso = telephonyManager.networkCountryIso.uppercase(Locale.ROOT)
    }
    fun match(number:String):Boolean{
        return getContact(number) != null
    }

    fun getContactId(number: String):Int{
        return getContact(number)?.id ?: 0
    }

    fun getContact(number:String):ContactsBean?{
        if(TextUtils.isEmpty(number)){
            Log.e(TAG, "match: number is empty")
            return null
        }
        val allContacts = PRDbRepository.getContacts()
        if(allContacts.isEmpty()){
            Log.e(TAG, "match: local contacts is empty")
            return null
        }
        Log.d(TAG, "match: number=$number")
        val n1: Phonenumber.PhoneNumber
        try {
            n1 = phoneNumberUtils.parseAndKeepRawInput(number,networkCountryIso)
        } catch (e: NumberParseException) {
            return null
        }
        for(dbContact in allContacts){
            if(number == dbContact.phoneNumber){
                return dbContact
            }
            val dbNumber = dbContact.phoneNumber.formatToPhoneNumber
            Log.d(TAG, "match: number=$number,phoneNumber=$dbNumber")
            val n2: Phonenumber.PhoneNumber
            try {
                n2 = phoneNumberUtils.parseAndKeepRawInput(dbNumber, networkCountryIso)
            } catch (e: NumberParseException) {
                continue
            }
            val matchType: PhoneNumberUtil.MatchType = phoneNumberUtils.isNumberMatch(n1, n2)
            if (matchType === PhoneNumberUtil.MatchType.EXACT_MATCH || matchType === PhoneNumberUtil.MatchType.NSN_MATCH) {
                return dbContact
            } else if (matchType === PhoneNumberUtil.MatchType.SHORT_NSN_MATCH && n1.nationalNumber == n2.nationalNumber && n1.countryCode == n2.countryCode) {
                return dbContact
            }
        }
        Log.d(TAG, "match: no match!")
        return null
    }

    fun format(number: String):String{
        try {
            val n1 = phoneNumberUtils.parseAndKeepRawInput(number, networkCountryIso)
            return phoneNumberUtils.format(n1, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (e: NumberParseException) {
            return number
        }
    }
}