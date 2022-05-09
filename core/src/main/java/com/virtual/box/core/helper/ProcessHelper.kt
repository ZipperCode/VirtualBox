package com.virtual.box.core.helper

import android.app.ActivityManager
import android.content.Context
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.BuildConfig
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest
import java.lang.IllegalStateException

object ProcessHelper {

    private val logger = Logger.getLogger(L.VM_TAG)
    /**
     * 查找可用的虚拟进程号
     */
    @Synchronized
    fun findAvailableVmPid(): Int {
        val activityManager = VirtualBox.get().hostContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessList = activityManager.runningAppProcesses
        val usedVmPidSet = mutableSetOf<Int>()
        for (runningAppProcessInfo in runningAppProcessList) {
            val pid = parseVmPid(runningAppProcessInfo.processName)
            if (pid >= 0) {
                usedVmPidSet.add(pid)
            }
        }
        var canUseVmPid = -1
        for (i in 0 until ProxyManifest.FREE_COUNT) {
            if (usedVmPidSet.contains(i)) {
                continue
            }
            canUseVmPid = i
            break
        }
        if (canUseVmPid == -1) {
            throw IllegalStateException("未找到可使用的 VmPid")
        }
        return canUseVmPid
    }

    /**
     * 解析进程id
     */
    private fun parseVmPid(proxyProcessName: String): Int {
        if (proxyProcessName.isEmpty()) {
            return -1
        }
        if (!proxyProcessName.contains(BuildConfig.HOST_PACKAGE)) {
            return -1
        }
        val index = proxyProcessName.indexOf(":p")
        if (index < 0) {
            return -1
        }
        val vmPid = proxyProcessName.substring(index).toInt()
        logger.i("解析进程 %s 中的vmPid = %s", proxyProcessName, vmPid)
        return vmPid
    }
}