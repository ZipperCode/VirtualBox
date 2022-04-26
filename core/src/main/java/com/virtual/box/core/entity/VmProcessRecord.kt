package com.virtual.box.core.entity

import android.content.pm.ApplicationInfo
import android.os.Binder
import android.os.ConditionVariable
import android.os.Parcelable

class VmProcessRecord: Binder, Parcelable {

    /**
     * 虚拟应用程序信息
     */
    @JvmField
    val info: ApplicationInfo

    /**
     * 进程名称
     */
    @JvmField
    val processName: String

    /**
     * 当前进程的进程id
     */
    @JvmField
    var systemPid = 0

    /**
     * 当前进程的用户id
     */
    @JvmField
    var systemUid = 0

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

    constructor(info: ApplicationInfo, processName: String, buid: Int, bpid: Int) {
        this.info = info
        this.vmUid = buid
        this.vmPid = bpid
        this.processName = processName
    }
}