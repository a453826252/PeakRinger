package com.zaz.peakringer.fragment.contacts

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zaz.support.dialog.permission.PermissionItem

@Entity(tableName = "contacts")
data class ContactsBean(
    @ColumnInfo(name = "phone_number")
    val phoneNumber:String,

    @ColumnInfo(name = "display_phone_number")
    val displayPhoneNumber:String,

    @ColumnInfo(name = "name")
    val name:String,

    @ColumnInfo(name = "icon")
    val icon:String?=null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int = 0,
):Parcelable{
    @ColumnInfo(name = "add_time", defaultValue = "CURRENT_TIMESTAMP")
    var addTime:Long = 0
    constructor(parcel: Parcel) : this(
        parcel.readString() ?:"",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString(),
        parcel.readInt()
    ){
        addTime = parcel.readLong()
    }
    override fun describeContents(): Int {
       return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(phoneNumber)
        dest.writeString(displayPhoneNumber)
        dest.writeString(name)
        dest.writeString(icon)
        dest.writeInt(id)
        dest.writeLong(addTime)
    }

    companion object CREATOR : Parcelable.Creator<ContactsBean> {
        override fun createFromParcel(parcel: Parcel): ContactsBean {
            return ContactsBean(parcel)
        }

        override fun newArray(size: Int): Array<ContactsBean?> {
            return arrayOfNulls(size)
        }
    }
}