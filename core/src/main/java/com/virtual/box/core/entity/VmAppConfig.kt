package com.virtual.box.core.entity

import android.content.pm.PackageInfo
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

    @JvmField
    var userId: Int = 0

    var isMainProcess: Boolean = false

    /**
     * 主进程的vmPid
     */
    var mainProcessVmPid: Int = -1

    /**
     * 主进程的系统pid
     */
    var mainProcessSystemPid: Int = -1

    /**
     * 主进程的系统uid
     */
    var mainProcessSystemUid: Int = -1
    /**
     * 进程记录
     */
    var vmProcessRecord: VmProcessRecord? = null

    constructor(parcel: Parcel) : this() {
        packageName = parcel.readString() ?: ""
        processName = parcel.readString() ?: ""
        userId = parcel.readInt()
        isMainProcess = parcel.readByte() != 0.toByte()
        mainProcessVmPid = parcel.readInt()
        mainProcessSystemPid = parcel.readInt()
        mainProcessSystemUid = parcel.readInt()
        vmProcessRecord = parcel.readParcelable(VmProcessRecord::class.java.classLoader) as? VmProcessRecord
    }

    fun getVmPidByProcess(processName: String): Int{
        if (processName == packageName){
            return mainProcessVmPid
        }
        return vmProcessRecord?.vmPid ?: mainProcessVmPid
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeString(processName)
        parcel.writeInt(userId)
        parcel.writeByte(if (isMainProcess) 1 else 0)
        parcel.writeInt(mainProcessVmPid)
        parcel.writeInt(mainProcessSystemPid)
        parcel.writeInt(mainProcessSystemUid)
        parcel.writeParcelable(vmProcessRecord, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "VmAppConfig(packageName='$packageName', processName='$processName', userId=$userId, isMainProcess=$isMainProcess, mainProcessVmPid=$mainProcessVmPid, mainProcessSystemPid=$mainProcessSystemPid, mainProcessSystemUid=$mainProcessSystemUid, vmProcessRecord=$vmProcessRecord)"
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