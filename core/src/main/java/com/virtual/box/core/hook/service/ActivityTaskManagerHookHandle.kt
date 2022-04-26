package com.virtual.box.core.hook.service

import android.annotation.TargetApi
import android.os.Build
import com.virtual.box.reflect.android.app.HIActivityTaskManager
import com.virtual.box.reflect.android.os.HServiceManager
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.os.Bundle
import com.virtual.box.core.hook.core.MethodHandle

/**
 * @author zipper
 */
@TargetApi(Build.VERSION_CODES.P)
@Suppress("UNUSED")
class ActivityTaskManagerHookHandle : BaseBinderHookHandle("activity_task") {
    override fun initTargetObj(): Any? {
        return HIActivityTaskManager.Stub.asInterface.call(proxyBinderObj)
    }

    override fun isSupport(): Boolean {
        return HServiceManager.getService.call("activity_task") !== this
    }

    fun startActivity(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int,
        flags: Int, profilerInfo: Any?, options: Bundle?
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startActivities(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intents: Array<Intent?>?, resolvedTypes: Array<String?>?,
        resultTo: IBinder?, options: Bundle?, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startActivityAsUser(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int, flags: Int,
        profilerInfo: Any?, options: Bundle?, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startNextMatchingActivity(
        methodHandle: MethodHandle, callingActivity: IBinder?,
        intent: Intent?, options: Bundle?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun startDreamActivity(methodHandle: MethodHandle, intent: Intent?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     *
     * @param caller IApplicationThread
     * @param target IIntentSender
     */
    fun startActivityIntentSender(
        methodHandle: MethodHandle, caller: Any?,
        target: Any?, whitelistToken: IBinder?, fillInIntent: Intent?,
        resolvedType: String?, resultTo: IBinder?, resultWho: String?, requestCode: Int,
        flagsMask: Int, flagsValues: Int, options: Bundle?
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    /**
     *
     * @param caller IApplicationThread
     * @return WaitResult
     */
    fun startActivityAndWait(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int, flags: Int,
        profilerInfo: Any?, options: Bundle?, userId: Int
    ): Any? {
        return methodHandle.invokeOriginMethod()
    }

    fun startActivityWithConfig(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int, startFlags: Int,
        newConfig: Configuration?, options: Bundle?, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    /**
     *
     * @param session IVoiceInteractionSession
     * @param interactor IVoiceInteractor
     */
    fun startVoiceActivity(
        methodHandle: MethodHandle, callingPackage: String?, callingFeatureId: String?, callingPid: Int,
        callingUid: Int, intent: Intent?, resolvedType: String?,
        session: Any?, interactor: Any?, flags: Int,
        profilerInfo: Any?, options: Bundle?, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startAssistantActivity(
        methodHandle: MethodHandle, callingPackage: String?, callingFeatureId: String?, callingPid: Int,
        callingUid: Int, intent: Intent?, resolvedType: String?, options: Bundle?, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    /**
     *
     * @param recentsAnimationRunner IRecentsAnimationRunner
     */
    fun startRecentsActivity(
        methodHandle: MethodHandle, intent: Intent?, eventTime: Long,
        recentsAnimationRunner: Any?
    ) {
        methodHandle.invokeOriginMethod()
    }

    fun startActivityFromRecents(methodHandle: MethodHandle, taskId: Int, options: Bundle?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startActivityAsCaller(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        intent: Intent?, resolvedType: String?, resultTo: IBinder?, resultWho: String?,
        requestCode: Int, flags: Int, profilerInfo: Any?, options: Bundle?,
        permissionToken: IBinder?, ignoreTargetSecurity: Boolean, userId: Int
    ): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun isActivityStartAllowedOnDisplay(
        methodHandle: MethodHandle, displayId: Int, intent: Intent?, resolvedType: String?,
        userId: Int
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     *
     * @param app IApplicationThread
     */
    fun moveTaskToFront(
        methodHandle: MethodHandle, app: Any?, callingPackage: String?, task: Int,
        flags: Int, options: Bundle?
    ) {
        methodHandle.invokeOriginMethod()
    }

    /**
     * @return List<IBinder>
    </IBinder> */
    fun getAppTasks(methodHandle: MethodHandle, callingPackage: String?): Any? {
        return methodHandle.invokeOriginMethod()
    }

    /**
     * @param receiver IAssistDataReceiver
     */
    fun requestAssistDataForTask(
        methodHandle: MethodHandle, receiver: Any?, taskId: Int,
        callingPackageName: String?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * @param adapter RemoteAnimationAdapter
     */
    fun registerRemoteAnimationForNextActivityStart(
        methodHandle: MethodHandle, packageName: String?,
        adapter: Any?
    ) {
        methodHandle.invokeOriginMethod()
    }

    fun getPackageScreenCompatMode(methodHandle: MethodHandle, packageName: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    fun setPackageScreenCompatMode(methodHandle: MethodHandle, packageName: String?, mode: Int) {
        methodHandle.invokeOriginMethod()
    }

    fun getPackageAskScreenCompat(methodHandle: MethodHandle, packageName: String?): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    fun setPackageAskScreenCompat(methodHandle: MethodHandle, packageName: String?, ask: Boolean) {
        methodHandle.invokeOriginMethod()
    }
}