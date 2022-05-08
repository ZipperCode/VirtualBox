package com.virtual.box.core.manager

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.BuildConfig
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.entity.VmAppProcess
import com.virtual.box.core.entity.VmProcessRecord
import com.virtual.box.core.helper.ProviderCallHelper
import com.virtual.box.core.proxy.ProxyContentProvider
import com.virtual.box.core.proxy.ProxyManifest
import java.lang.IllegalStateException
import java.lang.RuntimeException

object VmProcessManager {

    private val logger = Logger.getLogger(L.SERVER_TAG, "VmProcessManager")

    /**
     * 用户id: [进程名称：进程信息]
     */
    private val processMap: MutableMap<Int, MutableList<VmAppProcess>> = HashMap(10)

    private val allProcessList: MutableList<VmProcessRecord> = ArrayList(50)

    private val userProcessMapLock = Any()



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
        val vmPid = findAvailableVmPid()

        synchronized(existsVmAppProcess.appProcessHandleLock){
            // 创建进程记录
            val prepareVmProcessRecord = VmProcessRecord(vmApplicationInfo, userId, vmPid)
            if (startVmProxyProcess(prepareVmProcessRecord)) {
                // 进程启动成功, 检查VmAppProcess是否存在主进程，如果存在，本进程作为子进程
                existsVmAppProcess.checkAndSetProcess(prepareVmProcessRecord)
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
        return existsVmAppProcess
    }

    /**
     * 启动子进程
     */
    fun startVmSubProcess(vmApplicationInfo: ApplicationInfo, vmAppProcess: VmAppProcess){
        if (!vmAppProcess.checkMainProcess()){
            throw RuntimeException("主进程不存在，不允许创建${vmApplicationInfo.processName}进程")
        }
        if (vmApplicationInfo.processName == vmAppProcess.processName){
            logger.e("创建子进程失败，子进程进程名与主进程相同 %s", vmAppProcess.processName)
            return
        }
        val vmPid = findAvailableVmPid()
        synchronized(vmAppProcess.appProcessHandleLock){
            // 创建进程记录
            val prepareVmProcessRecord = VmProcessRecord(vmApplicationInfo, vmAppProcess.userId, vmPid)
            if (startVmProxyProcess(prepareVmProcessRecord)) {
                logger.i("启动子进程 ${vmApplicationInfo.processName} 成功，添加进程记录")
                // 进程启动成功, 检查VmAppProcess是否存在主进程，如果存在，本进程作为子进程
                vmAppProcess.checkAndSetProcess(prepareVmProcessRecord)
                // 进程启动成功，将进程信息添加到列表中
                allProcessList.add(prepareVmProcessRecord)
            }else{
                logger.e("启动子进程 ${vmApplicationInfo.processName} 失败")
            }
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

    fun startVmProxyProcess(vmProcessRecord: VmProcessRecord): Boolean {
        val bundle = Bundle()
        bundle.putBinder(VmProcessRecord.SERVER_2_CLIENT_PROCESS_RECORD_KEY, vmProcessRecord)
        val resultBundle = ProviderCallHelper.callSafely(
            vmProcessRecord.getProxyAuthority(),
            ProxyContentProvider.IPC_VM_INIT_METHOD_NAME, null, bundle
        ) ?: return false
        val vmActivityThreadHandle = resultBundle.getBinder(ProxyContentProvider.IPC_VM_BINDER_HANDLE_KEY)
        if (vmActivityThreadHandle == null || !vmActivityThreadHandle.isBinderAlive) {
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
        }
        return true
    }

    fun initVmProcess() {

    }

    fun killProcess(packageName: String, vmUid: Int) {
        processMap[vmUid]?.find { it.packageName == packageName }?.killAppProcess()
    }

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