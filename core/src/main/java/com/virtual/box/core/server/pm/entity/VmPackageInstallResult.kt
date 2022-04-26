package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageInstallResult() : Parcelable {

    /**
     * 是否安装成功
     */
    @JvmField
    var success = true

    /**
     * 包名
     */
    @JvmField
    var packageName: String = ""

    /**
     * 消息
     */
    @JvmField
    var msg: String = ""

    constructor(parcel: Parcel) : this() {
        success = parcel.readByte() != 0.toByte()
        packageName = parcel.readString() ?: ""
        msg = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (success) 1 else 0)
        parcel.writeString(packageName)
        parcel.writeString(msg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageInstallResult> {
        override fun createFromParcel(parcel: Parcel): VmPackageInstallResult {
            return VmPackageInstallResult(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageInstallResult?> {
            return arrayOfNulls(size)
        }

        @JvmStatic
        fun installFail(msg: String):VmPackageInstallResult{
            return VmPackageInstallResult().apply {
                success = false
                this.msg = "安装失败，文件路径不存在：${msg}"
            }
        }
    }
}