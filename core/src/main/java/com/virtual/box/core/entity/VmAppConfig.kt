package com.virtual.box.core.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmAppConfig() : Parcelable {

    /**
     * 安装包
     */
    @JvmField
    var packageName: String = ""

    /**
     * 进程名称
     */
    @JvmField
    var processName: String = ""

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmAppConfig> {
        override fun createFromParcel(parcel: Parcel): VmAppConfig {
            return VmAppConfig(parcel)
        }

        override fun newArray(size: Int): Array<VmAppConfig?> {
            return arrayOfNulls(size)
        }

        const val IPC_BUNDLE_KEY = "_VM_|app_config"
    }
}