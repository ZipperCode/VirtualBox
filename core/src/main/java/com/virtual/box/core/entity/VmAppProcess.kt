package com.virtual.box.core.entity

import android.os.Binder
import android.os.Process
import com.virtual.box.base.util.log.L

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmAppProcess(val appId: Int, val packageName: String) {
    /**
     * 应用运行的进程
     * 进程Id：进程信息
     */
    private val currentAppProcessRecord: MutableMap<Int, VmProcessRecord> = HashMap(5)

    /**
     * 虚拟进程对应的系统进程
     */
    private val vmPid2SystemPId: MutableMap<Int, Int> = HashMap(5)

    private var mainVmPid: Int = -1

    fun startMainProcess(vmPid: Int){
        val callingUid = Binder.getCallingUid()
        val callingPid = Binder.getCallingPid()
    }

    fun startProxyProcess(){

    }

    fun kill(vmPid: Int){
        // TODO 查找对应的系统
        vmPid2SystemPId[vmPid]?.run {
            killProcess(this)
        }
    }

    @Synchronized
    fun killAppProcess(){
        try {
            for (pidEntry in vmPid2SystemPId) {
                killProcess(pidEntry.value)
            }
        }finally {
            vmPid2SystemPId.clear()
        }
    }
    private fun killProcess(pid: Int){
        try {
            Process.killProcess(pid)
        }catch (e: Throwable){
            L.printStackTrace(e)
        }
    }
}