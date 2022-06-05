package com.virtual.box.core.hook.service

import android.os.IBinder
import com.virtual.box.reflect.android.os.HServiceManager
import com.virtual.box.reflect.com.android.internal.app.HIAppOpsService
import android.os.Build
import android.app.SyncNotedAppOp
import android.os.IInterface
import android.os.RemoteCallback
import android.app.AsyncNotedAppOp
import android.content.Context
import androidx.annotation.RequiresApi
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.MethodHandle

/**
 * @author zhangzhipeng
 * @date 2022/5/19
 */
@Suppress("UNUSED")
class AppOpsManagerHookHandle : BaseBinderHookHandle(Context.APP_OPS_SERVICE) {

    private val logger = Logger.getLogger(L.HOOK_TAG,"AppOpsManagerHookHandle")

    override fun getOriginObject(): Any? {
        val call = HServiceManager.getService.call(Context.APP_OPS_SERVICE)
        return HIAppOpsService.Stub.asInterface.call(call)
    }

    override fun isHooked(): Boolean {
        return getOriginObject() !== proxyInvocation
    }

    fun checkOperation(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?): Int {
        logger.e("checkOperation#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod() as Int
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    fun noteOperation(
        methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, attributionTag: String?,
        shouldCollectAsyncNotedOp: Boolean, message: String?, shouldCollectMessage: Boolean
    ): SyncNotedAppOp? {
        logger.e("noteOperation#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(
            arrayOf(
                code,
                uid,
                hostPkg,
                attributionTag,
                shouldCollectAsyncNotedOp,
                message,
                shouldCollectMessage
            )
        ) as SyncNotedAppOp?
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    fun startOperation(
        methodHandle: MethodHandle, clientId: IBinder?, code: Int, uid: Int, packageName: String?,
        attributionTag: String?, startIfModeDefault: Boolean,
        shouldCollectAsyncNotedOp: Boolean, message: String?, shouldCollectMessage: Boolean,
        attributionFlags: Int, attributionChainId: Int
    ): SyncNotedAppOp? {
        logger.e("startOperation#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(
            arrayOf(
                clientId, code, uid, hostPkg, attributionTag,
                startIfModeDefault, shouldCollectAsyncNotedOp, message, shouldCollectMessage,
                attributionFlags, attributionChainId
            )
        ) as SyncNotedAppOp?
    }

    fun finishOperation(
        methodHandle: MethodHandle, clientId: IBinder?, code: Int, uid: Int, packageName: String?,
        attributionTag: String?
    ) {
        logger.e("finishOperation#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(clientId, code, uid, hostPkg, attributionTag))
    }

    /**
     *
     * @param callback IAppOpsCallback
     */
    fun startWatchingMode(methodHandle: MethodHandle, op: Int, packageName: String?, callback: IInterface?) {
        logger.e("startWatchingMode#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(op, hostPkg, callback))
    }

    fun checkAudioOperation(methodHandle: MethodHandle, code: Int, usage: Int, uid: Int, packageName: String?): Int {
        logger.e("checkAudioOperation#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(code, usage, uid, hostPkg)) as Int
    }

    // Remaining methods are only used  Java.
    fun checkPackage(methodHandle: MethodHandle, uid: Int, packageName: String?): Int {
        logger.e("checkPackage#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(uid, hostPkg)) as Int
    }

    /**
     * @return MessageSamplingConfig
     */
    fun reportRuntimeAppOpAccessMessageAndGetConfig(
        methodHandle: MethodHandle, packageName: String?,
        appOp: SyncNotedAppOp?, message: String?
    ): Any? {
        logger.e("reportRuntimeAppOpAccessMessageAndGetConfig#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, appOp, message))
    }

    fun getOpsForPackage(methodHandle: MethodHandle, uid: Int, packageName: String?, ops: IntArray?): List<*>? {
        logger.e("getOpsForPackage#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(uid, hostPkg, ops)) as List<*>?
    }

    fun getHistoricalOps(
        methodHandle: MethodHandle, uid: Int, packageName: String?, attributionTag: String?, ops: List<String?>?,
        historyFlags: Int, filter: Int, beginTimeMillis: Long, endTimeMillis: Long, flags: Int,
        callback: RemoteCallback?
    ) {
        logger.e("getHistoricalOps#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(
            arrayOf(
                uid,
                hostPkg,
                attributionTag,
                ops,
                historyFlags,
                filter,
                beginTimeMillis,
                endTimeMillis,
                flags,
                callback
            )
        )
    }

    fun getHistoricalOpsFromDiskRaw(
        methodHandle: MethodHandle, uid: Int, packageName: String?, attributionTag: String?,
        ops: List<String?>?, historyFlags: Int, filter: Int, beginTimeMillis: Long,
        endTimeMillis: Long, flags: Int, callback: RemoteCallback?
    ) {
        logger.e("getHistoricalOpsFromDiskRaw#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(
            arrayOf(
                uid,
                hostPkg,
                attributionTag,
                ops,
                historyFlags,
                filter,
                beginTimeMillis,
                endTimeMillis,
                flags,
                callback
            )
        )
    }

    fun resetPackageOpsNoHistory(methodHandle: MethodHandle, packageName: String?) {
        logger.e("resetPackageOpsNoHistory#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(hostPkg))
    }

    fun setMode(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, mode: Int) {
        logger.e("setMode#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg, mode))
    }

    @Deprecated("maxTargetSdk = 30")
    fun resetAllModes(methodHandle: MethodHandle, reqUserId: Int, reqPackageName: String?) {
        logger.e("resetAllModes#packageName = %s", reqPackageName)
        methodHandle.invokeOriginMethod(arrayOf(reqUserId, hostPkg))
    }

    fun isOperationActive(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?): Boolean {
        logger.e("isOperationActive#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg)) as Boolean
    }

    fun isProxying(
        methodHandle: MethodHandle, op: Int, proxyPackageName: String?, proxyAttributionTag: String?, proxiedUid: Int,
        proxiedPackageName: String?
    ): Boolean {
        logger.e("isProxying#proxyPackageName = %s, proxiedPackageName = %s", proxyPackageName,proxiedPackageName)
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * @param callback IAppOpsCallback
     */
    fun startWatchingModeWithFlags(methodHandle: MethodHandle, op: Int, packageName: String?, flags: Int, callback: IInterface?) {
        logger.e("startWatchingModeWithFlags#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(op, hostPkg, flags, callback))
    }

    /**
     * @param callback IAppOpsAsyncNotedCallback
     */
    fun startWatchingAsyncNoted(methodHandle: MethodHandle, packageName: String?, callback: IInterface?) {
        logger.e("startWatchingAsyncNoted#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(hostPkg, callback))
    }

    /**
     * @param callback IAppOpsAsyncNotedCallback
     */
    fun stopWatchingAsyncNoted(methodHandle: MethodHandle, packageName: String?, callback: IInterface?) {
        logger.e("stopWatchingAsyncNoted#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(hostPkg, callback))
    }

    /**
     * @return List<AsyncNotedAppOp>
     */
    fun extractAsyncOps(methodHandle: MethodHandle, packageName: String?): List<*>? {
        logger.e("extractAsyncOps#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg)) as List<*>?
    }

    fun checkOperationRaw(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, attributionTag: String?): Int {
        logger.e("checkOperationRaw#packageName = %s", packageName)
        return methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg, attributionTag)) as Int
    }

    fun collectNoteOpCallsForValidation(methodHandle: MethodHandle, stackTrace: String?, op: Int, packageName: String?, version: Long) {
        logger.e("collectNoteOpCallsForValidation#packageName = %s", packageName)
        methodHandle.invokeOriginMethod(arrayOf(stackTrace, op, hostPkg, version))
    }
}