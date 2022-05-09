package com.virtual.box.core.server.am

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.helper.IntentHelper
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.manager.VmActivityStackManager
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest
import com.virtual.box.core.server.VmApplicationService
import com.virtual.box.core.server.pm.VmPackageManagerService

internal object VmActivityManagerService: IVmActivityManagrService.Stub() {
    private val logger = Logger.getLogger(L.SERVER_TAG, "VmActivityManagerService")
    /**
     * 虚拟程序启动入口
     */
    override fun launchActivity(intent: Intent, userId: Int) {
        logger.i("启动应用程序 intent = %s, userId = %s", intent, userId)
        val packageName = intent.getPackage()!!
        // 检查启动程序是否存在
        val findVmAppProcess = VmProcessManager.findVmAppProcess(packageName, userId)
        if (findVmAppProcess != null){
            logger.i("启动应用程序, 进程已经存在，讲进程移动至前台")
            // 进程已经存在,
            VmActivityStackManager.launchExistsTask(packageName, userId)
            return
        }
        val needLaunchActivityInfo = VmPackageManagerService.resolveApplicationInfo(intent, 0,  userId)
        if (needLaunchActivityInfo == null){
            logger.e("启动应用进程失败，解析到的ApplicationInfo == null")
            return
        }
        logger.i("启动应用程序, 进程不存在，启动新的进程")
        // 进程不存在，手动启动一个新的进程
        val startVmAppProcess = VmProcessManager.startVmAppProcess(needLaunchActivityInfo, userId)
        if (startVmAppProcess.checkMainProcess()){
            val vmPid = startVmAppProcess.mainProcessRecord!!.vmPid
            intent.putExtra("_VM_|_pid_", vmPid)
            // 进程启动成功, 开始启动Activity
            val shadowIntent = prepareStartActivity(intent, userId)
            startActivity(shadowIntent, userId)
            return
        }
    }
    /**
     * 启动一个Activity的准备
     * 解析需要替换的intent，讲intent进行占位替换
     */
    override fun prepareStartActivity(intent: Intent, userId: Int): Intent? {
        // 解析intent
        val resolveActivityInfo = VmPackageManagerService.resolveActivityInfo(intent, PackageManager.GET_ACTIVITIES, "", userId) ?: return intent
        val realLaunchMode = resolveActivityInfo.launchMode
        val taskAffinity = PackageHelper.getTaskAffinity(resolveActivityInfo)
        val vmPid = intent.getIntExtra("_VM_|_pid_", -1)
        val shadowIntent = Intent()
        if (vmPid != -1){
            // 说明是自身启动的
            shadowIntent.component = ComponentName(VirtualBox.get().hostContext, ProxyManifest.getProxyActivity(vmPid))
            IntentHelper.saveStubInfo(shadowIntent, intent, resolveActivityInfo, userId)
        }else{
            // 虚拟程序启动，获取上一个窗口的进程名称
//            VmActivityStackManager.findTaskRecordByTaskAffinityLocked()


        }
        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        shadowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shadowIntent.addFlags(realLaunchMode)

        // 查找窗口记录

        // 进程判断
        return shadowIntent
    }

    override fun startActivity(intent: Intent?, userId: Int): Int {
        intent ?: return -1
        val componentName = intent.component
        val packageName = intent.`package` ?: componentName?.packageName ?: return -1
        val installedPackageInfo = VmPackageManagerService.getVmInstalledPackageInfo(packageName, 0)
        if (installedPackageInfo == null){
            // 查询系统的
            return 1
        }


        // 替换Intent
        return  1
    }

    override fun startService(intent: Intent?, userId: Int): ComponentName {
        TODO("Not yet implemented")
    }
}