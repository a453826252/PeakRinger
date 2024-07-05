package com.zaz.peakringer.fragment.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactsBean(
    @PrimaryKey
    @ColumnInfo(name = "phone_number")
    val phoneNumber:String,

    @ColumnInfo(name = "display_phone_number")
    val displayPhoneNumber:String,

    @ColumnInfo(name = "name")
    val name:String,

    @ColumnInfo(name = "icon")
    val icon:String?=null,
){
    @ColumnInfo(name = "add_time", defaultValue = "CURRENT_TIMESTAMP")
    var addTime:Long = 0
}