package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageInstallOption() : Parcelable {
    /**
     * 安装来源
     */
    @JvmField
    @Options
    var originFlags: Int = 0

    /**
     * 安装包名，flag = [FLAG_SYSTEM] 时使用
     */
    var packageName: String = ""

    /**
     * 安装包文件路径，flag = [FLAG_STORAGE] 时使用
     */
    var filePath: String = ""

    @JvmField
    var installFlag: Int = 0

    constructor(parcel: Parcel) : this() {
        originFlags = parcel.readInt()
        packageName = parcel.readString() ?: ""
        filePath = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(flags)
        parcel.writeString(packageName)
        parcel.writeString(filePath)
    }

    val isFromSystemFlag: Boolean get() = isOriginFlag(FLAG_SYSTEM)

    val isFromStorageFlag: Boolean get() = isOriginFlag(FLAG_STORAGE)

    fun checkOriginFlag(): Boolean{
        return isOriginFlag(FLAG_SYSTEM) ||isOriginFlag(FLAG_STORAGE)
    }

    fun isOriginFlag(flag: Int): Boolean = this.originFlags.and(flag) == 0

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageInstallOption> {
        override fun createFromParcel(parcel: Parcel): VmPackageInstallOption {
            return VmPackageInstallOption(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageInstallOption?> {
            return arrayOfNulls(size)
        }

        /**
         * 标志来源于系统文件
         * 001
         */
        const val FLAG_SYSTEM = 1

        /**
         * 标志来源于存储文件
         * 010
         */
        const val FLAG_STORAGE = 2

        /**
         * 100
         */
        const val FLAG_URI_FILE = 4

        /**
         * 正常安装
         */
        const val INSTALL_FLAG_NORMAL = 1

        /**
         * 覆盖安装
         */
        const val INSTALL_FLAG_COVER = 2

        @JvmStatic
        fun installBySystem(packageName: String): VmPackageInstallOption{
            return VmPackageInstallOption().apply {
                originFlags = originFlags or FLAG_SYSTEM
                this.packageName = packageName
            }
        }

        @JvmStatic
        fun installByStorage(filePath: String): VmPackageInstallOption{
            return VmPackageInstallOption().apply {
                originFlags = originFlags or FLAG_STORAGE
                this.filePath = filePath
            }
        }

    }

    @IntDef(FLAG_SYSTEM, FLAG_STORAGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Options

    @IntDef(INSTALL_FLAG_NORMAL, INSTALL_FLAG_COVER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class InstallOptions
}