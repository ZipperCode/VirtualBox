package com.virtual.box.core.compat

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.MethodHandle

object ActivityManagerCompat {

    private val logger = Logger.getLogger(L.HOOK_TAG,"ActivityManagerCompat")

    private val hostPkg: String = VirtualBox.get().hostPkg

    fun startActivity(
        methodHandle: MethodHandle,
        caller: Any?, callingPackage: String?, intent: Intent?, resolvedType: String?,
        resultTo: IBinder?, resultWho: String?,
        requestCode: Int, flags: Int, profilerInfo: Any?, options: Bundle?
    ): Int {
        logger.e("startActivity#Deprecated > maxTargetSdk = 29")
        return methodHandle.invokeOriginMethod(arrayOf(
            caller, hostPkg, intent, resolvedType, resultTo, resultWho,
            requestCode, flags, profilerInfo, options
        )) as Int
    }
}