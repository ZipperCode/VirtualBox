package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmInstalledPackageInfo() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmInstalledPackageInfo> {
        override fun createFromParcel(parcel: Parcel): VmInstalledPackageInfo {
            return VmInstalledPackageInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmInstalledPackageInfo?> {
            return arrayOfNulls(size)
        }
    }
}