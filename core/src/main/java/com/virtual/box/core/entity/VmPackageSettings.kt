package com.virtual.box.core.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageSettings() : Parcelable {

    var packageName: String = ""

    constructor(parcel: Parcel) : this() {
        packageName = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageSettings> {
        override fun createFromParcel(parcel: Parcel): VmPackageSettings {
            return VmPackageSettings(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageSettings?> {
            return arrayOfNulls(size)
        }
    }
}