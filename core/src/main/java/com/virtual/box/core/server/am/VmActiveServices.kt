package com.virtual.box.core.server.am

import android.content.ComponentName
import android.content.Intent
import android.content.Intent.FilterComparison
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import androidx.collection.ArrayMap
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmProxyServiceRecord
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest
import com.virtual.box.core.server.am.entity.RunningServiceRecord
import com.virtual.box.core.server.am.entity.VmServiceRecord
import com.virtual.box.core.server.pm.VmPackageManagerService
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class VmActiveServices {

    private val logger = Logger.getLogger(L.SERVER_TAG, "VmActiveServices")

    val runService = ArrayMap<FilterComparison, VmServiceRecord>()

    private val recordLock = Any()


    fun startServiceLock(service: Intent?, resolvedType: String?, requireForeground: Boolean, userId: Int): ComponentName? {
        if (service == null) {
            logger.e("startService#fail intent == null")
            return null
        }

        val resolveInfo = VmPackageManagerService.resolveService(service, resolvedType, 0, userId)
        if (resolveInfo == null) {
            logger.e("startService#fail, resolvedService == null, intent = %s", service)
            return null
        }

        val serviceInfo = resolveInfo.serviceInfo!!
        val packageName = serviceInfo.packageName
        val processName = serviceInfo.processName ?: packageName

        val initNewProcess = try {
            VmActivityManagerService.initNewProcess(packageName, processName, userId)
        } catch (e: Exception) {
            logger.e("startService#fial, unable create new process packageName = %s, processName = %s", packageName, processName)
            return null
        }

        if (initNewProcess.vmProcessRecord?.applicationThread == null){
            VmProcessManager.killProcess(initNewProcess.packageName, initNewProcess.userId)
            logger.e("startService#失败，[%s]appConfig中不存在IAppApplicationThread的Binder引用", initNewProcess.packageName)
            return null
        }
        val vmPid = initNewProcess.getVmPidByProcess(processName)
        val targetApplicationThread = initNewProcess.vmProcessRecord!!.applicationThread!!
        if (!startDispatcherServiceLock(targetApplicationThread, vmPid)){
            return null
        }

        val runningServiceRecord = getOrCreateRunningServiceRecord(service, serviceInfo)
        val serviceProcessUserId = initNewProcess.userId

        try {
            targetApplicationThread.scheduleCreateService()
        } catch (e: Exception) {
            removeRunningServiceRecord(runningServiceRecord)
            logger.e(e)
            null
        } ?: return null

        return service.component
    }

    private fun startDispatcherServiceLock(caller: IAppApplicationThread, vmPid: Int): Boolean{
        synchronized(recordLock) {
            if (!processServices.containsKey(caller)){
                val shadowIntent = createStubService(vmPid)
                try {
                    if (BuildCompat.isAtLeastOreo) {
                        VirtualBox.get().hostContext.startForegroundService(shadowIntent)
                    } else {
                        VirtualBox.get().hostContext.startService(shadowIntent)
                    }
                } catch (e: Exception) {
                    logger.e(e)
                    null
                } ?: return false
                processServices[caller] = mutableMapOf()
                return true
            }
            return true
        }
    }

    fun stopService(intent: Intent?, resolvedType: String?, userId: Int): Int {
        intent ?: return -1
        val resolveInfo = VmPackageManagerService.resolveService(intent, resolvedType, 0, userId)
        if (resolveInfo == null) {
            logger.e("stopService#fail, resolvedService == null, intent = %s", intent)
            return -1
        }
        val serviceInfo = resolveInfo.serviceInfo!!
        val packageName = serviceInfo.packageName
        val processName = serviceInfo.processName ?: packageName
        val findRunningServiceRecord = findRunningServiceRecord(intent)
        if (findRunningServiceRecord == null) {
            logger.i("stopService#fail not found running service record")
            return -1
        }
        findRunningServiceRecord.startId.set(0)

        val processRecord = VmProcessManager.findProcess(packageName, processName)
        if (processRecord == null) {
            logger.i("stopService#fail not fount process record")
            return -1
        }

        try {
//            processRecord.applicationThread?.schduleStopService(intent)
        } catch (e: RemoteException) {
            logger.e(e)
        }
        return 0
    }

    private fun createStubService(vmPid: Int):Intent{
        val shadow = Intent()
        val shadowComponentName = ComponentName(
            VirtualBox.get().hostPkg,
            ProxyManifest.getProxyService(vmPid)
        )
        shadow.component = shadowComponentName
        shadow.action = UUID.randomUUID().toString()
        return shadow
    }

    private fun createStubServiceIntent(
        targetIntent: Intent,
        serviceInfo: ServiceInfo,
        runningServiceRecord: RunningServiceRecord,
        userId: Int,
        vmPid: Int
    ): Intent {
        val shadow = Intent()
        val shadowComponentName = ComponentName(
            VirtualBox.get().hostPkg,
            ProxyManifest.getProxyService(vmPid)
        )
        shadow.component = shadowComponentName
        shadow.action = UUID.randomUUID().toString()
        VmProxyServiceRecord.saveStubInfo(
            shadow,
            targetIntent,
            serviceInfo,
            runningServiceRecord,
            userId,
            runningServiceRecord.incrementBindCountAndGet()
        )
        return shadow
    }

    private fun getOrCreateRunningServiceRecord(intent: Intent, serviceInfo: ServiceInfo): RunningServiceRecord {
        synchronized(recordLock) {
            var runningServiceRecord = findRunningServiceRecord(intent)
            if (runningServiceRecord == null) {
                runningServiceRecord = RunningServiceRecord(intent, serviceInfo)
                runningServiceRecords[FilterComparison(intent)] = runningServiceRecord
                runningServiceTokens.add(runningServiceRecord)
            }
            return runningServiceRecord
        }
    }

    private fun removeRunningServiceRecord(runningServiceRecord: RunningServiceRecord) {
        synchronized(recordLock) {
            runningServiceRecords.remove(FilterComparison(runningServiceRecord.intent!!))
            runningServiceTokens.remove(runningServiceRecord)
        }
    }

    private fun findRunningServiceRecord(intent: Intent): RunningServiceRecord? {
        return runningServiceRecords[FilterComparison(intent)]
    }

    private fun findRunningServiceByToken(token: IBinder): RunningServiceRecord? {
        return runningServiceTokens.find { it == token } as RunningServiceRecord?
    }


}