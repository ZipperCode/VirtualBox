package com.virtual.box.core.server.user.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmUserInfoRepo() : HashMap<Int, VmUserInfo>(10), Parcelable {


    constructor(parcel: Parcel) : this() {
        parcel.readMap(this, javaClass.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeMap(this)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmUserInfoRepo> {
        override fun createFromParcel(parcel: Parcel): VmUserInfoRepo {
            return VmUserInfoRepo(parcel)
        }

        override fun newArray(size: Int): Array<VmUserInfoRepo?> {
            return arrayOfNulls(size)
        }
    }
}