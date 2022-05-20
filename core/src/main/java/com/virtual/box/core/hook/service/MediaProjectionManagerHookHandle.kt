package com.virtual.box.core.hook.service

import android.content.Context
import com.virtual.box.reflect.android.os.HServiceManager
import com.virtual.box.reflect.android.media.HIMediaProjectionManager
import android.os.IInterface
import com.virtual.box.core.hook.core.MethodHandle
/**
 * @author zipper
 */
class MediaProjectionManagerHookHandle : BaseBinderHookHandle(Context.MEDIA_PROJECTION_SERVICE) {
    override fun getOriginObject(): Any? {
        val binder = HServiceManager.getService.call(Context.MEDIA_PROJECTION_SERVICE)
        return HIMediaProjectionManager.Stub.asInterface.call(binder)
    }

    fun hasProjectionPermission(methodHandle: MethodHandle, uid: Int, packageName: String?): Boolean {
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(uid, hostPkg)) as Boolean
    }

    fun createProjection(
        methodHandle: MethodHandle, uid: Int, packageName: String?, type: Int,
        permanentGrant: Boolean
    ): IInterface? {
        return methodHandle.invokeOriginMethod(arrayOf(uid, packageName, type, permanentGrant)) as IInterface?
    }
}