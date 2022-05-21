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
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.MethodHandle

/**
 * @author zhangzhipeng
 * @date 2022/5/19
 */
@Suppress("UNUSED")
class AppOpsManagerHookHandle : BaseBinderHookHandle(Context.APP_OPS_SERVICE) {

    override fun getOriginObject(): Any? {
        val call = HServiceManager.getService.call(Context.APP_OPS_SERVICE)
        return HIAppOpsService.Stub.asInterface.call(call)
    }

    override fun isHooked(): Boolean {
        return getOriginObject() !== proxyInvocation
    }

    fun checkOperation(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?): Int {
        return methodHandle.invokeOriginMethod() as Int
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    fun noteOperation(
        methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, attributionTag: String?,
        shouldCollectAsyncNotedOp: Boolean, message: String?, shouldCollectMessage: Boolean
    ): SyncNotedAppOp? {
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
        methodHandle.invokeOriginMethod(arrayOf(clientId, code, uid, hostPkg, attributionTag))
    }

    /**
     *
     * @param callback IAppOpsCallback
     */
    fun startWatchingMode(methodHandle: MethodHandle, op: Int, packageName: String?, callback: IInterface?) {
        methodHandle.invokeOriginMethod(arrayOf(op, hostPkg, callback))
    }

    fun checkAudioOperation(methodHandle: MethodHandle, code: Int, usage: Int, uid: Int, packageName: String?): Int {
        return methodHandle.invokeOriginMethod(arrayOf(code, usage, uid, hostPkg)) as Int
    }

    // Remaining methods are only used  Java.
    fun checkPackage(methodHandle: MethodHandle, uid: Int, packageName: String?): Int {
        return methodHandle.invokeOriginMethod(arrayOf(uid, hostPkg)) as Int
    }

    /**
     * @return MessageSamplingConfig
     */
    fun reportRuntimeAppOpAccessMessageAndGetConfig(
        methodHandle: MethodHandle, packageName: String?,
        appOp: SyncNotedAppOp?, message: String?
    ): Any? {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, appOp, message))
    }

    fun getOpsForPackage(methodHandle: MethodHandle, uid: Int, packageName: String?, ops: IntArray?): List<*>? {
        return methodHandle.invokeOriginMethod(arrayOf(uid, hostPkg, ops)) as List<*>?
    }

    fun getHistoricalOps(
        methodHandle: MethodHandle, uid: Int, packageName: String?, attributionTag: String?, ops: List<String?>?,
        historyFlags: Int, filter: Int, beginTimeMillis: Long, endTimeMillis: Long, flags: Int,
        callback: RemoteCallback?
    ) {
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
        methodHandle.invokeOriginMethod(arrayOf(hostPkg))
    }

    fun setMode(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, mode: Int) {
        methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg, mode))
    }

    @Deprecated("maxTargetSdk = 30")
    fun resetAllModes(methodHandle: MethodHandle, reqUserId: Int, reqPackageName: String?) {
        methodHandle.invokeOriginMethod(arrayOf(reqUserId, hostPkg))
    }

    fun isOperationActive(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?): Boolean {
        return methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg)) as Boolean
    }

    fun isProxying(
        methodHandle: MethodHandle, op: Int, proxyPackageName: String?, proxyAttributionTag: String?, proxiedUid: Int,
        proxiedPackageName: String?
    ): Boolean {
        return methodHandle.invokeOriginMethod() as Boolean
    }

    /**
     * @param callback IAppOpsCallback
     */
    fun startWatchingModeWithFlags(methodHandle: MethodHandle, op: Int, packageName: String?, flags: Int, callback: IInterface?) {
        methodHandle.invokeOriginMethod(arrayOf(op, hostPkg, flags, callback))
    }

    /**
     * @param callback IAppOpsAsyncNotedCallback
     */
    fun startWatchingAsyncNoted(methodHandle: MethodHandle, packageName: String?, callback: IInterface?) {
        methodHandle.invokeOriginMethod(arrayOf(hostPkg, callback))
    }

    /**
     * @param callback IAppOpsAsyncNotedCallback
     */
    fun stopWatchingAsyncNoted(methodHandle: MethodHandle, packageName: String?, callback: IInterface?) {
        methodHandle.invokeOriginMethod(arrayOf(hostPkg, callback))
    }

    /**
     * @return List<AsyncNotedAppOp>
     */
    fun extractAsyncOps(methodHandle: MethodHandle, packageName: String?): List<*>? {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg)) as List<*>?
    }

    fun checkOperationRaw(methodHandle: MethodHandle, code: Int, uid: Int, packageName: String?, attributionTag: String?): Int {
        return methodHandle.invokeOriginMethod(arrayOf(code, uid, hostPkg, attributionTag)) as Int
    }

    fun collectNoteOpCallsForValidation(methodHandle: MethodHandle, stackTrace: String?, op: Int, packageName: String?, version: Long) {
        methodHandle.invokeOriginMethod(arrayOf(stackTrace, op, hostPkg, version))
    }
}