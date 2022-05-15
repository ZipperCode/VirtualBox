package com.virtual.box.core.manager

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.Application
import android.app.Instrumentation
import android.app.LoadedApk
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.os.*
import android.util.ArraySet
import androidx.annotation.MainThread
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.BuildConfig
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.helper.ContextHelper
import com.virtual.box.core.helper.IoHelper
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.core.server.am.IVmActivityThread
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HContextImpl
import com.virtual.box.reflect.android.app.HLoadedApk
import com.virtual.box.reflect.android.app.HResourcesManager
import dalvik.system.PathClassLoader
import java.lang.RuntimeException

/**
 * 虚拟进程的ActivityThread模拟实现
 *
 */
internal object VmActivityThread : IVmActivityThread.Stub() {

    private val logger = Logger.getLogger(L.SERVER_TAG, "VmApplicationManager")
    /**
     * 虚拟化app的Application，不是当前进程的Application
     */
    var vmApplication: Application? = null
        private set

    var vmAppConfig: VmAppConfig? = null
        private set

    /**
     * 当前的LoadedApk对象，不是宿主的LoadedApk对象
     */
    var mVmLoadedApk:LoadedApk? = null

    /**
     * 当前运行程序的包名
     */
    var mVmPackageName: String = ""

    /**
     * 当前虚拟用户id
     * 启动Activity的时候需要指定启动哪个用户下的程序
     */
    var currentProcessVmUserId: Int = 0

    val isInit: Boolean get() = vmAppConfig != null && vmApplication != null

    fun initProcessAppConfig(vmAppConfig: VmAppConfig){
        this.vmAppConfig = vmAppConfig
    }

    override fun getVmActivityThread(): IBinder {
        TODO("Not yet implemented")
    }

    /**
     * 处理插件的Application启动
     */
    override fun handleApplication() {
        if (!isInit) {
            return
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val conditionVariable = ConditionVariable()
            Handler(Looper.getMainLooper()).post {
                vmAppConfig?.run {
                    handleBindApplication(packageName, processName, userId)
                }
                conditionVariable.open()
            }
            conditionVariable.block()
        } else {
            vmAppConfig?.run {
                handleBindApplication(packageName, processName, userId)
            }
        }
    }

    @MainThread
    internal fun handleBindApplication(packageName: String, processName: String, userId: Int) {
        currentProcessVmUserId = vmAppConfig!!.userId
        val packageInfo = VmPackageManager.getPackageInfo(
            packageName, PackageManager.GET_ACTIVITIES
                    or PackageManager.GET_SERVICES
                    or PackageManager.GET_PROVIDERS
                    or PackageManager.GET_RECEIVERS,
            userId
        ) ?: return
        val applicationInfo = packageInfo.applicationInfo
        val providers = packageInfo.providers
        val originBoundApplication = HActivityThread.mBoundApplication[ActivityThread.currentActivityThread()]
        // 创建虚拟应用包的Context
        val packageContext = createPackageContext(applicationInfo) ?: throw RuntimeException("创建虚拟程序包失败")
        val mainThread = ActivityThread.currentActivityThread()
        if (BuildCompat.isAtLeastR){
            val resourcesManager = HContextImpl.mResourcesManager.get(packageContext)
            if(BuildCompat.isAtLeastS){
                // 发现k30pro中的这个还是宿主的类目录
                val mApplicationOwnedApks = HResourcesManager.mApplicationOwnedApks.get(resourcesManager)
                val newApplicationOwnedApks = ArraySet<String>()
                newApplicationOwnedApks.add(applicationInfo.publicSourceDir)
                HResourcesManager.mApplicationOwnedApks.set(resourcesManager,newApplicationOwnedApks)
            }

            val outerContext = HContextImpl.mOuterContext.get(packageContext)
            ContextHelper.fixPackageName(outerContext, packageName)
        }
        val loadedApk = HContextImpl.mPackageInfo[packageContext]
        // 替换掉当前进程的宿主的信息
        HLoadedApk.mSecurityViolation[loadedApk] = false
        HLoadedApk.mApplicationInfo[loadedApk] = applicationInfo

        // 计算函数偏移并hook native函数
        VmCore.init(Build.VERSION.SDK_INT, BuildConfig.DEBUG)
        IoHelper.enableRedirect(packageContext, applicationInfo)

        // 替换掉ActivityThread.AppBindData的信息为插件的信息

        HActivityThread.AppBindData.instrumentationName[originBoundApplication] =
            ComponentName(applicationInfo.packageName, Instrumentation::class.java.name)
        HActivityThread.AppBindData.appInfo[originBoundApplication] = applicationInfo
        HActivityThread.AppBindData.info[originBoundApplication] = loadedApk
        HActivityThread.AppBindData.processName[originBoundApplication] = processName
        HActivityThread.AppBindData.providers[originBoundApplication] = packageInfo.providers

        var application: Application? = null
        try {
            application = HLoadedApk.makeApplication.call(loadedApk, false, null)
        } catch (e: Throwable) {
            logger.e(e)
        }
        this.vmApplication = application

        // application生成后，需要处理插件应用中的ContentProvider，并且调用Application的onCreate方法
        if (this.vmApplication != null) {
            logger.d("插件Application初始化完成，获取插件ContentProvider进行安装")
            HActivityThread.installSystemProviders.call(mainThread, providers)
            application!!.onCreate()
        }
        HActivityThread.mInitialApplication[mainThread] = this.vmApplication
        mVmPackageName = packageContext.packageName
        mVmLoadedApk = loadedApk
    }

    @MainThread
    private fun createPackageContext(info: ApplicationInfo): Context? {
        try {
            val context = VirtualBox.get().hostContext
            logger.method("info = %s", info)
            val vmContextImpl = context.createPackageContext(info.packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            val hostContextImpl = context.createPackageContext(context.packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            try {
                // 修复类加载器, 看了下源码，createPackageContext 没看到有创建类加载器的地方，所以vmContextImpl。loadedApk中
                // 没有相应的类加载器
                val loadedApk = HContextImpl.mPackageInfo[vmContextImpl]
                val loader = PathClassLoader(info.sourceDir, info.nativeLibraryDir, null)
                HLoadedApk.mClassLoader[loadedApk] = loader
                HLoadedApk.mBaseClassLoader[loadedApk] = loader
                HLoadedApk.mDefaultClassLoader[loadedApk] = loader
                val hostLoadedApk = HContextImpl.mPackageInfo[hostContextImpl]
                // 对比了LoadedApk中的类加载器，都是相同的对象，所以这边直接用mDefaultClassLoader
                // 根据双亲委派机制，将插件类加载器作为当前使用加载器的父类
                // 因为，如果插件加载器没有附加到宿主的加载器上，一些通用的类，会优先使用宿主的，导致一些View会出现问题
                val mDefaultClassLoader = HLoadedApk.mDefaultClassLoader[hostLoadedApk]
                val hostParent = mDefaultClassLoader.parent
                // 宿主的类加载父加载器换成插件
                MirrorReflection.on(ClassLoader::class.java).field<Any>("parent")[loader] = hostParent
                MirrorReflection.on(ClassLoader::class.java).field<Any>("parent")[mDefaultClassLoader] = loader
            } catch (e: Exception) {
                logger.e(e)
            }
            return vmContextImpl
        } catch (e: Exception) {
            logger.e(e)
        }
        return null
    }


}