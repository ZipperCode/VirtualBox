package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageSetting: Parcelable {

    val packageName: String

    val appId: Int

    var vmPackageInfo: VmPackageInfo?

    var installOption: VmPackageInstallOption? = null

    constructor(packageName: String, appId: Int, vmPackageInfo: VmPackageInfo){
        this.packageName = packageName
        this.appId = appId
        this.vmPackageInfo = vmPackageInfo
    }

    constructor(parcel: Parcel){
        packageName = parcel.readString() ?: ""
        appId = parcel.readInt()
        vmPackageInfo = parcel.readParcelable(VmPackageInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeInt(appId)
        parcel.writeParcelable(vmPackageInfo, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageSetting> {
        override fun createFromParcel(parcel: Parcel): VmPackageSetting {
            return VmPackageSetting(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageSetting?> {
            return arrayOfNulls(size)
        }
    }
}