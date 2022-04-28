package com.virtual.box.core.server.pm.entity

import android.content.pm.PackageInfo
import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmInstalledPackageInfo(
    val userId: Int = 0,
    val packageName: String? = null,
    val packageInfo: PackageInfo? = null
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(PackageInfo::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(packageName)
        parcel.writeParcelable(packageInfo, flags)
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