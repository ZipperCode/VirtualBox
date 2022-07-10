package com.virtual.box.core.manager

import android.app.ActivityThread
import android.app.IServiceConnection
import android.content.ComponentName
import android.content.Intent
import android.os.Debug
import android.os.IBinder
import android.os.RemoteException
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.helper.IntentHelper
import com.virtual.box.core.server.am.IVmActivityManagrService
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.servertransaction.HClientTransaction
import com.virtual.box.reflect.android.app.servertransaction.HLaunchActivityItem
import java.lang.ref.WeakReference

object AppActivityManager {
    private val logger = Logger.getLogger(L.HOST_TAG, "VmActivityManager")

    private var service: IVmActivityManagrService? = null

    fun startActivity(intent: Intent, userId: Int){
        try {
            requireService().startActivity(intent, userId)
        }catch (e: RemoteException){
            logger.e(e)
        }
    }

    fun launchActivity(intent: Intent, userId: Int){
        try {
            requireService().launchActivity(intent, userId)
        }catch (e: RemoteException){
            logger.e(e)
        }
    }

    fun prepareStartActivity(intent: Intent, userId: Int): Intent?{
        return try {
            requireService().prepareStartActivity(intent, userId)
        }catch (e: RemoteException){
            logger.e(e)
            null
        }
    }
    /**
     * 处理打开Activity
     *
     * @param client Android>=9: ClientTransaction Android<9:ActivityClientRecord
     */
    fun restoreOriginAdnHandleActivity(client: Any) : Boolean{
        val r: Any = if (BuildCompat.isAtLeastPie) {
            // ClientTransaction
            getLaunchActivityItem(client)
        } else {
            // ActivityClientRecord
            client
        } ?: return false

        var intent: Intent? = null
        var token: IBinder? = null
        if (BuildCompat.isAtLeastPie) {
            intent = HLaunchActivityItem.mIntent.get(r)
            token = HClientTransaction.mActivityToken.get(client)
        } else {
            intent = HActivityThread.ActivityClientRecord.intent.get(r)
            token = HActivityThread.ActivityClientRecord.token.get(r)
        }

        if (intent == null){
            return false
        }

        // 创建代理的ActivityRecord对象
        val parseOriginRecord = IntentHelper.parseIntent(intent)
        val activityInfo = parseOriginRecord.activityInfo
        if(activityInfo != null){
            // bind
            if (!AppActivityThread.isInit) {
                AppActivityThread.handleBindApplication(
                    activityInfo.packageName,
                    activityInfo.processName ?: activityInfo.packageName,
                    parseOriginRecord.userId
                )
                return true
            }
//            if (BuildCompat.isAtLeastS){
//                val launchingActivityRecord = ActivityThread.currentActivityThread().getLaunchingActivity(token)
//                HActivityThread.ActivityClientRecord.intent.set(launchingActivityRecord, parseOriginRecord.originIntent)
//                HActivityThread.ActivityClientRecord.activityInfo.set(launchingActivityRecord, activityInfo)
//                VmActivityThread.vmAppConfig?.vmPackageInfo?.apply {
//                    HActivityThread.ActivityClientRecord.packageInfo.set(launchingActivityRecord, this)
//                }
//            }

            if (BuildCompat.isAtLeastPie) {
                HLaunchActivityItem.mIntent.set(r, parseOriginRecord.originIntent)
                HLaunchActivityItem.mInfo.set(r, activityInfo)
            } else {
                val resources: Map<String, Any>? = HActivityThread.mPackages.get(ActivityThread.currentActivityThread())
                if (resources != null) {
                    val loadApkRef = resources[activityInfo.packageName] as WeakReference<*>?
                    if (loadApkRef?.get() != null) {
                        HActivityThread.ActivityClientRecord.packageInfo.set(r, loadApkRef.get())
                    }
                    HActivityThread.ActivityClientRecord.intent.set(r, parseOriginRecord.originIntent)
                    HActivityThread.ActivityClientRecord.activityInfo.set(r, activityInfo)
                }
            }
            val targetComponent = ComponentName(activityInfo.packageName, activityInfo.name)
            intent.component = targetComponent
        }
        return false
    }

    /**
     * @param clientTransaction ClientTransaction 对象
     * @return ClientTransactionItem 对象
     */
    private fun getLaunchActivityItem(clientTransaction: Any): Any? {
        val mActivityCallbacks: List<Any> = HClientTransaction.mActivityCallbacks.get(clientTransaction)
        for (obj in mActivityCallbacks) {
            // 判断LaunchActivityItem对象
            if (HLaunchActivityItem.REF.clazz.name.equals(obj.javaClass.canonicalName)) {
                return obj
            }
        }
        return null
    }


    fun initNewProcess(packageName: String, processName: String, userId: Int): VmAppConfig?{
        return try {
            requireService().initNewProcess(packageName, processName, userId)
        }catch (e: RemoteException){
            L.printStackTrace(e)
            null
        }
    }

    fun startService(intent: Intent?, resolvedType: String?,requireForeground: Boolean, userId: Int): ComponentName? {
        return try {
            requireService().startService(intent, resolvedType,requireForeground, userId)
        }catch (e: RemoteException){
            L.printStackTrace(e)
            null
        }
    }

    fun stopService(intent: Intent?, resolvedType: String?, userId: Int): Int{
        return try {
            requireService().stopService(intent, resolvedType, userId)
        }catch (e: RemoteException){
            L.printStackTrace(e)
            -1
        }
    }

    fun bindService(intent: Intent?, token: IBinder?, resolvedType: String?,connection: IServiceConnection?, userId: Int): Int{
        return try {
            requireService().bindService(AppActivityThread.getApplicationThread(), intent, token, connection, resolvedType, userId)
        }catch (e: RemoteException){
            L.printStackTrace(e)
            -1
        }
    }

    fun peekService(intent: Intent?, resolvedType: String?, userId: Int): IBinder?{
        return try {
            requireService().peekService(intent, resolvedType, userId)
        }catch (e: RemoteException){
            L.printStackTrace(e)
            null
        }
    }

    fun publishService(token: IBinder, intent: Intent?, binder: IBinder?){
        try {
            requireService().publishService(token, intent, binder)
        }catch (e: RemoteException){
            L.printStackTrace(e)
        }
    }

    @Synchronized
    private fun requireService(): IVmActivityManagrService {
        if (service == null || !service!!.asBinder().isBinderAlive){
            service = IVmActivityManagrService.Stub.asInterface(AppServiceManager.getService(VmServiceManager.ACTIVITY_MANAGER))
        }
        return service!!
    }

}