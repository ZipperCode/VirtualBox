package com.virtual.box.core.manager

import android.annotation.SuppressLint
import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo
import android.graphics.Compatibility
import android.os.*
import android.util.ArraySet
import android.webkit.WebView
import androidx.annotation.MainThread
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.app.IAppApplicationThread
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.helper.ContextHelper
import com.virtual.box.core.helper.IoHelper
import com.virtual.box.core.helper.ProviderHelper
import com.virtual.box.core.hook.delegate.ContentProviderStub
import com.virtual.box.core.server.am.IVmActivityThread
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.*
import com.virtual.box.reflect.android.content.HContentProviderClient
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import com.virtual.box.reflect.android.provider.HFontsContract
import com.virtual.box.reflect.android.util.HSingleton
import dalvik.system.PathClassLoader
import dalvik.system.VMRuntime
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 虚拟进程的ActivityThread模拟实现
 *
 */
@SuppressLint("StaticFieldLeak")
internal object AppActivityThread : IVmActivityThread.Stub() {

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
    var mVmLoadedApk: LoadedApk? = null

    /**
     * 当前运行程序的包名
     */
    @JvmField
    var mVmPackageName: String = ""

    /**
     * 当前虚拟用户id
     * 启动Activity的时候需要指定启动哪个用户下的程序
     */
    var currentProcessVmUserId: Int = 0

    val isInit: Boolean get() = vmAppConfig != null && vmApplication != null

    val currentProcessVmPid: Int get() = vmAppConfig?.mainProcessVmPid ?: 0

    private val initProcessLock = Any()

    private lateinit var mContext: Context

    private val handler = Handler(Looper.getMainLooper())

    private val applicationThread: ApplicationThread = ApplicationThread()

    private lateinit var systemServiceMapRef: MutableMap<IBinder, Service>

    var systemDebug = true

    fun initProcessAppConfig(vmAppConfig: VmAppConfig) {
        logger.d("initProcessAppConfig#vmAppConfig = %s", vmAppConfig)
        synchronized(initProcessLock) {
            if (this.vmAppConfig != null && this.vmAppConfig!!.packageName != vmAppConfig.packageName) {
                throw java.lang.RuntimeException(
                    "InitProcess fail: origin pks = ${this.vmAppConfig?.packageName}, target pks = ${vmAppConfig.packageName} " +
                            "origin process = ${this.vmAppConfig?.processName}, target process = ${vmAppConfig.processName}"
                )
            }
        }
        mContext = VirtualBox.get().hostContext
        this.vmAppConfig = vmAppConfig
        systemServiceMapRef = HActivityThread.mServices.get(ActivityThread.currentActivityThread())
        val binder = asBinder()
        try {
            binder.linkToDeath({
                logger.e("binder death pks = ${this.vmAppConfig?.packageName}")
            }, 0)
        } catch (e: RemoteException) {
            logger.e(e)
        }
    }

    fun getApplicationThread(): IAppApplicationThread{
        return applicationThread
    }

    override fun acquireContentProviderClient(providerInfo: ProviderInfo): IBinder? {
        if (!isInit){
            handleBindApplication(providerInfo.packageName, providerInfo.processName, currentProcessVmUserId)
        }
        val split = providerInfo.authority.split(";").toTypedArray()
        for (auth in split) {
            val contentProviderClient = mContext.contentResolver.acquireContentProviderClient(auth) ?: continue
            val iInterface: IInterface = HContentProviderClient.mContentProvider.get(contentProviderClient) ?: continue
            return iInterface.asBinder()
        }
        return null
    }


    /**
     * 处理插件的Application启动
     */
    override fun handleApplication() {
        if (isInit) {
            return
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val conditionVariable = ConditionVariable()
            handler.post {
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

    private fun handleCreateAppService(serviceInfo: ServiceInfo, token: IBinder, intent: Intent){
        if (!isInit){
            return
        }
        val serviceInstance = try {
            try {
                Class.forName(serviceInfo.name).newInstance()
            }catch (e: ClassNotFoundException){
                val classLoader = getClassLoader(serviceInfo.packageName)
                classLoader?.loadClass(serviceInfo.name)!!
            }
        }catch (e: Exception){
            logger.e(e)
        }
        if (serviceInstance == null){
            logger.e("unable newInstance service: %s", serviceInfo.name)
            return
        }

        val packageContext = createPackageContext(serviceInfo.applicationInfo)

        HContextImpl.mOuterContext.set(packageContext, serviceInstance)
        HService.attach.call(serviceInstance, packageContext, ActivityThread.currentActivityThread(),
            serviceInfo.name, token, vmApplication, getIActivityManager())

        (serviceInstance as Service).onCreate()
        val serviceMap = HActivityThread.mServices.get(ActivityThread.currentActivityThread())
        serviceMap?.put(token, serviceInstance)
        // TODO ams serviceDoneExecuting
    }

    fun handleAppServiceArgs(token: IBinder, startId: Int, intent: Intent?){
        val s = systemServiceMapRef[token]
        if (s != null){
            try {
                val res = s.onStartCommand(intent, 0, startId)
                // TODO serviceDoneExecuting res
            }catch (e: Exception){
                logger.e(e)
            }
        }
    }

    fun handleStopAppService(token: IBinder){
        val s = systemServiceMapRef.remove(token)
        if (s != null){
            try {
                s.onDestroy()
                HService.detachAndCleanUp.call(s)
                // TODO serviceDoneExecuting
            }catch (e:Exception){
                logger.e(e)
            }
        }
    }

    fun handleBindAppService(token: IBinder, intent: Intent?, rebind: Boolean){
        val s = systemServiceMapRef[token]
        if (s != null){
            try{
                if (!rebind){
                    val binder = s.onBind(intent)
                    AppActivityManager.publishService(token, intent, binder)
                }else{
                    s.onRebind(intent)
                    // TODO serviceDoneExecuting
                }
            }catch(e: Exception){
                logger.e(e)
            }
        }
    }

    fun handleUnbindAppService(token: IBinder, intent: Intent?){
        val s = systemServiceMapRef[token]
        if (s != null){
            try{
                val doRebind = s.onUnbind(intent)
                if (doRebind){
                    // TODO unbindFinished
                }else{
                    // TODO serviceDoneExecuting
                }
            }catch(e: Exception){
                logger.e(e)
            }
        }
    }

    @MainThread
    internal fun handleBindApplication(packageName: String, processName: String, userId: Int) {
        HActivityThread.setSystemDebug(systemDebug);
        logger.i("handleBindApplication > packageName = %s, processName = %s", packageName, processName)
        logger.i("handleBindApplication > vmAppConfig = %s", vmAppConfig)
        currentProcessVmUserId = vmAppConfig?.userId ?: 0
        val packageInfo = VmAppPackageManager.getPackageInfo(
            packageName, PackageManager.GET_ACTIVITIES
                    or PackageManager.GET_SERVICES
                    or PackageManager.GET_PROVIDERS
                    or PackageManager.GET_RECEIVERS,
            userId
        ) ?: return
        val applicationInfo = packageInfo.applicationInfo
        val providers = packageInfo.providers
        // Debug.waitForDebugger()
        val originBoundApplication = HActivityThread.mBoundApplication[ActivityThread.currentActivityThread()]
        // 创建虚拟应用包的Context
        val packageContext = createPackageContext(applicationInfo) ?: throw RuntimeException("创建虚拟程序包失败")
        val mainThread = ActivityThread.currentActivityThread()
        if (BuildCompat.isAtLeastR) {
            val resourcesManager = HContextImpl.mResourcesManager.get(packageContext)
            if (BuildCompat.isAtLeastS) {
                // 发现k30pro中的这个还是宿主的类目录
                val mApplicationOwnedApks = HResourcesManager.mApplicationOwnedApks.get(resourcesManager)
                val newApplicationOwnedApks = ArraySet<String>()
                newApplicationOwnedApks.add(applicationInfo.publicSourceDir)
                HResourcesManager.mApplicationOwnedApks.set(resourcesManager, newApplicationOwnedApks)
            }

            val outerContext = HContextImpl.mOuterContext.get(packageContext)
            ContextHelper.fixPackageName(outerContext, packageName)
        }
        val loadedApk = HContextImpl.mPackageInfo[packageContext]
        // 替换掉当前进程的宿主的信息
        HLoadedApk.mSecurityViolation[loadedApk] = false
        HLoadedApk.mApplicationInfo[loadedApk] = applicationInfo
        HLoadedApk.mDataDir[loadedApk] = applicationInfo.dataDir
        HLoadedApk.mDataDirFile[loadedApk] = File(applicationInfo.dataDir)
        if (BuildCompat.isAtLeastN){
            HLoadedApk.mDeviceProtectedDataDirFile[loadedApk] = File(HApplicationInfo.deviceProtectedDataDir.get(applicationInfo))
            HLoadedApk.mCredentialProtectedDataDirFile[loadedApk] = File(HApplicationInfo.credentialProtectedDataDir.get(applicationInfo))
        }

        if (BuildCompat.isAtLeastPie) {
            // 多进程webView
            WebView.setDataDirectorySuffix("$currentProcessVmUserId:$packageName:$processName")
        }

        VMRuntime.setProcessPackageName(loadedApk.packageName)
        // Pass data directory path to ART. This is used for caching information and
        // should be set before any application code is loaded.
        VMRuntime.setProcessDataDirectory(loadedApk.appDir)
        VMRuntime.getRuntime().targetSdkVersion = loadedApk.targetSdkVersion
        // Supply the targetSdkVersion to the UI rendering module, which may
        // need it in cases where it does not have access to the appInfo.
        if (BuildCompat.isAtLeastS) {
            Compatibility.setTargetSdkVersion(loadedApk.targetSdkVersion);
        }

        IoHelper.enableRedirect(packageContext, applicationInfo)
        // 替换掉ActivityThread.AppBindData的信息为插件的信息

        HActivityThread.AppBindData.instrumentationName[originBoundApplication] =
            ComponentName(applicationInfo.packageName, Instrumentation::class.java.name)
        HActivityThread.AppBindData.appInfo[originBoundApplication] = applicationInfo
        HActivityThread.AppBindData.info[originBoundApplication] = loadedApk
        HActivityThread.AppBindData.processName[originBoundApplication] = processName
        // HActivityThread.AppBindData.providers[originBoundApplication] = packageInfo.providers.toMutableList()

        var application: Application? = null
        try {
            application = HLoadedApk.makeApplication.call(loadedApk, false, null)
            HActivityThread.mInitialApplication.set(ActivityThread.currentActivityThread(), application)
        } catch (e: Throwable) {
            logger.e(e)
        }
        this.vmApplication = application
        AppHookManager.onBindApplicationHook()
        // Debug.waitForDebugger()
        // application生成后，需要处理插件应用中的ContentProvider，并且调用Application的onCreate方法
        if (this.vmApplication != null) {
            logger.d("插件Application初始化完成，获取插件ContentProvider进行安装")
            installProviders(providers, processName)
            packageContext.filesDir
            application!!.onCreate()
            if (BuildCompat.isAtLeastOreo) {
                HFontsContract.sContext.set(null, application.applicationContext)
            }
        }
        HActivityThread.mInitialApplication[mainThread] = this.vmApplication
        mVmPackageName = packageContext.packageName
        mVmLoadedApk = loadedApk


    }

    private fun installProviders(providers: Array<ProviderInfo>, processName: String) {
        ProviderHelper.cleanAndInitProvider()
        if (providers.isNotEmpty()) {

            providers.forEach {
                try {
                    ActivityThread.currentActivityThread().installSystemProviders(
                        Arrays.asList(it)
                    )
                }catch (e: Exception){
                    L.printStackTrace(e)
                }
            }
            val providerMap = HActivityThread.mProviderMap.get(ActivityThread.currentActivityThread())
            for (mutableEntry in providerMap) {
                val value = mutableEntry.value
                val recordName = HActivityThread.ProviderClientRecord.mNames.get(mutableEntry.value)
                if (recordName == null || recordName.isEmpty()) {
                    continue
                }
                val iProvider = HActivityThread.ProviderClientRecord.mProvider.get(value)
                if (iProvider == null || iProvider is java.lang.reflect.Proxy) {
                    continue
                }
                HActivityThread.ProviderClientRecord.mProvider.set(
                    value,
                    ContentProviderStub().wrapper(iProvider, VirtualBox.get().hostPkg)
                )
            }
        }
    }

    @MainThread
    private fun createPackageContext(info: ApplicationInfo): Context? {
        try {
            val context = VirtualBox.get().hostContext
            logger.method("info = %s", info)
            val vmContextImpl = context.createPackageContext(
                info.packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            val hostContextImpl = context.createPackageContext(
                context.packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
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

    fun getAppLoadedApk(): Any?{
        return getLoadedApk(mVmPackageName)
    }

    fun getLoadedApk(packageName: String): Any?{
        val resources = HActivityThread.mPackages[ActivityThread.currentActivityThread()]
        if (resources != null) {
            val loadApkRef = resources[packageName] as WeakReference<*>?
            if (loadApkRef?.get() != null) {
                return loadApkRef.get()
            }
        }
        return null
    }

    fun getClassLoader(packageName: String): ClassLoader?{
        var targetClassLoader: ClassLoader? = null
        val resources = HActivityThread.mPackages[ActivityThread.currentActivityThread()]
        if (resources != null) {
            val loadApkRef = resources[packageName] as WeakReference<*>?
            if (loadApkRef?.get() != null) {
                targetClassLoader = HLoadedApk.mClassLoader[loadApkRef.get()]
            }
        }
        return targetClassLoader
    }

    private fun getIActivityManager(): Any{
        val iActivityManager: Any? = if (BuildCompat.isAtLeastOreo) {
            HActivityManager.IActivityManagerSingleton.get()
        } else {
            HActivityManagerNative.gDefault.get()
        }
        return HSingleton.get.call(iActivityManager)
    }

    private class ApplicationThread: IAppApplicationThread.Stub() {

        override fun getVmAppConfig(): VmAppConfig? {
            return AppActivityThread.vmAppConfig
        }

        override fun attachApplication(appConfig: VmAppConfig){
            if (AppActivityThread.vmAppConfig == null){
                AppActivityThread.vmAppConfig = appConfig
            }
            handleApplication()
        }

        override fun scheduleCreateService(token: IBinder, serviceInfo: ServiceInfo, intent: Intent) {
            handler.post {
                handleCreateAppService(serviceInfo, token, intent)
            }
        }

        override fun scheduleServiceArgs(token: IBinder, startId: Int, intent: Intent?) {
            handler.post {
                handleAppServiceArgs(token, startId, intent)
            }
        }

        override fun schduleStopService(token: IBinder, intent: Intent) {
            handler.post {
                handleStopAppService(token)
            }
        }


        override fun scheduleBindService(token: IBinder, intent: Intent?, rebind: Boolean) {
            handler.post {
                handleBindAppService(token, intent, rebind)
            }
        }

        override fun scheduleUnbindService(token: IBinder, intent: Intent?) {
            handler.post {
                handleUnbindAppService(token, intent)
            }
        }

        override fun acquireContentProviderClient(providerInfo: ProviderInfo?): IBinder {
            TODO("Not yet implemented")
        }

        override fun finishActivity(token: IBinder?) {
            TODO("Not yet implemented")
        }


    }
}