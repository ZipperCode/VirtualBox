package com.virtual.box.core.hook.service

import android.os.Process
import com.virtual.box.base.util.log.Logger.Companion.getLogger
import com.virtual.box.base.util.log.L
import com.virtual.box.reflect.android.os.storage.HIStorageManager
import android.os.storage.StorageVolume
import com.virtual.box.core.hook.core.MethodHandle

class IStorageManagerHookHandle : BaseBinderHookHandle("mount") {
    private val logger = getLogger(L.HOOK_TAG, "IStorageManagerHookHandle")
    override fun getOriginObject(): Any? {
        return HIStorageManager.Stub.asInterface.call(originBinder)
    }

    fun mkdirs(methodHandle: MethodHandle, callingPkg: String?, path: String?) {
        logger.i("mkdirs#callingPkg = %s, path = %s", callingPkg, path)
        methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, path
            )
        )
    }

    /**
     * TODO Specified package com.sinyee.babybus.world under uid 10740 but it is really 10741
     * TODO 需要做uid处理
     */
    fun getVolumeList(methodHandle: MethodHandle, uid: Int, packageName: String?, flags: Int): Array<*>? {
        logger.i("getVolumeList#uid = %s, sysUid = %s, package = %s", uid, Process.myUid(), packageName)
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                Process.myUid(), hostPkg, flags
            )
        ) as Array<*>?
    }
}