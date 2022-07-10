package com.virtual.box.core.hook.service

import android.app.ActivityThread
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.*
import android.graphics.Bitmap
import android.os.*
import androidx.annotation.RequiresApi
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.core.manager.AppActivityThread
import com.virtual.box.core.manager.VmAppPackageManager
import com.virtual.box.core.server.user.BUserHandle
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HContextImpl

/**
 * IPackageManager Hook
 */
@Suppress("UNUSED")
class PackageManagerHookHandle : BaseBinderHookHandle("package") {

    private val logger = Logger.getLogger(L.HOOK_TAG, "PackageManagerHookHandle")

    override fun getOriginObject(): Any? {
        return HActivityThread.sPackageManager.get(ActivityThread.currentActivityThread())
    }

    override fun hookInject(target: Any, proxy: Any) {
        super.hookInject(target, proxy)
        HActivityThread.sPackageManager.set(proxy)
        val systemContext = HActivityThread.getSystemContext.call(ActivityThread.currentActivityThread())
        val packageManager = HContextImpl.mPackageManager.get(systemContext)
        packageManager?.apply {
            MirrorReflection.on("android.app.ApplicationPackageManager")
                .field<Any>("mPM")
                .set(packageManager, proxyInvocation)
        }
    }

    /**
     * 检查包启动表
     */
    fun checkPackageStartable(methodHandle: MethodHandle, packageName: String?, userId: Int) {
        val replacePackageName = packageName
        methodHandle.invokeOriginMethod(arrayOf(replacePackageName, userId))
    }

    /**
     * 包是否可用
     */
    fun isPackageAvailable(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        if (VmAppPackageManager.isInstalled(packageName, userId)) {
            return true
        }
        return methodHandle.invokeOriginMethod(arrayOf(packageName, userId)) as Boolean
    }

    fun getPackageInfo(methodHandle: MethodHandle, packageName: String, flags: Int, userId: Int): PackageInfo? {
        val packageInfo = VmAppPackageManager.getPackageInfo(packageName, flags, AppActivityThread.currentProcessVmUserId)
        if (packageInfo != null) {
            return packageInfo
        }
        return methodHandle.invokeOriginMethod() as? PackageInfo
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPackageInfoVersioned(
        methodHandle: MethodHandle,
        versionedPackage: VersionedPackage?,
        flags: Int, userId: Int
    ): PackageInfo? {
        val packageInfo = VmAppPackageManager.getPackageInfo(versionedPackage!!.packageName, flags, AppActivityThread.currentProcessVmUserId)
        if (packageInfo != null) {
            return packageInfo
        }
        return methodHandle.invokeOriginMethod() as? PackageInfo
    }

    fun getPackageUid(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): Int {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, flags, userId)) as Int
    }

    fun getPackageGids(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): IntArray? {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, flags, userId)) as IntArray
    }

    fun getApplicationInfo(methodHandle: MethodHandle, packageName: String, flags: Int, userId: Int): ApplicationInfo? {
        if (packageName != hostPkg){
            logger.i("getApplication#packageName = %s", packageName)
            val applicationInfo = VmAppPackageManager.getApplicationInfo(packageName, flags, AppActivityThread.currentProcessVmUserId)
            logger.i("getApplication#applicationInfo = %s", applicationInfo)
            if (applicationInfo != null) {
                return applicationInfo
            }
        }
        return methodHandle.invokeOriginMethod() as? ApplicationInfo
    }

    /**
     * @return the target SDK for the given package name, or -1 if it cannot be retrieved
     */
    fun getTargetSdkVersion(methodHandle: MethodHandle, packageName: String): Int {
        val applicationInfo = VmAppPackageManager.getApplicationInfo(packageName, 0, AppActivityThread.currentProcessVmUserId)
        if (applicationInfo != null) {
            return applicationInfo.targetSdkVersion
        }
        return methodHandle.invokeOriginMethod() as Int
    }

    fun getActivityInfo(methodHandle: MethodHandle, className: ComponentName, flags: Int, userId: Int): ActivityInfo? {
        val activityInfo = VmAppPackageManager.getActivityInfo(className, flags,AppActivityThread.currentProcessVmUserId)
        if (activityInfo != null) {
            return activityInfo
        }
        return methodHandle.invokeOriginMethod() as? ActivityInfo
    }

    fun activitySupportsIntent(
        methodHandle: MethodHandle,
        className: ComponentName?, intent: Intent?,
        resolvedType: String?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getReceiverInfo(methodHandle: MethodHandle, className: ComponentName?, flags: Int, userId: Int): ActivityInfo? {
        val receiverInfo = VmAppPackageManager.getReceiverInfo(className, flags, AppActivityThread.currentProcessVmUserId)
        if (receiverInfo != null) {
            return receiverInfo
        }
        return methodHandle.invokeOriginMethod() as? ActivityInfo
    }

    fun getServiceInfo(methodHandle: MethodHandle, className: ComponentName?, flags: Int, userId: Int): ServiceInfo? {
        val serviceInfo = VmAppPackageManager.getServiceInfo(className, flags, AppActivityThread.currentProcessVmUserId)
        if (serviceInfo != null) {
            return serviceInfo
        }
        return methodHandle.invokeOriginMethod() as? ServiceInfo
    }

    fun getProviderInfo(methodHandle: MethodHandle, className: ComponentName?, flags: Int, userId: Int): ProviderInfo? {
        val providerInfo = VmAppPackageManager.getProviderInfo(className, flags, AppActivityThread.currentProcessVmUserId)
        if (providerInfo != null) {
            return providerInfo
        }
        return methodHandle.invokeOriginMethod() as? ProviderInfo
    }

    /**
     * check PackageSetting1.getSigningDetails == PackageSetting2.getSigningDetails
     */
    fun checkSignatures(methodHandle: MethodHandle, pkg1: String?, pkg2: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun checkUidSignatures(methodHandle: MethodHandle, uid1: Int, uid2: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun resolveIntent(methodHandle: MethodHandle, intent: Intent?, resolvedType: String?, flags: Int, userId: Int): ResolveInfo? {
        val resolveIntent = VmAppPackageManager.resolveIntent(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (resolveIntent != null) {
            return resolveIntent
        }
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun findPersistentPreferredActivity(methodHandle: MethodHandle, intent: Intent?, userId: Int): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun queryIntentActivities(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        val queryIntentActivities = VmAppPackageManager.queryIntentActivities(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (queryIntentActivities.list.isNotEmpty()) {
            return queryIntentActivities
        }
        return methodHandle.invokeOriginMethod() as ParceledListSlice<Parcelable>?
    }

    fun queryIntentActivityOptions(
        methodHandle: MethodHandle,
        caller: ComponentName?, specifics: Array<Intent>?,
        specificTypes: Array<String>?, intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        val queryIntentActivityOptions =
            VmAppPackageManager.queryIntentActivityOptions(caller, specifics, specificTypes, intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (queryIntentActivityOptions.list.isNotEmpty()) {
            return queryIntentActivityOptions
        }
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun queryIntentReceivers(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        val queryIntentReceivers = VmAppPackageManager.queryIntentReceivers(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (queryIntentReceivers.list.isNotEmpty()) {
            return queryIntentReceivers
        }
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun resolveService(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ResolveInfo? {
        val resolveService = VmAppPackageManager.resolveService(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (resolveService != null) {
            return resolveService
        }
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun queryIntentServices(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        val queryIntentServices = VmAppPackageManager.queryIntentServices(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (queryIntentServices.list.isNotEmpty()) {
            return queryIntentServices
        }
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun queryIntentContentProviders(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        val queryIntentContentProviders = VmAppPackageManager.queryIntentContentProviders(intent, resolvedType, flags, AppActivityThread.currentProcessVmUserId)
        if (queryIntentContentProviders.list.isNotEmpty()) {
            return queryIntentContentProviders
        }
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun getInstalledPackages(methodHandle: MethodHandle, flags: Int, userId: Int): ParceledListSlice<*>? {
        // TODO
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun getLastChosenActivity(
        methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int
    ): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    /**
     * This implements getPackagesHoldingPermissions via a "last returned row"
     * mechanism that is not exposed the API. This is to get around the IPC
     * limit that kicks when flags are included that bloat up the data
     * returned.
     */
    fun getPackagesHoldingPermissions(
        methodHandle: MethodHandle, permissions: Array<String?>?,
        flags: Int, userId: Int
    ): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    /**
     * This implements getInstalledApplications via a "last returned row"
     * mechanism that is not exposed the API. This is to get around the IPC
     * limit that kicks when flags are included that bloat up the data
     * returned.
     */
    fun getInstalledApplications(methodHandle: MethodHandle, flags: Int, userId: Int): ParceledListSlice<*>? {
        // TODO include virtual
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    /**
     * Retrieve all applications that are marked as persistent.
     *
     * @return A List&lt{
     * return methodHandle.invokeOriginMethod();
     * }applicationInfo> containing one entry for each persistent
     * application.
     */
    fun getPersistentApplications(methodHandle: MethodHandle, flags: Int): ParceledListSlice<*>? {
        // TODO include virtual
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun resolveContentProvider(methodHandle: MethodHandle, name: String?, flags: Int, userId: Int): ProviderInfo? {
        val resolveContentProvider = VmAppPackageManager.resolveContentProvider(name, flags, AppActivityThread.currentProcessVmUserId)
        if (resolveContentProvider != null) {
            return resolveContentProvider
        }
        // TODO virtual
        return methodHandle.invokeOriginMethod() as ProviderInfo?
    }

    /**
     * Retrieve sync information for all content providers.
     *
     * @param outNames Filled with a list of the root names of the content
     * providers that can sync.
     * @param outInfo Filled with a list of the ProviderInfo for each
     * name 'outNames'.
     */
    fun querySyncProviders(
        methodHandle: MethodHandle, outNames: List<String?>?,
        outInfo: List<ProviderInfo?>?
    ) {
        // TODO include virtual
        methodHandle.invokeOriginMethod()
    }

    fun queryContentProviders(
        methodHandle: MethodHandle,
        processName: String?, uid: Int, flags: Int, metaDataKey: String?
    ): ParceledListSlice<*>? {
        val queryContentProviders = VmAppPackageManager.queryContentProviders(processName, uid, flags, metaDataKey)
        if (queryContentProviders.list.isNotEmpty()) {
            return queryContentProviders
        }
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun getInstrumentationInfo(
        methodHandle: MethodHandle,
        className: ComponentName?, flags: Int
    ): InstrumentationInfo? {
        if (className?.packageName == hostPkg) {
            return methodHandle.invokeOriginMethod() as InstrumentationInfo?
        }
        BUserHandle.getUserId(AppActivityThread.currentProcessVmPid)
        val instrumentationInfo = VmAppPackageManager.getInstrumentationInfo(className, flags, AppActivityThread.currentProcessVmUserId)
        if (instrumentationInfo != null) {
            return instrumentationInfo
        }
        return methodHandle.invokeOriginMethod() as InstrumentationInfo?
    }

    fun queryInstrumentation(
        methodHandle: MethodHandle,
        targetPackage: String?, flags: Int
    ): ParceledListSlice<*>? {
        if (targetPackage == hostPkg) {
            return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
        }
        val queryInstrumentation = VmAppPackageManager.queryInstrumentation(targetPackage, flags, AppActivityThread.currentProcessVmUserId)
        if (queryInstrumentation != null) {
            return queryInstrumentation;
        }
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun finishPackageInstall(methodHandle: MethodHandle, token: Int, didLaunch: Boolean) {
        methodHandle.invokeOriginMethod()
    }

    fun setInstallerPackageName(methodHandle: MethodHandle, targetPackage: String?, installerPackageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun setApplicationCategoryHint(methodHandle: MethodHandle, packageName: String?, categoryHint: Int, callerPackageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    /** @param observer IPackageDeleteObserver
     */
    @Deprecated(
        """rawr, don't call AIDL methods directly! 
      """
    )
    fun deletePackageAsUser(
        methodHandle: MethodHandle, packageName: String?, versionCode: Int,
        observer: Any?, userId: Int, flags: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * PMS没啥特殊测操作
     */
    fun getInstallerPackageName(methodHandle: MethodHandle, packageName: String?): String? {
        return methodHandle.invokeOriginMethod() as String?
    }

    /**
     * PMS解析intent返回对应的Activity数量
     */
    fun getPreferredActivities(
        methodHandle: MethodHandle, outFilters: List<IntentFilter?>?,
        outActivities: List<ComponentName?>?, packageName: String?
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun addPersistentPreferredActivity(methodHandle: MethodHandle, filter: IntentFilter?, activity: ComponentName?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    fun clearPackagePersistentPreferredActivities(methodHandle: MethodHandle, packageName: String?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    fun addCrossProfileIntentFilter(
        methodHandle: MethodHandle, intentFilter: IntentFilter?, ownerPackage: String?,
        sourceUserId: Int, targetUserId: Int, flags: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    fun clearCrossProfileIntentFilters(methodHandle: MethodHandle, sourceUserId: Int, ownerPackage: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun setDistractingPackageRestrictionsAsUser(
        methodHandle: MethodHandle, packageNames: Array<String?>?, restrictionFlags: Int,
        userId: Int
    ): Array<String?>? {
        return methodHandle.invokeOriginMethod() as Array<String?>?
    }

    fun setPackagesSuspendedAsUser(
        methodHandle: MethodHandle, packageNames: Array<String?>?, suspended: Boolean,
        appExtras: PersistableBundle?, launcherExtras: PersistableBundle?,
        dialogInfo: Any?, callingPackage: String?, userId: Int
    ): Array<String?>? {
        return methodHandle.invokeOriginMethod() as Array<String?>?
    }

    fun getUnsuspendablePackagesForUser(methodHandle: MethodHandle, packageNames: Array<String?>?, userId: Int): Array<String?>? {
        return methodHandle.invokeOriginMethod() as Array<String?>?
    }

    fun isPackageSuspendedForUser(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getSuspendedPackageAppExtras(methodHandle: MethodHandle, packageName: String?, userId: Int): Bundle? {
        return methodHandle.invokeOriginMethod() as Bundle?
    }

    /**
     * Report the set of 'Home' activity candidates, plus (MethodHandle methodHandle, if any) which of them
     * is the current "always use this one" setting.
     */
    fun getHomeActivities(methodHandle: MethodHandle, outHomeCandidates: List<ResolveInfo?>?): ComponentName? {
        return methodHandle.invokeOriginMethod() as ComponentName?
    }

    fun setHomeActivity(methodHandle: MethodHandle, className: ComponentName?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Overrides the label and icon of the component specified by the component name. The component
     * must belong to the calling app.
     *
     * These changes will be reset on the next boot and whenever the package is updated.
     *
     * Only the app defined as com.android.internal.R.config_overrideComponentUiPackage is allowed
     * to call this.
     *
     * @param componentName The component name to override the label/icon of.
     * @param nonLocalizedLabel The label to be displayed.
     * @param icon The icon to be displayed.
     * @param userId The user id.
     */
    fun overrideLabelAndIcon(
        methodHandle: MethodHandle, componentName: ComponentName?, nonLocalizedLabel: String?,
        icon: Int, userId: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Restores the label and icon of the activity specified by the component name if either has
     * been overridden. The component must belong to the calling app.
     *
     * Only the app defined as com.android.internal.R.config_overrideComponentUiPackage is allowed
     * to call this.
     *
     * @param componentName The component name.
     * @param userId The user id.
     */
    fun restoreLabelAndIcon(methodHandle: MethodHandle, componentName: ComponentName?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * As per [android.content.pm.PackageManager.setComponentEnabledSetting].
     */
    fun setComponentEnabledSetting(
        methodHandle: MethodHandle, componentName: ComponentName?,
        newState: Int, flags: Int, userId: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * As per [android.content.pm.PackageManager.getComponentEnabledSetting].
     */
    fun getComponentEnabledSetting(methodHandle: MethodHandle, componentName: ComponentName?, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    /**
     * As per [android.content.pm.PackageManager.setApplicationEnabledSetting].
     */
    fun setApplicationEnabledSetting(
        methodHandle: MethodHandle, packageName: String?, newState: Int, flags: Int,
        userId: Int, callingPackage: String?
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * As per [android.content.pm.PackageManager.getApplicationEnabledSetting].
     */
    fun getApplicationEnabledSetting(methodHandle: MethodHandle, packageName: String?, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    /**
     * Logs process start information (MethodHandle methodHandle, including APK hash) to the security log.
     */
    fun logAppProcessStartIfNeeded(
        methodHandle: MethodHandle,
        packageName: String?,
        processName: String?,
        uid: Int,
        seinfo: String?,
        apkFile: String?,
        pid: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Set whether the given package should be considered stopped, making
     * it not visible to implicit intents that filter stopped packages.
     */
    fun setPackageStoppedState(methodHandle: MethodHandle, packageName: String?, stopped: Boolean, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Delete all the cache files an applications cache directory
     * @param packageName The package name of the application whose cache
     * files need to be deleted
     * @param observer a callback used to notify when the deletion is finished.
     */
    fun deleteApplicationCacheFiles(methodHandle: MethodHandle, packageName: String?, observer: IPackageDataObserver?) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Delete all the cache files an applications cache directory
     * @param packageName The package name of the application whose cache
     * files need to be deleted
     * @param userId the user to delete application cache for
     * @param observer a callback used to notify when the deletion is finished.
     */
    fun deleteApplicationCacheFilesAsUser(methodHandle: MethodHandle, packageName: String?, userId: Int, observer: IPackageDataObserver?) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Clear the user data directory of an application.
     * @param packageName The package name of the application whose cache
     * files need to be deleted
     * @param observer a callback used to notify when the operation is completed.
     */
    fun clearApplicationUserData(methodHandle: MethodHandle, packageName: String?, observer: IPackageDataObserver?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Clear the profile data of an application.
     * @param packageName The package name of the application whose profile data
     * need to be deleted
     */
    fun clearApplicationProfileData(methodHandle: MethodHandle, packageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Get package statistics including the code, data and cache size for
     * an already installed package
     * @param packageName The package name of the application
     * @param userHandle Which user the size should be retrieved for
     * @param observer IPackageStatsObserver a callback to use to notify when the asynchronous
     * retrieval of information is complete.
     */
    fun getPackageSizeInfo(methodHandle: MethodHandle, packageName: String?, userHandle: Int, observer: Any?) {
        methodHandle.invokeOriginMethod()
    }


    /**
     * Notify the package manager that a package is going to be used and why.
     *
     * See PackageManager.NOTIFY_PACKAGE_USE_* for reasons.
     */
    fun notifyPackageUse(methodHandle: MethodHandle, packageName: String?, reason: Int) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Notify the package manager that a list of dex files have been loaded.
     *
     * @param loadingPackageName the name of the package who performs the load
     * @param classLoaderContextMap a map from file paths to dex files that have been loaded to
     * the class loader context that was used to load them.
     * @param loaderIsa the ISA of the loader process
     */
    fun notifyDexLoad(
        methodHandle: MethodHandle, loadingPackageName: String?,
        classLoaderContextMap: Map<String?, String?>?, loaderIsa: String?
    ) {
        methodHandle.invokeOriginMethod(arrayOf(
            hostPkg, classLoaderContextMap, loaderIsa
        ))
    }

    /**
     * Register an application dex module with the package manager.
     * The package manager will keep track of the given module for future optimizations.
     *
     * Dex module optimizations will disable the classpath checking at runtime. The client bares
     * the responsibility to ensure that the static assumptions on classes the optimized code
     * hold at runtime (MethodHandle methodHandle, e.g. there's no duplicate classes the classpath).
     *
     * Note that the package manager already keeps track of dex modules loaded with
     * [dalvik.system.DexClassLoader] and [dalvik.system.PathClassLoader].
     * This can be called for an eager registration.
     *
     * The call might take a while and the results will be posted on the mathread, using
     * the given callback.
     *
     * If the module is intended to be shared with other apps, make sure that the file
     * permissions allow for it.
     * If at registration time the permissions allow for others to read it, the module would
     * be marked as a shared module which might undergo a different optimization strategy.
     * (MethodHandle methodHandle, usually shared modules will generated larger optimizations artifacts,
     * taking more disk space).
     *
     * @param packageName the package name to which the dex module belongs
     * @param dexModulePath the absolute path of the dex module.
     * @param isSharedModule whether or not the module is intended to be used by other apps.
     * @param callback if not null,
     * [android.content.pm.IDexModuleRegisterCallback.IDexModuleRegisterCallback.onDexModuleRegistered]
     * will be called once the registration finishes.
     */
    fun registerDexModule(
        methodHandle: MethodHandle, packageName: String?, dexModulePath: String?,
        isSharedModule: Boolean, callback: Any?
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Ask the package manager to perform a dex-opt with the given compiler filter.
     *
     * Note: exposed only for the shell command to allow moving packages explicitly to a
     * definite state.
     */
    fun performDexOptMode(
        methodHandle: MethodHandle, packageName: String?, checkProfiles: Boolean,
        targetCompilerFilter: String?, force: Boolean, bootComplete: Boolean, splitName: String?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * Ask the package manager to perform a dex-opt with the given compiler filter on the
     * secondary dex files belonging to the given package.
     *
     * Note: exposed only for the shell command to allow moving packages explicitly to a
     * definite state.
     */
    fun performDexOptSecondary(
        methodHandle: MethodHandle, packageName: String?,
        targetCompilerFilter: String?, force: Boolean
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * Ask the package manager to dump profiles associated with a package.
     */
    fun dumpProfiles(methodHandle: MethodHandle, packageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun forceDexOpt(methodHandle: MethodHandle, packageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * Reconcile the information we have abthe secondary dex files belonging to
     * `packagName` and the actual dex files. For all dex files that were
     * deleted, update the internal records and delete the generated oat files.
     */
    fun reconcileSecondaryDexFiles(methodHandle: MethodHandle, packageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun movePackage(methodHandle: MethodHandle, packageName: String?, volumeUuid: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun installExistingPackageAsUser(
        methodHandle: MethodHandle, packageName: String?, userId: Int, installFlags: Int,
        installReason: Int, whiteListedPermissions: List<String?>?
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }


    @Deprecated("")
    fun getIntentVerificationStatus(methodHandle: MethodHandle, packageName: String?, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    @Deprecated("")
    fun updateIntentVerificationStatus(methodHandle: MethodHandle, packageName: String?, status: Int, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    @Deprecated("")
    fun getIntentFilterVerifications(methodHandle: MethodHandle, packageName: String?): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun getAllIntentFilters(methodHandle: MethodHandle, packageName: String?): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun setApplicationHiddenSettingAsUser(methodHandle: MethodHandle, packageName: String?, hidden: Boolean, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getApplicationHiddenSettingAsUser(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun setSystemAppHiddenUntilInstalled(methodHandle: MethodHandle, packageName: String?, hidden: Boolean) {
        methodHandle.invokeOriginMethod()
    }

    fun setSystemAppInstallState(methodHandle: MethodHandle, packageName: String?, installed: Boolean, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun setBlockUninstallForUser(methodHandle: MethodHandle, packageName: String?, blockUninstall: Boolean, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

//    fun getBlockUninstallForUser(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
//        try {
//            return methodHandle.invokeOriginMethod() as Boolean
//        }catch (e: RemoteException){
//            logger.e(e)
//            return false
//        }
//    }

    fun getKeySetByAlias(methodHandle: MethodHandle, packageName: String?, alias: String?): Any? {
        return methodHandle.invokeOriginMethod()
    }

    fun getSigningKeySet(methodHandle: MethodHandle, packageName: String?): Any? {
        return methodHandle.invokeOriginMethod()
    }

    fun isPackageSignedByKeySet(methodHandle: MethodHandle, packageName: String?, ks: Any?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun isPackageSignedByKeySetExactly(methodHandle: MethodHandle, packageName: String?, ks: Any?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getInstantAppCookie(methodHandle: MethodHandle, packageName: String?, userId: Int): ByteArray? {
        return methodHandle.invokeOriginMethod() as ByteArray?
    }

    fun setInstantAppCookie(methodHandle: MethodHandle, packageName: String?, cookie: ByteArray?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getInstantAppIcon(methodHandle: MethodHandle, packageName: String?, userId: Int): Bitmap? {
        return methodHandle.invokeOriginMethod() as Bitmap?
    }

    fun isInstantApp(methodHandle: MethodHandle?, packageName: String?, userId: Int): Boolean {
        return false
    }

    fun setRequiredForSystemUser(methodHandle: MethodHandle, packageName: String?, systemUserApp: Boolean): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * Sets whether or not an update is available. Ostensibly for instant apps
     * to force exteranl resolution.
     */
    fun setUpdateAvailable(methodHandle: MethodHandle, packageName: String?, updateAvaialble: Boolean) {
        methodHandle.invokeOriginMethod()
    }

    fun isPackageDeviceAdminOnAnyUser(methodHandle: MethodHandle, packageName: String?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getInstallReason(methodHandle: MethodHandle, packageName: String?, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun getSharedLibraries(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun getDeclaredSharedLibraries(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod() as ParceledListSlice<*>?
    }

    fun canRequestPackageInstalls(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getInstantAppAndroidId(methodHandle: MethodHandle, packageName: String?, userId: Int): String? {
        return methodHandle.invokeOriginMethod() as String?
    }

    fun setHarmfulAppWarning(methodHandle: MethodHandle, packageName: String?, warning: CharSequence?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    fun getHarmfulAppWarning(methodHandle: MethodHandle, packageName: String?, userId: Int): CharSequence? {
        return methodHandle.invokeOriginMethod() as CharSequence?
    }

    fun hasSigningCertificate(methodHandle: MethodHandle, packageName: String?, signingCertificate: ByteArray?, flags: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun isPackageStateProtected(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getModuleInfo(methodHandle: MethodHandle, packageName: String?, flags: Int): ModuleInfo? {
        return methodHandle.invokeOriginMethod() as ModuleInfo?
    }

    fun requestChecksums(
        methodHandle: MethodHandle,
        packageName: String?,
        includeSplits: Boolean,
        optional: Int,
        required: Int,
        trustedInstallers: List<*>?,
        onChecksumsReadyListener: Any?,
        userId: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    fun checkPermission(methodHandle: MethodHandle, permName: String?, pkgName: String?, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun grantRuntimePermission(methodHandle: MethodHandle, packageName: String?, permissionName: String?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    //------------------------------------------------------------------------
    // We need to keep these IPackageManager for convenience splitting
    // the permission manager. This should be cleaned up, but, will require
    // a large change that modifies many repos.
    //------------------------------------------------------------------------
    fun checkUidPermission(methodHandle: MethodHandle, permName: String?, uid: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun setMimeGroup(methodHandle: MethodHandle, packageName: String?, group: String?, mimeTypes: List<String?>?): Any? {
        return methodHandle.invokeOriginMethod()
    }

    fun getSplashScreenTheme(methodHandle: MethodHandle, packageName: String?, userId: Int): String? {
        return methodHandle.invokeOriginMethod() as String?
    }

    fun setSplashScreenTheme(methodHandle: MethodHandle, packageName: String?, themeName: String?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

    fun getMimeGroup(methodHandle: MethodHandle, packageName: String?, group: String?): List<String?>? {
        return methodHandle.invokeOriginMethod() as List<String?>?
    }

    fun isAutoRevokeWhitelisted(methodHandle: MethodHandle, packageName: String?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun getProperty(methodHandle: MethodHandle, propertyName: String?, packageName: String?, className: String?): Any? {
        return methodHandle.invokeOriginMethod()
    }

    fun getArtManager(methodHandle: MethodHandle): Any? {
        logger.i("getArtManager before")
        try {
            return methodHandle.invokeOriginMethod()
        } catch (e: Throwable) {
            logger.e(e)
            return null
        }finally {
            logger.i("getArtManager after")
        }
    }
}