package com.virtual.box.core.server.am

import android.app.IServiceConnection
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import android.os.IBinder
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.helper.IntentHelper
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.manager.VmActivityStackManager
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest
import com.virtual.box.core.server.pm.VmPackageManagerService

internal object VmActivityManagerService : IVmActivityManagrService.Stub() {
    private val logger = Logger.getLogger(L.SERVER_TAG, "VmActivityManagerService")

    private val vmActiveServices = VmActiveServices()

    /**
     * 虚拟程序启动入口
     */
    override fun launchActivity(intent: Intent, userId: Int) {
        logger.i("启动应用程序 intent = %s, userId = %s", intent, userId)
        // Debug.waitForDebugger()
        val packageName = intent.getPackage() ?: intent.component!!.packageName
        // 检查启动程序是否存在
        val findVmAppProcess = VmProcessManager.findAppProcessWithId(packageName, userId)
        if (findVmAppProcess != null) {
            logger.i("启动应用程序, 进程已经存在，讲进程移动至前台")
            // 进程已经存在,
            VmActivityStackManager.launchExistsTask(packageName, userId)
            return
        }
        val needLaunchActivityInfo = VmPackageManagerService.resolveApplicationInfo(intent, 0, userId)
        if (needLaunchActivityInfo == null) {
            logger.e("启动应用进程失败，解析到的ApplicationInfo == null")
            return
        }
        logger.i("启动应用程序, 进程不存在，启动新的进程")
        // 进程不存在，手动启动一个新的进程
        val startVmAppProcess = VmProcessManager.startVmAppProcess(needLaunchActivityInfo, userId)
        if (startVmAppProcess.checkMainProcess()) {
            val vmPid = startVmAppProcess.mainProcessRecord!!.vmPid
            val vmProcessName = startVmAppProcess.mainProcessRecord!!.processName
            intent.putExtra("_VM_|_pid_", vmPid)
            intent.putExtra("_VM_|_process_name_", vmProcessName)
            // 进程启动成功, 开始启动Activity
            startActivity(intent, userId)
            return
        }
    }

    /**
     * 启动一个Activity的准备
     * 解析需要替换的intent，讲intent进行占位替换
     */
    override fun prepareStartActivity(intent: Intent, userId: Int): Intent? {
        // 解析intent
        val resolveActivityInfo =
            VmPackageManagerService.resolveActivityInfo(intent, PackageManager.GET_ACTIVITIES, "", userId) ?: return intent
        val realLaunchMode = resolveActivityInfo.launchMode
        val taskAffinity = PackageHelper.getTaskAffinity(resolveActivityInfo)
        val vmPid = intent.getIntExtra("_VM_|_pid_", -1)
        val vmProcessName = intent.getStringExtra("_VM_|_process_name_")
        val intentPackageName = intent.getPackage() ?: intent.component!!.packageName
        val shadowIntent = Intent()
        if (vmPid != -1) {
            // 说明是自身启动的
            shadowIntent.component = ComponentName(VirtualBox.get().hostContext, ProxyManifest.getProxyActivity(vmPid))
        } else {
            // 虚拟程序启动，获取上一个窗口的进程名称
            val needStartActivityProcessName = resolveActivityInfo.processName ?: resolveActivityInfo.packageName
            val needStartActivityPackage = resolveActivityInfo.packageName
            if (needStartActivityPackage == intentPackageName) {
                var appProcess = VmProcessManager.findAppProcessWithId(intentPackageName, userId)
                if (appProcess == null) {
                    appProcess = VmProcessManager.startVmAppProcess(resolveActivityInfo.applicationInfo, userId)
                }
                val originProcessName = appProcess.processName
                val originVmPid = appProcess.vmPid
                // 启动来源窗口包名如果和下一个要启动的包名相同
                if (needStartActivityProcessName != originProcessName) {
                    // 如果某个进程与当前需要启动Activity的进程不一样，那么要启动的进程作为子进程
                    val vmAppProcess = VmProcessManager.findAppProcessWithId(needStartActivityPackage, userId)!!
                    // 启动一个子进程
                    val vmSubPid = VmProcessManager.startVmSubProcess(resolveActivityInfo.applicationInfo, vmAppProcess)
                    if (vmSubPid == -1) {
                        throw RuntimeException("创建子进程失败, 启动子进程vmPid == -1")
                    }
                    shadowIntent.component = ComponentName(VirtualBox.get().hostContext, ProxyManifest.getProxyActivity(vmSubPid))
                } else {
                    shadowIntent.component = ComponentName(VirtualBox.get().hostContext, ProxyManifest.getProxyActivity(originVmPid))
                }
            } else {
                // 要启动的包名不同
                val vmAppProcessList = VmProcessManager.findAppProcess(needStartActivityPackage)
                if (vmAppProcessList.isEmpty()) {
                    // 不存在已经启动的进程
                    val vmAppProcess = VmProcessManager.startVmAppProcess(resolveActivityInfo.applicationInfo, userId)
                    if (vmAppProcess.checkMainProcess()) {
                        throw RuntimeException("启动新的App进程失败")
                    }
                    shadowIntent.component = ComponentName(
                        VirtualBox.get().hostContext,
                        ProxyManifest.getProxyActivity(vmAppProcess.mainProcessRecord!!.vmPid)
                    )
                } else {
                    // TODO 这边的策略先取第一个
                    val vmProcessRecord = vmAppProcessList.find { it.vmPid != -1 } ?: throw RuntimeException("启动存在的App进程失败")
                    shadowIntent.component = ComponentName(
                        VirtualBox.get().hostContext,
                        ProxyManifest.getProxyActivity(vmProcessRecord.vmPid)
                    )
                }
            }
        }
        IntentHelper.saveStubInfo(shadowIntent, intent, resolveActivityInfo, userId)
//        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
//        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
//        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shadowIntent.addFlags(realLaunchMode)
        return shadowIntent
    }

    override fun startActivity(intent: Intent?, userId: Int): Int {
        if (intent == null) {
            logger.e("StartActivity 失败，Intent == null")
            return -1
        }
        val componentName = intent.component
        val packageName = intent.`package` ?: componentName?.packageName
        if (packageName.isNullOrEmpty()) {
            logger.e("StartActivity 失败，Intent 中不存在包名")
            return -1
        }

        val installedPackageInfo = VmPackageManagerService.getPackageInfo(packageName, 0, userId)
        if (installedPackageInfo == null) {
            logger.e("StartActivity 失败，获取的的PackageInfo == null")
            // 查询系统的
            return -1
        }
        val shadowIntent = prepareStartActivity(intent, userId)
        shadowIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        VirtualBox.get().hostContext.startActivity(shadowIntent)
        return 1
    }

    override fun initNewProcess(packageName: String, processName: String, userId: Int): VmAppConfig {
        val findAppProcess = VmProcessManager.findAppProcessWithId(packageName, userId)
        if (findAppProcess != null) {
            val vmAppConfig = findAppProcess.getVmProcessAppConfig(processName)
            if (vmAppConfig != null) {
                return vmAppConfig
            }
            // 主进程存在，要启动的进程名称不存在
            val applicationInfo = VmPackageManagerService.getApplicationInfo(packageName, PackageManager.GET_META_DATA, userId)
                ?: throw RuntimeException("启动进程失败，解析到的ApplicationInfo == null, packageName = $packageName")
            val vmSubPid = VmProcessManager.startVmSubProcess(applicationInfo, findAppProcess)
            if (vmSubPid == -1) {
                throw RuntimeException("启动子进程失败，processName = $processName")
            }
            return findAppProcess.getVmProcessAppConfig(processName)!!
        }
        val applicationInfo = VmPackageManagerService.getApplicationInfo(packageName, PackageManager.GET_META_DATA, userId)
            ?: throw RuntimeException("启动进程失败，解析到的ApplicationInfo == null, packageName = $packageName")
        val appProcess = VmProcessManager.startVmAppProcess(applicationInfo, userId)
        return appProcess.getVmAppProcessAppConfig()
    }

    override fun startService(intent: Intent?, resolvedType: String?, requireForeground: Boolean, userId: Int): ComponentName? {
        return vmActiveServices.startServiceLock(intent, resolvedType, requireForeground, userId)
    }

    override fun stopService(intent: Intent?, resolvedType: String?, userId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun stopServiceToken(componentName: ComponentName?, token: IBinder?, userId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun prepareBindService(
        intent: Intent?,
        token: IBinder?,
        resulvedType: String?,
        connection: IServiceConnection?,
        flags: Int,
        userId: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun unbindService(connection: IServiceConnection?, userId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun peekService(intent: Intent?, resolvedType: String?, userId: Int): IBinder {
        TODO("Not yet implemented")
    }

    override fun sendBroadcast(intent: Intent?, resolvedType: String?, userId: Int): Intent {
        TODO("Not yet implemented")
    }

    override fun getRunningAppProcesses(callingPackage: String?, userId: Int): ParceledListSlice<*> {
        TODO("Not yet implemented")
    }

    override fun getServices(callingPackage: String?, userId: Int): ParceledListSlice<*> {
        TODO("Not yet implemented")
    }

    override fun getCallingPackage(token: IBinder?, userId: Int): String {
        TODO("Not yet implemented")
    }
}