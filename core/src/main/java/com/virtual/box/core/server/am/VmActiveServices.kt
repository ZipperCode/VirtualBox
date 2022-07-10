package com.virtual.box.core.server.am

import android.app.IServiceConnection
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.FilterComparison
import android.content.pm.ServiceInfo
import android.os.*
import android.util.SparseArray
import androidx.core.util.containsKey
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmProcessRecord
import com.virtual.box.core.entity.VmProxyServiceRecord
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest
import com.virtual.box.core.server.am.entity.RunningServiceRecord
import com.virtual.box.core.server.am.entity.VmConnectionServiceRecord
import com.virtual.box.core.server.am.entity.VmServiceRecord
import com.virtual.box.core.server.pm.VmPackageManagerService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class VmActiveServices {

    companion object {
        /**
         * 30s
         */
        const val SCHEDULE_TIMEOUT_TIME = 40 * 1000L

        const val SCHEDULE_SERVICE_START = 0x1000
        const val SCHEDULE_SERVICE_STOP = 0x1001
        const val SCHEDULE_SERVICE_BIND = 0x1002
        const val SCHEDULE_SERVICE_UNBIND = 0x1003
        const val SCHEDULE_SERVICE_START_TIMEOUT = 0x1111
        const val SCHEDULE_SERVICE_STOP_TIMEOUT = 0x1112
        const val SCHEDULE_SERVICE_BIND_TIMEOUT = 0x1113
    }

    private val logger = Logger.getLogger(L.SERVER_TAG, "VmActiveServices")

    val userServiceMapLock = Any()

    val userServiceMap = SparseArray<ServiceMap>()

    val runBindService = HashMap<IBinder, VmServiceRecord>()

    /**
     * 启动中的服务
     */
    val waitStartingServices = mutableListOf<VmServiceRecord>()

    private val waitStartingLock = ConditionVariable()

    private val globalServiceConnectionMap = HashMap<IBinder, ArrayList<VmConnectionServiceRecord>>()

    private val serviceLock = Any()

    fun startService(service: Intent?, resolvedType: String?, requireForeground: Boolean, userId: Int): ComponentName? {
        if (service == null) {
            logger.e("startService#fail intent == null")
            return null
        }
        val sRecord = retrieveServiceLocked(service, resolvedType, userId)
        if (sRecord == null) {
            logger.e("startService#fial retrieveServiceLocked == null")
            return null
        }

        startServiceInnerLock(sRecord)
        return sRecord.componentName
    }

    fun stopService(service: Intent?, resolvedType: String?, userId: Int): Int {
        if (service == null) {
            logger.e("stopService#fail intent == null")
            return 0
        }
        val sRecord = retrieveServiceLocked(service, resolvedType, userId, false)
        if (sRecord == null) {
            logger.e("stopService#fial 不存在需要停止的服务记录")
            return 0
        }
        return stopServiceInnerLock(sRecord)
    }

    fun bindService(
        caller: IAppApplicationThread?,
        service: Intent?,
        token: IBinder?,
        conn: IServiceConnection?,
        resolvedType: String?,
        userId: Int
    ): Int {
        if (caller == null) {
            logger.e("bindService#fail caller == null")
            return -1
        }

        if (service == null) {
            logger.e("bindService#fail intent == null")
            return 0
        }
        if (token == null){
            logger.e("bindService#fail token == null")
            return 0
        }
        if (conn == null){
            logger.e("bindService#fail conn == null")
            return 0
        }

        val callingProcess = VmProcessManager.findProcess(caller)
        if (callingProcess == null) {
            logger.e("bindService#fail 未找到进程信息，无法进行绑定")
            return -1
        }

        val sRecord = retrieveServiceLocked(service, resolvedType, userId)
        if (sRecord == null) {
            logger.e("startService#fial retrieveServiceLocked == null")
            return 0
        }

        return bindServiceInnerLock(sRecord, service, token, callingProcess, conn)
    }

    fun unbindService(conn: IServiceConnection?): Boolean{
        if (conn == null){
            logger.e("unbindService#fial conn == null")
            return false
        }
        val binder = conn.asBinder()
        val globalConnList = globalServiceConnectionMap[binder]
        if (globalConnList == null){
            logger.e("unbindService#fial globalConnList == null")
            return false
        }
        val iterator = globalConnList.iterator()
        while (iterator.hasNext()){
            val entry = iterator.next()
            val sRecord = entry.serviceRecord
            val token = entry.token
            try {
                val intent = entry.intent
                sRecord.app?.applicationThread?.scheduleUnbindService(token, intent)
            }catch (e: Exception){
                L.printStackTrace(e)
            }
            sRecord.unbindService(token, entry)
            iterator.remove()
            sRecord.onServiceUnBind()
        }
        return true
    }

    fun peekServiceLocked(service: Intent?, resolvedType: String?, userId: Int): IBinder?{
        if (service == null) {
            logger.e("peekServiceLocked#fail intent == null")
            return null
        }
        val sRecord = retrieveServiceLocked(service, resolvedType, userId, false)
        if (sRecord == null) {
            logger.e("peekServiceLocked#fial retrieveServiceLocked == null")
            return null
        }
        return sRecord
    }

    /**
     * @param token VmServiceRecord
     * @param binder service onBind result
     */
    fun publishService(token: IBinder, intent: Intent?, binder: IBinder?) {
        if (token !is VmServiceRecord) {
            logger.e("publishService#fail token type is not VmServiceRecord")
            return
        }
        val filter = FilterComparison(intent)
        for (entry in token.connections) {
            for (vmConnectionServiceRecord in entry.value) {
                if (filter != FilterComparison(vmConnectionServiceRecord.intent)) {
                    continue
                }
                try {
                    vmConnectionServiceRecord.conn.connected(vmConnectionServiceRecord.getComponentName(), binder)
                } catch (e: Exception) {
                    L.printStackTrace(e)
                }
            }
        }
    }

    fun serviceDoneExecuting(token: VmServiceRecord, type: Int, startId: Int) {

    }

    fun stopServiceTokenLocked(componentName: ComponentName, token: IBinder, startId: Int, userId: Int): Boolean{
        synchronized(serviceLock){
            val serviceMap = getServiceMapLock(userId)
            val serviceRecord = serviceMap.runService[componentName]
            if (serviceRecord != null){
                if (startId >= 0){
                    serviceRecord.onServiceOnStop()
                    if(serviceRecord.startId.get() == startId){
                        return false
                    }
                }else{
                    for (entry in serviceRecord.connections) {
                        for (vmConnectionServiceRecord in entry.value) {
                            vmConnectionServiceRecord.hasBind.set(false)
                            try {
                                serviceRecord.app?.applicationThread?.scheduleUnbindService(serviceRecord, vmConnectionServiceRecord.intent)
                            }catch (e: Exception){
                                L.printStackTrace(e)
                            }finally {
                                vmConnectionServiceRecord.hasDeath.set(true)
                            }
                        }
                    }
                    serviceMap.clearServiceRecordLock(serviceRecord)
                    return true
                }
            }
            return false
        }
    }

    private fun startServiceInnerLock(sRecord: VmServiceRecord) {
        synchronized(serviceLock) {
            ensureServiceStarting(sRecord)
            val serviceMap = getServiceMapLock(sRecord.userId)
            val msg = Message.obtain()
            msg.what = SCHEDULE_SERVICE_START
            msg.obj = sRecord
            sRecord.executingStart = SystemClock.uptimeMillis();
            serviceMap.sendMessage(msg)
            val timeoutMsg = Message.obtain()
            timeoutMsg.what = SCHEDULE_SERVICE_START_TIMEOUT
            timeoutMsg.obj
            serviceMap.sendMessageDelayed(timeoutMsg, SCHEDULE_TIMEOUT_TIME)
        }
    }

    private fun ensureServiceStarting(sRecord: VmServiceRecord) {
        if (ensureServiceInStarting(sRecord)) {
            return
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            waitStartingServices.add(sRecord)
        } else {
            VmActivityManagerService.vmHandler.post {
                waitStartingServices.add(sRecord)
                waitStartingLock.open()
            }
            waitStartingLock.block()
        }
    }

    private fun ensureServiceInStarting(sRecord: VmServiceRecord): Boolean {
        return waitStartingServices.contains(sRecord)
    }

    private fun scheduleStartServiceInner(serviceMap: ServiceMap, sRecord: VmServiceRecord) {
        var throwable: Throwable? = null
        try {
            if (!ensureServiceInStarting(sRecord)) {
                throw IllegalStateException("scheduleStartService#fail 不存在等待启动的服务")
            }
            val targetApplicationThread = handleServiceAttachApplication(sRecord)
            if (!sRecord.isStarted.get()) {
                targetApplicationThread.scheduleCreateService(sRecord, sRecord.serviceInfo, sRecord.intent)
                sRecord.isStarted.set(true)
            }
//            val flag = if (sRecord.hasDeath.get()){
//                Service.START_FLAG_REDELIVERY
//            }else{
//                Service.START_FLAG_RETRY
//            }
            targetApplicationThread.scheduleServiceArgs(sRecord, sRecord.startId.get(), sRecord.intent)
            sRecord.serviceOnStart()
        } catch (e: Exception) {
            throwable = e
            L.printStackTrace(e)
        } finally {
            serviceMap.removeMessages(SCHEDULE_SERVICE_START_TIMEOUT)
            waitStartingServices.remove(sRecord)
            if (throwable != null) {
                serviceMap.clearServiceRecordLock(sRecord)
            }
        }
    }

    private fun handleServiceAttachApplication(sRecord: VmServiceRecord): IAppApplicationThread {
        val serviceInfo = sRecord.serviceInfo
        val packageName = serviceInfo.packageName
        val processName = serviceInfo.processName ?: packageName
        val initNewProcess = VmActivityManagerService.initNewProcess(packageName, processName, sRecord.userId)

        if (initNewProcess.vmProcessRecord?.applicationThread == null) {
            VmProcessManager.killProcess(initNewProcess.packageName, initNewProcess.userId)
            throw IllegalStateException("startService#失败，[${initNewProcess.packageName}]appConfig中不存在IAppApplicationThread的Binder引用")
        }
        sRecord.app = initNewProcess.vmProcessRecord
        val targetApplicationThread = initNewProcess.vmProcessRecord!!.applicationThread!!
        targetApplicationThread.attachApplication(initNewProcess)
        return targetApplicationThread
    }

    private fun scheduleStartServiceTimeoutInnerLock(serviceMap: ServiceMap, sRecord: VmServiceRecord) {
        val startTime = sRecord.executingStart
        val now = SystemClock.uptimeMillis()
        if (startTime + SCHEDULE_TIMEOUT_TIME > now) {
            serviceMap.clearServiceRecordLock(sRecord)
        }
        waitStartingServices.remove(sRecord)
    }

    private fun stopServiceInnerLock(sRecord: VmServiceRecord): Int {
        synchronized(serviceLock) {


            val serviceMap = getServiceMapLock(sRecord.userId)
            if (ensureServiceInStarting(sRecord)) {
                // 正在启动
                sRecord.onServiceOnStop()
                serviceMap.removeMessages(SCHEDULE_SERVICE_START)
                serviceMap.removeMessages(SCHEDULE_SERVICE_START_TIMEOUT)
            } else {
                // 已经启动了
                val msg = Message.obtain()
                msg.what = SCHEDULE_SERVICE_STOP
                msg.obj = sRecord
                serviceMap.sendMessage(msg)
            }
        }
        return 0
    }

    private fun scheduleStopServiceInner(serviceMap: ServiceMap, sRecord: VmServiceRecord) {
        try {
            if (ensureServiceInStarting(sRecord)) {
                waitStartingServices.remove(sRecord)
            }
            serviceMap.removeMessages(SCHEDULE_SERVICE_START)
            serviceMap.removeMessages(SCHEDULE_SERVICE_START_TIMEOUT)

            // TODO startId ??
            sRecord.app?.applicationThread?.schduleStopService(sRecord, sRecord.intent)
            sRecord.onServiceOnStop()
        } catch (e: Exception) {
            L.printStackTrace(e)
        }
    }

    private fun bindServiceInnerLock(
        sRecord: VmServiceRecord,
        intent: Intent,
        token: IBinder,
        callingProcessRecord: VmProcessRecord,
        conn: IServiceConnection
    ): Int {
        synchronized(serviceLock) {
            val packageName = callingProcessRecord.packageName ?: ""
            val processName = callingProcessRecord.processName ?: ""
            val connRecord = VmConnectionServiceRecord(
                sRecord,
                token, intent,
                packageName, processName, conn, sRecord.serviceInfo
            )
            if (sRecord.connections.containsKey(token)) {
                var csRecordList = sRecord.connections[token]
                if (csRecordList == null) {
                    csRecordList = ArrayList()
                    sRecord.connections[token] = csRecordList
                }
                csRecordList.add(connRecord)
            }
            var globalConnList = globalServiceConnectionMap[token]
            if (globalConnList == null){
                globalConnList = ArrayList()
                globalServiceConnectionMap[token] = globalConnList
            }
            globalConnList.add(connRecord)
            ensureServiceStarting(sRecord)
            val serviceMap = getServiceMapLock(sRecord.userId)
            val msg = Message.obtain()
            msg.what = SCHEDULE_SERVICE_BIND
            msg.obj = sRecord
            sRecord.executingStart = SystemClock.uptimeMillis();
            serviceMap.sendMessage(msg)
            val timeoutMsg = Message.obtain()
            timeoutMsg.what = SCHEDULE_SERVICE_BIND_TIMEOUT
            timeoutMsg.obj
            serviceMap.sendMessageDelayed(timeoutMsg, SCHEDULE_TIMEOUT_TIME)
        }
        return 1
    }

    private fun scheduleBindService(serviceMap: ServiceMap, sRecord: VmServiceRecord) {
        var throwable: Throwable? = null
        try {
            val targetApplicationThread = handleServiceAttachApplication(sRecord)
            if (!sRecord.isStarted.get()) {
                targetApplicationThread.scheduleCreateService(sRecord, sRecord.serviceInfo, sRecord.intent)
            }

            val iterator = sRecord.connections.iterator()
            while (iterator.hasNext()) {
                val connRecordMap = iterator.next()
                val connToken = connRecordMap.key
                val connRecordList = connRecordMap.value
                if (!connToken.isBinderAlive) {
                    iterator.remove()
                    continue
                }
                for (connRecord in connRecordList) {
                    val hasBind = connRecord.hasBind.get()
                    if (!hasBind) {
                        connToken.linkToDeath(connRecord.setAndGetOuterBinderDeathRecipient {
                            try {
                                sRecord.app?.applicationThread?.scheduleUnbindService(connToken, connRecord.intent)
                                connToken.unlinkToDeath(connRecord, 0)
                                connRecord.hasDeath.set(true)
                            } catch (e: Exception) {
                                L.printStackTrace(e)
                            }
                        }, 0)
                        connRecord.hasBind.set(true)
                    }
                    sRecord.onServiceBind()
                    sRecord.app?.applicationThread?.scheduleBindService(connToken, connRecord.intent, hasBind)
                }


                // publishService
                // serviceDoneExecuting
            }
        } catch (e: Exception) {
            throwable = e
            L.printStackTrace(e)
        } finally {
            serviceMap.removeMessages(SCHEDULE_SERVICE_START_TIMEOUT)
            waitStartingServices.remove(sRecord)
            if (throwable != null) {
                serviceMap.clearServiceRecordLock(sRecord)
            }
        }
    }

    private fun scheduleUnBindService(serviceMap: ServiceMap, sRecord: VmServiceRecord) {

    }

    private fun getServiceMapLock(userId: Int): ServiceMap {
        synchronized(userServiceMapLock) {
            if (!userServiceMap.containsKey(userId)) {
                val sMap = ServiceMap(userId, VmActivityManagerService.vmHandler.looper)
                userServiceMap.put(userId, sMap)
            }
            return userServiceMap[userId]
        }
    }

    private fun retrieveServiceLocked(service: Intent, resolvedType: String?, userId: Int, needCreate: Boolean = true): VmServiceRecord? {
        synchronized(userServiceMapLock) {
            val sMap = getServiceMapLock(userId)
            return sMap.retrieveServiceLocked(service, resolvedType, needCreate)
        }
    }


    private fun createStubService(vmPid: Int): Intent {
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


    inner class ServiceMap(val userId: Int, looper: Looper) : Handler(looper) {

        private val recordLock = Any()

        val runService = HashMap<ComponentName, VmServiceRecord>()

        val runIntentService = HashMap<FilterComparison, VmServiceRecord>()

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SCHEDULE_SERVICE_START -> {
                    scheduleStartServiceInner(this, msg.obj as VmServiceRecord)
                }
                SCHEDULE_SERVICE_START_TIMEOUT -> {
                    scheduleStartServiceTimeoutInnerLock(this, msg.obj as VmServiceRecord)
                }
                SCHEDULE_SERVICE_STOP -> {
                    scheduleStopServiceInner(this, msg.obj as VmServiceRecord)
                }
                SCHEDULE_SERVICE_BIND -> {
                    scheduleBindService(this, msg.obj as VmServiceRecord)
                }
                SCHEDULE_SERVICE_UNBIND -> {
                    scheduleUnBindService(this, msg.obj as VmServiceRecord)
                }
            }
        }

        fun retrieveServiceLocked(service: Intent, resolvedType: String?, needCreate: Boolean): VmServiceRecord? {
            synchronized(recordLock) {
                val componentName = service.component
                var r: VmServiceRecord? = null
                if (componentName != null) {
                    r = runService[componentName]
                }

                if (r == null) {
                    val filter = FilterComparison(service.cloneFilter())
                    r = runIntentService[filter]
                }

                if (r != null) {
                    // 检查进程是否存在
                    val processName = r.serviceInfo.processName ?: r.serviceInfo.packageName
                    if (VmProcessManager.checkProcessExists(r.serviceInfo.packageName, processName, userId)) {
                        return r
                    }
                }
                if (r == null && needCreate) {
                    // 新创建的服务
                    val resolveInfo = VmPackageManagerService.resolveService(service, resolvedType, 0, userId)
                    if (resolveInfo == null) {
                        logger.e("retrieveServiceLocked#fail, resolvedService == null, intent = %s", service)
                        return null
                    }
                    val serviceInfo = resolveInfo.serviceInfo
                    if (serviceInfo == null) {
                        logger.e("retrieveServiceLocked#fail, serviceInfo == null, intent = %s", service)
                        return null
                    }

                    val className = componentName ?: ComponentName(serviceInfo.packageName, serviceInfo.name)
                    val filter = FilterComparison(service.cloneFilter())
                    r = VmServiceRecord(userId, className, service, serviceInfo)
                    runService[className] = r
                    runIntentService[filter] = r
                }

                return r
            }
        }


        fun clearServiceRecordLock(vmServiceRecord: VmServiceRecord) {
            synchronized(recordLock) {
                val className = vmServiceRecord.componentName
                val filter = vmServiceRecord.filter
                runService.remove(className)
                runIntentService.remove(filter)
            }
        }
    }

}