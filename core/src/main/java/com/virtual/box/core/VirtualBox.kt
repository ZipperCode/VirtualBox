package com.virtual.box.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.constant.ProcessType
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.core.manager.VmPackageManager
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.core.service.DaemonService
import com.virtual.box.reflect.android.app.HActivityThread
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

    lateinit var hostPm: PackageManager

    lateinit var mainAThread: Any

    /**
     * 当前的LoadedApk对象，不是宿主的LoadedApk对象
     */
    lateinit var mVmLoadedApk:Any

    /**
     * 当前运行程序的包名
     */
    lateinit var mVmPackageName: String

    /**
     * 当前虚拟用户id
     * 启动Activity的时候需要指定启动哪个用户下的程序
     */
    var currentProcessVmUserId: Int = 0

    fun setVmLoadedApkAndPks(pks: String, loadedApk: Any){
        if (!::mVmPackageName.isInitialized){
            mVmPackageName = pks
        }
        if (!::mVmLoadedApk.isInitialized){
            mVmLoadedApk = loadedApk
        }
    }

    fun doAttachAppBaseContext(context: Context){
        hostContext = context
        hostPkg = hostContext.packageName
        hostPm = context.packageManager
        mainAThread = HActivityThread.currentActivityThread.call()
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
        return VmPackageManager.getInstalledPackageInfoList(0)
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