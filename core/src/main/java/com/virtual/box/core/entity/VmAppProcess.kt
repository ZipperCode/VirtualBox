package com.virtual.box.core.entity

import android.os.Binder
import android.os.ConditionVariable
import android.os.Process
import com.virtual.box.base.util.log.L

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmAppProcess(val appId: Int, val userId: Int, val packageName: String, val processName: String) {
    /**
     * 应用运行的进程
     * 代理应用进程名称：进程信息
     */
    val currentAppProcessRecord: MutableMap<String, VmProcessRecord> = HashMap(5)

    /**
     * 虚拟进程对应的系统进程
     */
    private val vmPid2SystemPId: MutableMap<Int, Int> = HashMap(5)

    /**
     * 应用主进程的进程信息
     */
    var mainProcessRecord: VmProcessRecord? = null

    val appProcessHandleLock = Any()

    /**
     * 进程是否启动
     */
    val processHasStarted: Boolean get() {
        return mainProcessRecord != null && vmPid2SystemPId.isNotEmpty()
    }

    val vmPackageName: String get() = mainProcessRecord?.processName ?: ""

    val vmPid:Int get() = mainProcessRecord?.vmPid ?: -1
    /**
     * 主进程创建lock
     */
    val mainProcessInitLock = ConditionVariable()

    var hasKilled: Boolean = false
        private set


    fun startProxyProcess(){

    }

    fun addSubProcessInfo(vmProcessRecord: VmProcessRecord){

    }
    /**
     * 检查应用进程是否存在
     */
    fun checkProcessExists(vmProcessName: String): Boolean{
        return currentAppProcessRecord.containsKey(vmProcessName)
    }

    fun checkMainProcess(): Boolean{
        return mainProcessRecord != null
    }

    fun checkAndWaitMainProcess(){
//        if (checkMainProcess()){
//            if (mainProcessRecord!!.appThread != null)
//        }
    }

    fun checkAndSetMainProcess(vmProcessRecord: VmProcessRecord){
        if (mainProcessRecord == null){
            mainProcessRecord = vmProcessRecord
        }
    }

    fun checkAndSetProcess(vmProcessRecord: VmProcessRecord){
        if (mainProcessRecord == null){
            mainProcessRecord = vmProcessRecord
        }else{
            currentAppProcessRecord[vmProcessRecord.processName!!] = vmProcessRecord
        }
        vmPid2SystemPId[vmProcessRecord.vmPid] = vmProcessRecord.systemPid
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
            mainProcessRecord = null
            for (pidEntry in vmPid2SystemPId) {
                killProcess(pidEntry.value)
            }
        }finally {
            hasKilled = true
            vmPid2SystemPId.clear()
        }
    }

    fun killProcessByName(processName: String){
        try {

        }catch (e: Throwable){
            L.printStackTrace(e)
        }
    }

    private fun killProcess(pid: Int){
        try {
            Process.killProcess(pid)
        }catch (e: Throwable){
            L.printStackTrace(e)
        }
    }

    fun getVmAppProcessAppConfig(): VmAppConfig{
        return getVmProcessAppConfig(mainProcessRecord!!)
    }

    fun getVmProcessAppConfig(processName: String): VmAppConfig?{
        if (packageName == processName){
            return getVmProcessAppConfig(mainProcessRecord!!)
        }
        val processRecord = currentAppProcessRecord[processName] ?: return null
        return getVmProcessAppConfig(processRecord)
    }

    fun getVmProcessAppConfig(vmProcessRecord: VmProcessRecord): VmAppConfig{
        return VmAppConfig().apply {
            this.processName = vmProcessRecord.processName ?: ""
            this.packageName = vmProcessRecord.packageName ?: ""
            this.userId = this@VmAppProcess.userId ?: -1
            this.vmProcessRecord = vmProcessRecord
            this.isMainProcess = mainProcessRecord?.processName != vmProcessRecord.processName
            this.mainProcessVmPid = mainProcessRecord?.vmPid ?: -1
            this.mainProcessSystemPid = mainProcessRecord?.systemPid ?: -1
            this.mainProcessSystemUid = mainProcessRecord?.systemUid ?: -1
        }
    }
}