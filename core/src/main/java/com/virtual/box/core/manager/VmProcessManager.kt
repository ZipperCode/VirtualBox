package com.virtual.box.core.manager

import android.content.pm.ApplicationInfo
import android.os.ConditionVariable
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.entity.VmAppProcess
import com.virtual.box.core.entity.VmProcessRecord

object VmProcessManager {

    private val logger = Logger.getLogger(L.SERVER_TAG,"VmProcessManager")
    /**
     * 用户id: [进程名称：进程信息]
     */
    private val processMap: MutableMap<Int, VmAppProcess> = HashMap(10)

    /**
     * 准备启动一个新的进程
     */
    fun prepareStartVmProcess(vmApplicationInfo: ApplicationInfo, processName: String, userId: Int): VmAppProcess?{
        val existsVmAppProcess = processMap[userId]
        val packageName = vmApplicationInfo.packageName

        return null
    }

    fun startVmProcess(vmAppProcess: VmAppProcess){


    }


    fun initVmProcess(){

    }

    fun killProcess(vmUid: Int){
        processMap[vmUid]?.run {
            this.killAppProcess()
        }
    }
}