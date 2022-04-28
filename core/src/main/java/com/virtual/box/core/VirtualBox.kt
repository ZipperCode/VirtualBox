package com.virtual.box.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.constant.ProcessType
import com.virtual.box.core.hook.JavaTestHandle
import com.virtual.box.core.hook.TestHookHandle
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.core.hook.method.ArtMethod
import com.virtual.box.core.manager.HookManager
import com.virtual.box.core.manager.ServiceManager
import com.virtual.box.core.manager.VmPackageManager
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.core.service.DaemonService
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.java.lang.reflect.HExecutable
import me.weishu.reflection.Reflection

@SuppressLint("StaticFieldLeak")
class VirtualBox {

    private val logger = Logger.virtualLogger()

    /**
     * 宿主的上下文
     */
    lateinit var hostContext: Context
        private set

    lateinit var hostPkg: String

    fun doAttachAppBaseContext(context: Context){
        hostContext = context
        hostPkg = hostContext.packageName
        // 去除反射限制
        Reflection.unseal(context)
        logger.method("context = %s", context)
        val processName = SystemHelper.getProcessName(context)

        mProcessType = when {
            processName == hostPkg -> ProcessType.Main
            processName.endsWith(context.getString(R.string.server_process_name)) -> ProcessType.Server
            else -> ProcessType.VmClient
        }

        initService()
        if (isVirtualProcess){
            VmCore.init(Build.VERSION.SDK_INT, true)
        }
    }

    private fun initService(){
        if (isServerProcess) {
            DaemonService.startService(hostContext)
        }
    }

    private fun initHook(){

    }

    fun installPackage(installOption: VmPackageInstallOption){
        VmPackageManager.installPackage(installOption)
    }

    fun uninstallPackage(packageName: String){
        VmPackageManager.uninstallPackage(packageName, 0)
    }

    fun getInstalledPackageInfo(): List<VmInstalledPackageInfo>{
        return VmPackageManager.getInstalledPackageInfo(0)
    }

    /**
     * 当前进程类型
     */
    private var mProcessType: ProcessType? = null

    val isVirtualProcess: Boolean
        get() = mProcessType == ProcessType.VmClient
    val isMainProcess: Boolean
        get() = mProcessType == ProcessType.Main
    val isServerProcess: Boolean
        get() = mProcessType == ProcessType.Server

    companion object{

        /**
         * 单例
         */
        private val sVirtualCore = VirtualBox()

        @JvmStatic
        fun get(): VirtualBox {
            return sVirtualCore
        }

        /**
         * 当前进程的 ActivityThread
         */
        @JvmStatic
        fun mainThread(): Any {
            return HActivityThread.currentActivityThread.call()
        }
    }
}