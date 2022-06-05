package com.virtual.box.core.hook.service

import android.annotation.TargetApi
import android.os.Build
import com.virtual.box.reflect.android.app.HIActivityTaskManager
import com.virtual.box.reflect.android.os.HServiceManager
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.os.Bundle
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.reflect.android.app.HActivityTaskManager
import com.virtual.box.reflect.android.util.HSingleton
import java.lang.reflect.Proxy

/**
 * @author zipper
 */
@TargetApi(Build.VERSION_CODES.P)
@Suppress("UNUSED")
class ActivityTaskManagerHookHandle : BaseBinderHookHandle("activity_task") {

    private val logger: Logger = Logger.getLogger(L.HOOK_TAG, "ActivityTaskManagerHookHandle")

    override fun getOriginObject(): Any? {
        return HIActivityTaskManager.Stub.asInterface.call(originBinder)
    }

    override fun isHooked(): Boolean {
        return HServiceManager.getService.call("activity_task") !== this
    }

    override fun hookInject(target: Any, proxy: Any) {
        super.hookInject(target, proxy)
        val singleInstance = HActivityTaskManager.IActivityTaskManagerSingleton.get()
        if (target !is Proxy){
            HSingleton.mInstance.set(singleInstance, proxy)
        }
    }

    fun startActivity(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int,
        flags: Int, profilerInfo: Any?, options: Bundle?
    ): Int {
        logger.d("startActivity#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, callingFeatureId, intent, resolvedType, resultTo, resultWho,
            requestCode, flags, profilerInfo, options
        )) as Int
    }

    fun startActivities(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intents: Array<Intent?>?, resolvedTypes: Array<String?>?,
        resultTo: IBinder?, options: Bundle?, userId: Int
    ): Int {
        logger.d("startActivities#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, callingFeatureId, intents, resolvedTypes, resultTo, options, userId
        )) as Int
    }

    fun startActivityAsUser(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int, flags: Int,
        profilerInfo: Any?, options: Bundle?, userId: Int
    ): Int {
        logger.d("startActivityAsUser#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, callingFeatureId, intent, resolvedType,
            resultTo, resultWho, requestCode, flags,
            profilerInfo, options, userId
        )) as Int
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
        logger.d("startActivityIntentSender")
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
        logger.d("startActivityAndWait#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, callingFeatureId, intent, resolvedType,
            resultTo, resultWho, requestCode, flags,
            profilerInfo, options, userId
        ))
    }

    fun startActivityWithConfig(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        callingFeatureId: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?, requestCode: Int, startFlags: Int,
        newConfig: Configuration?, options: Bundle?, userId: Int
    ): Int {
        logger.d("startActivityWithConfig#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, callingFeatureId, intent, resolvedType,
            resultTo, resultWho, requestCode, startFlags,
            newConfig, options, userId
        )) as Int
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
        logger.d("startVoiceActivity#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            hostPkg, callingFeatureId, callingPid, callingUid, intent, resolvedType,
            session, interactor, flags,
            profilerInfo, options, userId
        )) as Int
    }

    fun startAssistantActivity(
        methodHandle: MethodHandle, callingPackage: String?, callingFeatureId: String?, callingPid: Int,
        callingUid: Int, intent: Intent?, resolvedType: String?, options: Bundle?, userId: Int
    ): Int {
        logger.d("startAssistantActivity#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            hostPkg, callingFeatureId, callingPid, callingUid, intent, resolvedType, options, userId
        )) as Int
    }

    /**
     *
     * @param recentsAnimationRunner IRecentsAnimationRunner
     */
    fun startRecentsActivity(
        methodHandle: MethodHandle, intent: Intent?, eventTime: Long,
        recentsAnimationRunner: Any?
    ) {
        logger.d("startRecentsActivity")
        methodHandle.invokeOriginMethod()
    }

    fun startActivityFromRecents(methodHandle: MethodHandle, taskId: Int, options: Bundle?): Int {
        logger.d("startActivityFromRecents")
        return methodHandle.invokeOriginMethod() as Int
    }

    fun startActivityAsCaller(
        methodHandle: MethodHandle, caller: Any?, callingPackage: String?,
        intent: Intent?, resolvedType: String?, resultTo: IBinder?, resultWho: String?,
        requestCode: Int, flags: Int, profilerInfo: Any?, options: Bundle?,
        permissionToken: IBinder?, ignoreTargetSecurity: Boolean, userId: Int
    ): Int {
        logger.d("startActivityAsCaller#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, intent, resolvedType, resultTo, resultWho,
            requestCode, flags, profilerInfo, options,
            permissionToken, ignoreTargetSecurity, userId
        )) as Int
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
        logger.d("moveTaskToFront#callingPackage = %s, hostPkg = %s", callingPackage, hostPkg)
        methodHandle.invokeOriginMethod(arrayOf(
            app, hostPkg, task, flags, options
        ))
    }

    /**
     * @return List<IBinder>
    </IBinder> */
    fun getAppTasks(methodHandle: MethodHandle, callingPackage: String?): Any? {
        logger.d("getAppTasks#callingPackage = %s", callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg))
    }

    /**
     * @param receiver IAssistDataReceiver
     */
    fun requestAssistDataForTask(
        methodHandle: MethodHandle, receiver: Any?, taskId: Int,
        callingPackageName: String?
    ): Boolean {
        logger.d("requestAssistDataForTask#callingPackage = %s, hostPkg = %s", callingPackageName, hostPkg)
        return methodHandle.invokeOriginMethod(arrayOf(
            receiver, taskId, hostPkg
        )) as Boolean
    }

    /**
     * @param adapter RemoteAnimationAdapter
     */
    fun registerRemoteAnimationForNextActivityStart(
        methodHandle: MethodHandle, packageName: String?,
        adapter: Any?
    ) {
        logger.d("registerRemoteAnimationForNextActivityStart#packageName = %s, hostPkg = %s", packageName, hostPkg)
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