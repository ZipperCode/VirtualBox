package com.virtual.box.core.manager

import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.entity.VmAppProcess
import com.virtual.box.core.entity.VmProcessRecord
import com.virtual.box.core.helper.ProcessHelper
import com.virtual.box.core.helper.ProviderCallHelper
import com.virtual.box.core.proxy.ProxyContentProvider
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.util.concurrent.CopyOnWriteArrayList

object VmProcessManager {

    private val logger = Logger.getLogger(L.SERVER_TAG, "VmProcessManager")

    /**
     * 用户id: [进程名称：进程信息]
     */
    private val processMap: MutableMap<Int, MutableList<VmAppProcess>> = HashMap(10)

    private val allProcessList: CopyOnWriteArrayList<VmProcessRecord> = CopyOnWriteArrayList()

    private val userProcessMapLock = Any()

    fun findAppProcessWithId(packageName: String, userId: Int): VmAppProcess?{
        if (processMap.containsKey(userId)){
            val userVmAppProcessList = processMap[userId]!!
            for (vmAppProcess in userVmAppProcessList) {
                if (packageName == vmAppProcess.packageName){
                    return vmAppProcess
                }
            }
        }
        return null
    }

    fun findAppProcess(packageName: String): List<VmAppProcess>{
        val result = mutableListOf<VmAppProcess>()
        for (appProcessList in processMap.values) {
            for (vmAppProcess in appProcessList) {
                if (packageName == vmAppProcess.vmPackageName){
                    result.add(vmAppProcess)
                }
            }
        }
        return result
    }

    fun findProcess(packageName: String): List<VmProcessRecord>{
        val result = mutableListOf<VmProcessRecord>()
        for (vmProcessRecord in allProcessList) {
            if (packageName == vmProcessRecord.packageName){
                result.add(vmProcessRecord)
            }
        }
        return result
    }

    fun findProcess(packageName: String, processName: String): VmProcessRecord?{
        for (vmProcessRecord in allProcessList) {
            if (packageName == vmProcessRecord.packageName && processName == vmProcessRecord.processName){
                return vmProcessRecord
            }
        }
        return null
    }

    fun findProcess(caller: IAppApplicationThread): VmProcessRecord?{
        for (vmProcessRecord in allProcessList) {
            if (vmProcessRecord.applicationThread == caller){
                return vmProcessRecord
            }
        }
        return null
    }

    /**
     * 准备启动一个新的应用进程，启动一个新应用进程时调用
     *
     * 应用进程-应用子进程
     */
    fun startVmAppProcess(vmApplicationInfo: ApplicationInfo, userId: Int): VmAppProcess {
        val existsVmAppProcess = getUserProcessWithLock(userId, vmApplicationInfo)
        // 如果主进程创建成功后，就不继续创建了，创建处理由
        if (existsVmAppProcess.checkMainProcess()){
            return existsVmAppProcess
        }
        // 获取一个可用的进程id
        val vmPid = ProcessHelper.findAvailableVmPid()

        synchronized(existsVmAppProcess.appProcessHandleLock){
            // 创建进程记录
            val prepareVmProcessRecord = VmProcessRecord(vmApplicationInfo, userId, vmPid)
            // 进程启动成功, 检查VmAppProcess是否存在主进程，如果存在，本进程作为子进程
            existsVmAppProcess.checkAndSetProcess(prepareVmProcessRecord)
            if (startVmProxyProcess(prepareVmProcessRecord)) {
                // 进程启动成功，将进程信息添加到列表中
                allProcessList.add(prepareVmProcessRecord)
            }else{
                // 移除进程
                val vmAppProcessList: MutableList<VmAppProcess> = processMap[userId]!!
                vmAppProcessList.remove(existsVmAppProcess)
                // 进程启动失败，关闭app进程已经其子进程
                existsVmAppProcess.killAppProcess()
            }
        }
        if (existsVmAppProcess.hasKilled){
            throw IllegalStateException("startAppProcess fail, process killed")
        }
        return existsVmAppProcess
    }

    /**
     * 启动子进程
     */
    fun startVmSubProcess(vmApplicationInfo: ApplicationInfo, vmAppProcess: VmAppProcess) : Int{
        if (!vmAppProcess.checkMainProcess()){
            throw RuntimeException("主进程不存在，不允许创建${vmApplicationInfo.processName}进程")
        }
        if (vmApplicationInfo.processName == vmAppProcess.processName){
            logger.e("创建子进程失败，子进程进程名与主进程相同 %s", vmAppProcess.processName)
            return -1
        }
        val vmPid = ProcessHelper.findAvailableVmPid()
        synchronized(vmAppProcess.appProcessHandleLock){
            // 创建进程记录
            val prepareVmProcessRecord = VmProcessRecord(vmApplicationInfo, vmAppProcess.userId, vmPid)
            if (startVmProxyProcess(prepareVmProcessRecord)) {
                logger.i("启动子进程 ${vmApplicationInfo.processName} 成功，添加进程记录")
                // 进程启动成功, 检查VmAppProcess是否存在主进程，如果存在，本进程作为子进程
                vmAppProcess.checkAndSetProcess(prepareVmProcessRecord)
                // 进程启动成功，将进程信息添加到列表中
                allProcessList.add(prepareVmProcessRecord)
                return vmPid
            }else{
                logger.e("启动子进程 ${vmApplicationInfo.processName} 失败")
                return -1
            }
        }
    }

    fun checkProcessExists(packageName: String, processName: String, userId: Int = 0): Boolean{
        if (processMap.containsKey(userId)){
            val appProcessList = processMap[userId]!!
            for (vmAppProcess in appProcessList) {
                if (vmAppProcess.packageName == packageName){
                    return vmAppProcess.checkProcessExists(processName)
                }
            }
        }
        return false
    }

    private fun startVmProxyProcess(vmProcessRecord: VmProcessRecord): Boolean {
        val bundle = Bundle()
        val mainProcess = checkAndGetAppMainProcessWithLock(vmProcessRecord.packageName)
        val vmAppConfig = VmAppConfig().apply {
            processName = vmProcessRecord.processName ?: ""
            packageName = vmProcessRecord.packageName ?: ""
            userId = mainProcess?.userId ?: -1
            this.vmProcessRecord = vmProcessRecord
            this.isMainProcess = mainProcess != null
            this.mainProcessVmPid = vmProcessRecord.vmPid
            this.mainProcessSystemPid = mainProcess?.mainProcessRecord?.systemPid ?: -1
            this.mainProcessSystemUid = mainProcess?.mainProcessRecord?.systemUid ?: -1
        }
        bundle.putParcelable(VmAppConfig.IPC_BUNDLE_KEY,vmAppConfig)
        val resultBundle = ProviderCallHelper.callSafely(
            vmProcessRecord.getProxyAuthority(),
            ProxyContentProvider.IPC_VM_INIT_METHOD_NAME, null, bundle
        ) ?: return false
        val vmApplicationThreadHandle = resultBundle.getBinder(ProxyContentProvider.IPC_VM_BINDER_HANDLE_KEY)
        if (vmApplicationThreadHandle == null || !vmApplicationThreadHandle.isBinderAlive) {
            return false
        }
        //
        val stubPid = resultBundle.getInt(ProxyContentProvider.IPC_VM_CUR_PID_KEY)
        val stubUid = resultBundle.getInt(ProxyContentProvider.IPC_VM_CUR_UID_KEY)
        val stubProcessName = resultBundle.getString(ProxyContentProvider.IPC_VM_PROXY_PROCESS_NAME_KEY)
        vmProcessRecord.apply {
            systemPid = stubPid
            systemUid = stubUid
            systemProcessName = stubProcessName
            applicationThread = IAppApplicationThread.Stub.asInterface(vmApplicationThreadHandle)
            linkToDeath()
        }
        return true
    }

    /**
     * 根据包名获取虚拟应用主进程信息
     */
    private fun checkAndGetAppMainProcessWithLock(packageName: String?): VmAppProcess?{
        if (packageName == null){
            return null
        }
        synchronized(userProcessMapLock){
            for (appProcessList in processMap.values) {
                for (vmAppProcess in appProcessList) {
                    if (packageName == vmAppProcess.processName){
                        return vmAppProcess
                    }
                }
            }
            return null
        }
    }

    private fun getUserProcessWithLock(userId: Int, vmApplicationInfo: ApplicationInfo): VmAppProcess{
        synchronized(userProcessMapLock){
            val vmAppProcessList: MutableList<VmAppProcess> = if (processMap.containsKey(userId)) {
                processMap[userId]!!
            } else {
                // 创建一个新的app进程
                ArrayList<VmAppProcess>(10).apply {
                    processMap[userId] = this
                }
            }

            // 判断应用进程是否存在
            val vmProcessName = vmApplicationInfo.processName
            val vmPackageName = vmApplicationInfo.packageName
            var existsVmAppProcess = vmAppProcessList.find { it.packageName == vmPackageName }
            if (existsVmAppProcess == null) {
                // 应用进程不存在，创建新的应用进程信息 TODO 这边appid后续补充
                existsVmAppProcess = VmAppProcess(0, userId,vmPackageName, vmProcessName)
            }
            // 将主进程添加到用户对应的进程中
            vmAppProcessList.add(existsVmAppProcess)
            return existsVmAppProcess
        }
    }

    fun killProcess(packageName: String?, userId: Int) {
        synchronized(userProcessMapLock){
            val userRunAppProcessList = processMap[userId]
            userRunAppProcessList?.run {
                val appProcess = find { it.packageName == packageName } ?: return@run
                // 移除主进程
                allProcessList.remove(appProcess.mainProcessRecord!!)
                appProcess.currentAppProcessRecord.forEach { (_, record) ->
                    allProcessList.remove(record)
                }
                appProcess.killAppProcess()
                // 移除app进程
                remove(appProcess)
            }
        }
    }


}