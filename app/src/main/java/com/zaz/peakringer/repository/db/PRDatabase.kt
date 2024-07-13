package com.zaz.peakringer.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zaz.peakringer.fragment.contacts.ContactsBean

@Database(entities = arrayOf(ContactsBean::class),exportSchema=false, version = 1)
abstract class PRDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PRDatabase? = null

        fun getDatabase(
            context: Context,
        ): PRDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PRDatabase::class.java,
                    "peak_ringer_database"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}