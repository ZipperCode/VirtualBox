package com.virtual.box.core.manager

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.RemoteException
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.server.am.IVmActivityManagrService
import com.virtual.box.core.server.pm.IVmPackageManagerService
import com.virtual.box.core.server.pm.IVmPackageObserver
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.core.server.pm.entity.VmPackageResult

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

    @Synchronized
    private fun requireService(): IVmActivityManagrService {
        if (service == null || !service!!.asBinder().isBinderAlive){
            service = IVmActivityManagrService.Stub.asInterface(ServiceManager.getService(VmServiceManager.ACTIVITY_MANAGER))
        }
        return service!!
    }

}