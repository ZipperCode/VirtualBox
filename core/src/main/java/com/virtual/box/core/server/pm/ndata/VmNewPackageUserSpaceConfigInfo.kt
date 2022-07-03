package com.virtual.box.core.server.pm.ndata

import android.os.Parcel
import android.os.Parcelable

class VmNewPackageUserSpaceConfigInfo(
    val packageName: String?,
    /**
     * 安装位置的根目录
     */
    val installVmPackageDirPath: String?,

    /**
     * 安装后的应用包信息文件路径
     */
    val installVmPackageInfoFilePath: String?,

    /**
     * 用户空间根目录路径
     */
    val userPackageSpaceRootDirPath: String?,

    /**
     * 安装后是否被打开
     */
    var isOpened: Boolean
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeString(installVmPackageDirPath)
        parcel.writeString(installVmPackageInfoFilePath)
        parcel.writeString(userPackageSpaceRootDirPath)
        parcel.writeByte(if (isOpened) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmNewPackageUserSpaceConfigInfo> {
        override fun createFromParcel(parcel: Parcel): VmNewPackageUserSpaceConfigInfo {
            return VmNewPackageUserSpaceConfigInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmNewPackageUserSpaceConfigInfo?> {
            return arrayOfNulls(size)
        }
    }


}