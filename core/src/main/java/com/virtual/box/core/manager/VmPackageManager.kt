package com.virtual.box.core.manager

import android.os.RemoteException
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.server.pm.IVmPackageManagerService
import com.virtual.box.core.server.pm.IVmPackageObserver
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.core.server.pm.entity.VmPackageResult

object VmPackageManager {
    private val logger = Logger.getLogger(L.HOST_TAG, "VmPackageManager")

    private var service: IVmPackageManagerService? = null

    private val packageMonitorList: MutableList<PackageMonitor> = mutableListOf()

    private val packageObserver: IVmPackageObserver = object : IVmPackageObserver.Stub(){

        override fun onPackageResult(installResult: VmPackageResult?) {
            if (installResult?.success == false){
                dispatcherPackageOperatorFail(installResult)
                return
            }
            when(installResult?.resultType){
                VmPackageResult.INSTALL_FLAG -> dispatcherPackageInstalled(installResult)
                VmPackageResult.UNINSTALL_FLAG -> dispatcherPackageUninstalled(installResult)
                VmPackageResult.UPDATE_FLAG -> dispatcherPackageUpgraded(installResult)
            }
        }
    }

    init {
        requirePackageService().registerPackageObserver(packageObserver)
    }


    fun installPackage(installOption: VmPackageInstallOption): VmPackageResult{
        return try {
            requirePackageService().installPackageAsUser(installOption, 0)
        }catch (e: RemoteException){
            logger.e(e)
            VmPackageResult.installFail("调用服务进程安装失败：${e.message}")
        }
    }

    fun uninstallPackage(packageName: String, userId: Int): VmPackageResult{
        return try {
            requirePackageService().uninstallPackageAsUser(packageName, userId)
        }catch (e: RemoteException){
            logger.e(e)
            VmPackageResult.installFail("调用服务进程卸载失败：${e.message}")
        }
    }

    fun getInstalledPackageInfo(flag: Int): List<VmInstalledPackageInfo>{
        return try {
            requirePackageService().getVmInstalledPackageInfo(flag)
        }catch (e: RemoteException){
            logger.e(e)
            emptyList<VmInstalledPackageInfo>()
        }
    }

    private fun dispatcherPackageInstalled(installResult: VmPackageResult){
        for (packageMonitor in packageMonitorList) {
            packageMonitor.onPackageInstalled(installResult)
        }
    }

    private fun dispatcherPackageUpgraded(installResult: VmPackageResult){
        for (packageMonitor in packageMonitorList) {
            packageMonitor.onPackageUpgraded(installResult)
        }
    }

    private fun dispatcherPackageUninstalled(installResult: VmPackageResult){
        for (packageMonitor in packageMonitorList) {
            packageMonitor.onPackageUninstalled(installResult)
        }
    }

    private fun dispatcherPackageOperatorFail(installResult: VmPackageResult){
        for (packageMonitor in packageMonitorList) {
            packageMonitor.onPackageOperatorFail(installResult)
        }
    }

    @Synchronized
    private fun requirePackageService(): IVmPackageManagerService {
        if (service == null || !service!!.asBinder().isBinderAlive){
            service = IVmPackageManagerService.Stub.asInterface(ServiceManager.getService(VmServiceManager.PACKAGE_MANAGER))
        }
        return service!!
    }

    interface PackageMonitor{

        fun onPackageInstalled(installResult: VmPackageResult)

        fun onPackageUpgraded(installResult: VmPackageResult){}

        fun onPackageUninstalled(installResult: VmPackageResult){}

        fun onPackageOperatorFail(installResult: VmPackageResult){}
    }

}