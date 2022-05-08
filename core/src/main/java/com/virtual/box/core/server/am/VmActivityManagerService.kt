package com.virtual.box.core.server.am

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.server.pm.VmPackageManagerService

internal object VmActivityManagerService: IVmActivityManagrService.Stub() {
    /**
     * 启动一个Activity的准备
     * 解析需要替换的intent，讲intent进行占位替换
     */
    override fun prepareStartActivity(intent: Intent?, userId: Int): Intent? {
        // 解析intent
        val resolveActivityInfo = VmPackageManagerService.resolveActivityInfo(intent, PackageManager.GET_ACTIVITIES, "", userId) ?: return intent
        // 查找进程记录

        // 查找窗口记录

        // 进程判断
        return Intent()
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