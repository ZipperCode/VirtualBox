package com.virtual.box.core.manager

import android.os.Build
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.BuildConfig
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.IInjectHook
import com.virtual.box.core.hook.annotation.IHook
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.core.hook.delegate.AppInstrumentation
import com.virtual.box.core.hook.delegate.VmHandlerCallback
import com.virtual.box.core.hook.libcore.LibCoreOsHookHandle
import com.virtual.box.core.hook.service.ActivityManagerHookHandle
import com.virtual.box.core.hook.service.ActivityTaskManagerHookHandle
import com.virtual.test.NativeLib

/**
 * hook管理程序，同一处理需要hook的服务等
 */
internal object HookManager {
    const val TAG = "HookManager"
    private val logger = Logger.virtualLogger()

    fun initHook() {
        VmCore.init(Build.VERSION.SDK_INT, BuildConfig.DEBUG)
        L.vd("[%s] >> 初始化hook", TAG)
        val list = mutableListOf<IInjectHook>(
            LibCoreOsHookHandle(),
            VmHandlerCallback(),
            AppInstrumentation(),
            ActivityManagerHookHandle(),
        )
        if (BuildCompat.isAtLeastPie) {
            list.add(ActivityTaskManagerHookHandle())
        }
        list.forEach {
            it.initHook()
        }
//        // hook 系统文件重定向
//        addInjector(OsStub())
//        // hook ActivityManager 服务
//        addInjector(IActivityManagerProxy())
//        // hook 包管理服务
//        addInjector(IPackageManagerProxy())
//        // hook 电话管理服务
//        addInjector(ITelephonyManagerProxy())
//        // hook Handler
//        addInjector(HCallbackProxy())
//        // hook ops服务
//        addInjector(IAppOpsManagerProxy())
//        // hook 闹钟服务
//        addInjector(IAlarmManagerProxy())
//        // hook 存储管理服务
//        addInjector(IStorageManagerProxy())
//        // hook 启动服务
//        addInjector(ILauncherAppsProxy())
//        // hook 任务调度服务
//        addInjector(IJobServiceProxy())
//        addInjector(ITelephonyRegistryProxy())
//        // hook Instrumentation
//        addInjector(AppInstrumentation.get())
//
//        addInjector(IConnectivityManagerProxy())
//        // 10.0
//        if (isQ) {
//            addInjector(IActivityTaskManagerProxy())
//        }
//        // 8.0
//        if (isOreo) {
//            addInjector(IDeviceIdentifiersPolicyProxy())
//        }
////        addInjector(ApplicationThreadProxy())
//        injectAll()
    }

    fun nativeHook() {
        logger.e(">> 第一次Hook之后加载So库Native方法")
        NativeLib.kotlinDynamicRegister()
//        VmCore.nativeHook()
//        logger.e(">> 第二次Hook之后调用Native方法")
//        NativeLib.kotlinStaticRegister()
    }

}