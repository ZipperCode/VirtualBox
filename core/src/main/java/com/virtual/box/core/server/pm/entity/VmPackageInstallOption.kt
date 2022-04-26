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

    @JvmField
    @Options
    var flags: Int = 0

    var packageName: String = ""

    var filePath: String = ""

    constructor(parcel: Parcel) : this() {
        flags = parcel.readInt()
        packageName = parcel.readString() ?: ""
        filePath = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(flags)
        parcel.writeString(packageName)
        parcel.writeString(filePath)
    }

    fun checkFlag(): Boolean{
        return isFlag(FLAG_SYSTEM) ||isFlag(FLAG_STORAGE)
    }

    fun isFlag(flag: Int): Boolean = this.flags.and(flag) == 0

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

        @JvmStatic
        fun installBySystem(packageName: String): VmPackageInstallOption{
            return VmPackageInstallOption().apply {
                flags = flags or FLAG_SYSTEM
                this.packageName = packageName
            }
        }

        @JvmStatic
        fun installByStorage(filePath: String): VmPackageInstallOption{
            return VmPackageInstallOption().apply {
                flags = flags or FLAG_STORAGE
                this.filePath = filePath
            }
        }

    }

    @IntDef(FLAG_SYSTEM, FLAG_STORAGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Options
}