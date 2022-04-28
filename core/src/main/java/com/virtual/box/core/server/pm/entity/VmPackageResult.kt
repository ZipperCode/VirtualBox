package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageResult() : Parcelable {

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

    @JvmField
    var installUserId: Int = 0

    /**
     * 消息
     */
    @JvmField
    var msg: String = ""

    /**
     * 结果类型
     */
    @JvmField
    @ResultFlag
    var resultType: Int = 0

    constructor(parcel: Parcel) : this() {
        success = parcel.readByte() != 0.toByte()
        packageName = parcel.readString() ?: ""
        installUserId = parcel.readInt()
        msg = parcel.readString() ?: ""
        resultType = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (success) 1 else 0)
        parcel.writeString(packageName)
        parcel.writeInt(installUserId)
        parcel.writeString(msg)
        parcel.writeInt(resultType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageResult> {
        override fun createFromParcel(parcel: Parcel): VmPackageResult {
            return VmPackageResult(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageResult?> {
            return arrayOfNulls(size)
        }

        @JvmStatic
        fun installFail(msg: String):VmPackageResult{
            return VmPackageResult().apply {
                success = false
                this.msg = "安装失败，文件路径不存在：${msg}"
            }
        }

        @JvmStatic
        fun installSuccess(packageName: String):VmPackageResult{
            return VmPackageResult().apply {
                success = true
                this.packageName = packageName
            }
        }

        /**
         * 安装
         */
        const val INSTALL_FLAG = 1

        /**
         * 卸载
         */
        const val UNINSTALL_FLAG = 2

        /**
         * 更新
         */
        const val UPDATE_FLAG = 4
    }

    @IntDef(INSTALL_FLAG, UNINSTALL_FLAG, UPDATE_FLAG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ResultFlag
}