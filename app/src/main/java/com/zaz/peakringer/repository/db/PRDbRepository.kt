package com.zaz.peakringer.repository.db

import com.zaz.peakringer.PRApp
import com.zaz.peakringer.fragment.contacts.ContactsBean
import kotlinx.coroutines.flow.Flow

object PRDbRepository {
    private val db = PRDatabase.getDatabase(PRApp.application)

    fun addContact(contact:ContactsBean){
        db.runInTransaction{
            contact.addTime = System.currentTimeMillis() / 1000
            db.contactsDao().addContact(contact)
        }
    }

    fun addContacts(contacts: List<ContactsBean>){
        db.runInTransaction{
            contacts.forEach { it.addTime = System.currentTimeMillis() / 1000 }
            db.contactsDao().addContacts(contacts)
        }
    }

    fun getContacts(): Flow<List<ContactsBean>> {
        return db.contactsDao().getAllContacts()
    }

    fun findContact(phoneNum:String):ContactsBean?{
        return db.contactsDao().getContact(phoneNum)
    }

    fun delContact(contact: ContactsBean):Int{
       return db.contactsDao().delete(contact)
    }
}