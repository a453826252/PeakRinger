package com.zaz.peakringer.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaz.peakringer.fragment.contacts.ContactsBean


@Database(entities = arrayOf(ContactsBean::class),exportSchema=false, version = 2)
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
                    .addMigrations(MIGRATION_1_2) // 添加迁移策略
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `contacts_new` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `phone_number` TEXT NOT NULL,`name` TEXT NOT NULL, `icon` TEXT)")
                database.execSQL("INSERT INTO `contacts_new` (`id`,`phone_number`,`name`, `icon`) SELECT `id`,`phone_number`, `name`, `icon` FROM `contacts`")
                // 删除旧表
                database.execSQL("DROP TABLE contacts");
                // 重命名新表为旧表名
                database.execSQL("ALTER TABLE contacts_new RENAME TO contacts");
            }
        }
    }
}