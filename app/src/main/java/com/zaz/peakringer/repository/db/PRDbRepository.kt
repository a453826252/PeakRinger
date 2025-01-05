package com.zaz.peakringer.repository.db

import com.zaz.peakringer.PRApp
import com.zaz.peakringer.fragment.contacts.ContactsBean
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.concurrent.Callable

object PRDbRepository {
    private val db = PRDatabase.getDatabase(PRApp.application)

    fun addContact(contact: ContactsBean):Long{
       return db.runInTransaction(Callable {
            db.contactsDao().addContact(contact)
        })
    }

    fun updateContact(contact: ContactsBean):Int{
        return db.contactsDao().updateContact(contact)
    }

    fun addContacts(contacts: List<ContactsBean>){
        db.runInTransaction{
            db.contactsDao().addContacts(contacts)
        }
    }

    fun getContactsFlow(): Flow<List<ContactsBean>> {
        return db.contactsDao().getAllContactsFlow()
    }
    fun getContacts(): List<ContactsBean> {
        return db.contactsDao().getAllContacts()
    }

    fun findContact(phoneNum:String): ContactsBean?{
        return db.contactsDao().getContact(phoneNum)
    }

    fun delContact(contact: ContactsBean):Int{
        return db.runInTransaction(Callable {
            val iconPath = contact.icon
            if(iconPath?.startsWith("/") == true){
                File(iconPath).delete()
            }
            db.contactsDao().delete(contact)
        })
    }
}