package com.virtual.box.core.hook.service

import android.app.ActivityThread
import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import android.os.Parcelable
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HContextImpl
import com.virtual.box.reflect.android.os.HServiceManager

/**
 * IPackageManager Hook
 */
@Suppress("UNUSED")
class PackageManagerHookHandle : BaseBinderHookHandle("package") {
    override fun getOriginObject(): Any? {
        return HActivityThread.sPackageManager.get(ActivityThread.currentActivityThread())
    }

    override fun isHooked(): Boolean {
        return HServiceManager.getService.call(serviceName) != this
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

    fun checkPackageStartable(methodHandle: MethodHandle, packageName: String?, userId: Int) {
        val replacePackageName = packageName
        methodHandle.invokeOriginMethod(arrayOf(replacePackageName, userId))
    }

    fun isPackageAvailable(methodHandle: MethodHandle, packageName: String?, userId: Int): Boolean {
        val replacePackageName = packageName
        return methodHandle.invokeOriginMethod(arrayOf(replacePackageName, userId)) as Boolean
    }

    fun getPackageInfo(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): PackageInfo? {
        val replacePackageName = packageName
        return methodHandle.invokeOriginMethod(arrayOf(replacePackageName, flags, userId)) as? PackageInfo
    }

    fun getPackageInfoVersioned(
        methodHandle: MethodHandle,
        versionedPackage: VersionedPackage?,
        flags: Int, userId: Int
    ): PackageInfo? {
        return methodHandle.invokeOriginMethod() as? PackageInfo
    }

    fun getPackageUid(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun getPackageGids(methodHandle: MethodHandle, packageName: String?, flags: Int, userId: Int): IntArray? {
        return methodHandle.invokeOriginMethod() as IntArray
    }

    fun getApplicationInfo(methodHandle: MethodHandle,packageName: String?, flags: Int, userId: Int): ApplicationInfo? {
        return methodHandle.invokeOriginMethod() as? ApplicationInfo
    }

    /**
     * @return the target SDK for the given package name, or -1 if it cannot be retrieved
     */
    fun getTargetSdkVersion(methodHandle: MethodHandle, packageName: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun getActivityInfo(methodHandle: MethodHandle, className: ComponentName?, flags: Int, userId: Int): ActivityInfo? {
        return methodHandle.invokeOriginMethod() as? ActivityInfo
    }

    fun activitySupportsIntent(methodHandle: MethodHandle,
        className: ComponentName?, intent: Intent?,
        resolvedType: String?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun getReceiverInfo(methodHandle: MethodHandle,className: ComponentName?, flags: Int, userId: Int): ActivityInfo? {
        return methodHandle.invokeOriginMethod() as? ActivityInfo
    }

    fun getServiceInfo(methodHandle: MethodHandle,className: ComponentName?, flags: Int, userId: Int): ServiceInfo? {
        return methodHandle.invokeOriginMethod() as? ServiceInfo
    }

    fun getProviderInfo(methodHandle: MethodHandle,className: ComponentName?, flags: Int, userId: Int): ProviderInfo? {
        return methodHandle.invokeOriginMethod() as? ProviderInfo
    }

    fun checkSignatures(methodHandle: MethodHandle,pkg1: String?, pkg2: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun checkUidSignatures(methodHandle: MethodHandle,uid1: Int, uid2: Int): Int {
        return methodHandle.invokeOriginMethod() as Int
    }


    fun resolveIntent(methodHandle: MethodHandle,intent: Intent?, resolvedType: String?, flags: Int, userId: Int): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun findPersistentPreferredActivity(methodHandle: MethodHandle,intent: Intent?, userId: Int): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun queryIntentActivities(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun queryIntentActivityOptions(methodHandle: MethodHandle,
        caller: ComponentName?, specifics: Array<Intent?>?,
        specificTypes: Array<String?>?, intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun queryIntentReceivers(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun resolveService(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun queryIntentServices(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun queryIntentContentProviders(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int, userId: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun resolveContentProvider(methodHandle: MethodHandle,name: String?, flags: Int, userId: Int): ProviderInfo? {
        return methodHandle.invokeOriginMethod() as? ProviderInfo
    }

    fun getInstrumentationInfo(methodHandle: MethodHandle,
        className: ComponentName?, flags: Int
    ): InstrumentationInfo? {
        return methodHandle.invokeOriginMethod() as? InstrumentationInfo
    }
    fun queryInstrumentation(methodHandle: MethodHandle,
        targetPackage: String?, flags: Int
    ): ParceledListSlice<Parcelable>? {
        return methodHandle.invokeOriginMethod() as? ParceledListSlice<Parcelable>
    }

    fun setInstallerPackageName(methodHandle: MethodHandle,targetPackage: String?, installerPackageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun setApplicationCategoryHint(methodHandle: MethodHandle,packageName: String?, categoryHint: Int, callerPackageName: String?) {
        methodHandle.invokeOriginMethod()
    }

    fun getInstallerPackageName(methodHandle: MethodHandle,packageName: String?): String? {
        return methodHandle.invokeOriginMethod() as? String
    }

    fun getLastChosenActivity(methodHandle: MethodHandle,
        intent: Intent?,
        resolvedType: String?, flags: Int
    ): ResolveInfo? {
        return methodHandle.invokeOriginMethod() as? ResolveInfo
    }

    fun overrideLabelAndIcon(methodHandle: MethodHandle,
        componentName: ComponentName?, nonLocalizedLabel: String?,
        icon: Int, userId: Int
    ) {
        methodHandle.invokeOriginMethod()
    }

    fun restoreLabelAndIcon(methodHandle: MethodHandle,componentName: ComponentName?, userId: Int) {
        methodHandle.invokeOriginMethod()
    }

}