package com.virtual.box.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.constant.ProcessType
import com.virtual.box.core.service.DaemonService
import com.virtual.box.reflect.android.app.HActivityThread
import me.weishu.reflection.Reflection

@SuppressLint("StaticFieldLeak")
class VirtualCore {

    private val logger = Logger.virtualLogger()

    lateinit var application: Application
        private set
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
    }

    private fun initService(){
        if (isServerProcess) {
            DaemonService.startService(hostContext)
        }
    }

    private fun initHook(){

    }

    fun installPackage(sysPackageName: String){

    }

    fun uninstallPackage(packageName: String){

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
        private val sVirtualCore = VirtualCore()

        @JvmStatic
        fun get(): VirtualCore {
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