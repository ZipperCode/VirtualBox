package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * 用户的数据空间
 *
 * 用于
 *
 * @author zhangzhipeng
 * @date   2022/4/29
 **/
data class VmAppDataConfigInfo(
    /**
     * 唯一标识名
     */
    val appDataId: String,
    /**
     * 用户id
     */
    val userId: Int,

    /**
     * 安装包名
     */
    val packageName: String,
    /**
     * 安装位置的根目录
     */
    val installVmPackageDirPath: String,
    /**
     * 用户空间根目录路径
     */
    val userPackageSpaceRootDirPath: String,
    /**
     * 安装后的应用包信息文件路径
     */
    val installVmPackageInfoFilePath: String? = null,

) : Parcelable {

    /**
     * 安装后是否被打开
     */
    var isOpened: Boolean = false

    /**
     * 最后安装更新时间
     */
    var lastInstallUpdateTime: Long = 0

    constructor(parcel: Parcel) : this(
        appDataId = parcel.readString()!!,
        userId = parcel.readInt(),
        packageName = parcel.readString()!!,
        installVmPackageDirPath = parcel.readString()!!,
        userPackageSpaceRootDirPath = parcel.readString()!!,
        installVmPackageInfoFilePath = parcel.readString()
    ) {
        isOpened = parcel.readByte() != 0.toByte()
        lastInstallUpdateTime = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appDataId)
        parcel.writeInt(userId)
        parcel.writeString(packageName)
        parcel.writeString(installVmPackageDirPath)
        parcel.writeString(userPackageSpaceRootDirPath)
        parcel.writeString(installVmPackageInfoFilePath)
        parcel.writeByte(if (isOpened) 1 else 0)
        parcel.writeLong(lastInstallUpdateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmAppDataConfigInfo> {
        override fun createFromParcel(parcel: Parcel): VmAppDataConfigInfo {
            return VmAppDataConfigInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmAppDataConfigInfo?> {
            return arrayOfNulls(size)
        }
    }
}