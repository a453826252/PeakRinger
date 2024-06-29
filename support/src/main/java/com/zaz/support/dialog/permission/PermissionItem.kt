package com.zaz.support.dialog.permission

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes

data class PermissionItem(val permission:String,val title:String,@DrawableRes val icon:Int,val subTitle:String?=null):Parcelable {
    var granted = false //是否已有该权限
    constructor(parcel: Parcel) : this(
        parcel.readString() ?:"",
        parcel.readString()?: "",
        parcel.readInt(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(permission)
        dest.writeString(title)
        dest.writeInt(icon)
        dest.writeString(subTitle)
    }

    companion object CREATOR : Parcelable.Creator<PermissionItem> {
        override fun createFromParcel(parcel: Parcel): PermissionItem {
            return PermissionItem(parcel)
        }

        override fun newArray(size: Int): Array<PermissionItem?> {
            return arrayOfNulls(size)
        }
    }
}
