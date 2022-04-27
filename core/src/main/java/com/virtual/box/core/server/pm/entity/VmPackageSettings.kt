package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmPackageSettings() : HashMap<String, VmPackageSetting>(10), Parcelable {
    constructor(parcel: Parcel) : this() {
        parcel.readMap(this, javaClass.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeMap(this)
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