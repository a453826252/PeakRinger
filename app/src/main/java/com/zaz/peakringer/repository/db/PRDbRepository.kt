package com.zaz.peakringer.repository.db

import com.zaz.peakringer.PRApp
import com.zaz.peakringer.fragment.contacts.ContactsBean
import kotlinx.coroutines.flow.Flow

object PRDbRepository {
    private val db = PRDatabase.getDatabase(PRApp.application)

    fun addContact(contact:ContactsBean){
        db.runInTransaction{
            db.contactsDao().addContact(contact)
        }
    }

    fun getContacts(): Flow<List<ContactsBean>> {
        return db.contactsDao().getAllContacts()
    }

    fun findContact(phoneNum:String):ContactsBean?{
        return db.contactsDao().getContact(phoneNum)
    }
}