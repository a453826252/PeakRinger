package com.zaz.peakringer.repository.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaz.peakringer.fragment.contacts.ContactsBean
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query("select * from contacts")
    fun getAllContacts(): Flow<List<ContactsBean>>

    @Query("select * from contacts where phone_number=:phoneNum")
    fun getContact(phoneNum:String):ContactsBean?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addContacts(contacts:List<ContactsBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addContact(contact:ContactsBean)

    @Delete
    fun delete(contact:ContactsBean)
}