package com.virtual.box.core.manager

import android.app.ActivityThread
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Debug
import android.os.RemoteException
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.helper.IntentHelper
import com.virtual.box.core.server.am.IVmActivityManagrService
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.servertransaction.HClientTransaction
import com.virtual.box.reflect.android.app.servertransaction.HLaunchActivityItem
import java.lang.ref.WeakReference

object VmActivityManager {
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

        val intent: Intent  = if (BuildCompat.isAtLeastPie) {
            HLaunchActivityItem.mIntent.get(r)
        } else {
            HActivityThread.ActivityClientRecord.intent.get(r)
        } ?: return false

        // 创建代理的ActivityRecord对象
        val parseOriginRecord = IntentHelper.parseIntent(intent)
        val activityInfo = parseOriginRecord.activityInfo ?: return false
        // bind
        if (!VmActivityThread.isInit) {
            VmActivityThread.handleBindApplication(
                activityInfo.packageName,
                activityInfo.processName ?: activityInfo.packageName,
                parseOriginRecord.userId
            )
        }
        if (BuildCompat.isAtLeastPie) {
            HLaunchActivityItem.mInfo.set(r, activityInfo)
        } else {
            val resources: Map<String, Any>? = HActivityThread.mPackages.get(ActivityThread.currentActivityThread())
            if (resources != null) {
                val loadApkRef = resources[activityInfo.packageName] as WeakReference<*>?
                if (loadApkRef?.get() != null) {
                    HActivityThread.ActivityClientRecord.packageInfo.set(r, loadApkRef.get())
                }
            }
        }
        val targetComponent = ComponentName(activityInfo.packageName, activityInfo.name)
        intent.component = targetComponent
        return true
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

    @Synchronized
    private fun requireService(): IVmActivityManagrService {
        if (service == null || !service!!.asBinder().isBinderAlive){
            service = IVmActivityManagrService.Stub.asInterface(ServiceManager.getService(VmServiceManager.ACTIVITY_MANAGER))
        }
        return service!!
    }

}