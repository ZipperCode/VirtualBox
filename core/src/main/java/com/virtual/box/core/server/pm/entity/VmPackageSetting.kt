package com.virtual.box.core.server.pm.entity

import android.content.pm.PackageInfo
import android.os.Parcel
import android.os.Parcelable

/**
 * 安装包信息类
 *
 * 存储安装的应用包信息
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageSetting: Parcelable {
    /**
     * 包名
     */
    val packageName: String

    var vmPackageInfo: PackageInfo?

    var installOption: VmPackageInstallOption? = null

    constructor(vmPackageInfo: PackageInfo, vmPackageInstallOption: VmPackageInstallOption){
        this.packageName = vmPackageInfo.packageName
        this.vmPackageInfo = vmPackageInfo
        this.installOption = vmPackageInstallOption
    }

    constructor(parcel: Parcel){
        packageName = parcel.readString() ?: ""
        vmPackageInfo = parcel.readParcelable(PackageInfo::class.java.classLoader)
        installOption = parcel.readParcelable(VmPackageInstallOption::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeParcelable(vmPackageInfo, flags)
        parcel.writeParcelable(installOption, flags)
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