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
class VmPackageUserSpace : Parcelable {
    /**
     * 用户id
     */
    var userId: Int

    /**
     * 安装包名
     */
    var packageName: String

    /**
     * 安装位置的根目录
     */
    var installVmPackageDirPath: String?

    /**
     * 安装后的应用包信息文件路径
     */
    var installVmPackageInfoFilePath: String?

    /**
     * 用户空间根目录路径
     */
    var userPackageSpaceRootDirPath: String?

    /**
     * 安装后是否被打开
     */
    var isOpened: Boolean

    /**
     * 最后安装更新时间
     */
    var lastInstallUpdateTime: Long

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString() ?: "") {
        installVmPackageDirPath = parcel.readString()
        installVmPackageInfoFilePath = parcel.readString()
        userPackageSpaceRootDirPath = parcel.readString()
        isOpened = parcel.readByte() != 0.toByte()
        lastInstallUpdateTime = parcel.readLong()
    }

    constructor(userId: Int, packageName: String) {
        this.userId = userId
        this.packageName = packageName
        installVmPackageDirPath = ""
        installVmPackageInfoFilePath = ""
        userPackageSpaceRootDirPath = ""
        isOpened = false
        lastInstallUpdateTime = 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(packageName)
        parcel.writeString(installVmPackageDirPath)
        parcel.writeString(installVmPackageInfoFilePath)
        parcel.writeString(userPackageSpaceRootDirPath)
        parcel.writeByte(if (isOpened) 1 else 0)
        parcel.writeLong(lastInstallUpdateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageUserSpace> {
        override fun createFromParcel(parcel: Parcel): VmPackageUserSpace {
            return VmPackageUserSpace(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageUserSpace?> {
            return arrayOfNulls(size)
        }
    }
}