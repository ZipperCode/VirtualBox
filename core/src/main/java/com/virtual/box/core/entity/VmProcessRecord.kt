package com.virtual.box.core.entity

import android.content.pm.ApplicationInfo
import android.os.*
import androidx.versionedparcelable.ParcelField
import com.virtual.box.core.proxy.ProxyManifest


class VmProcessRecord : Binder, Parcelable {

    /**
     * 虚拟应用程序信息
     */
    @JvmField
    var info: ApplicationInfo?

    /**
     * 宿主进程名称
     */
    var systemProcessName: String? = null
    /**
     * 宿主进程的进程id
     */
    @JvmField
    var systemPid = 0

    /**
     * 宿主进程的用户id
     */
    @JvmField
    var systemUid = 0

    /**
     * 虚拟进程名称（应用自身的进程名称）
     */
    @JvmField
    var processName: String?

    /**
     * 自定义的虚拟应用的用户id
     */
    @JvmField
    var vmUid: Int

    /**
     * 自定义的虚拟应用的进程id，这边的id与ProxyActivity或ProxyContentProvider的内部类的序号一致
     */
    @JvmField
    var vmPid: Int

    /**
     * 线程锁
     */
    @JvmField
    var initLock = ConditionVariable()

    /**
     * 虚拟进程的 IApplicationThread 代理对象
     */
    @JvmField
    var appThread: IInterface? = null


    constructor(info: ApplicationInfo, vmUid: Int, vmPid: Int): super() {
        this.info = info
        this.processName = info.processName
        this.vmUid = vmUid
        this.vmPid = vmPid
    }

    /**
     * 获取应用包名
     */
    val packageName: String? get() = info?.packageName

    constructor(parcel: Parcel) : super() {
        info = parcel.readParcelable(ApplicationInfo::class.java.classLoader)
        systemProcessName = parcel.readString()
        systemPid = parcel.readInt()
        systemUid = parcel.readInt()
        processName = parcel.readString()
        vmUid = parcel.readInt()
        vmPid = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(info, flags)
        parcel.writeString(systemProcessName)
        parcel.writeInt(systemPid)
        parcel.writeInt(systemUid)
        parcel.writeString(processName)
        parcel.writeInt(vmUid)
        parcel.writeInt(vmPid)
    }

    fun getProxyAuthority(): String{
        return ProxyManifest.getProxyAuthorities(vmPid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmProcessRecord> {
        override fun createFromParcel(parcel: Parcel): VmProcessRecord {
            return VmProcessRecord(parcel)
        }

        override fun newArray(size: Int): Array<VmProcessRecord?> {
            return arrayOfNulls(size)
        }

        const val SERVER_2_CLIENT_PROCESS_RECORD_KEY = "SERVER_2_CLIENT_PROCESS_RECORD_KEY"
    }


}