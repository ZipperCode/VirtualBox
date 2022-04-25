package com.virtual.box.core.hook.service

import android.os.ParcelFileDescriptor
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.reflect.android.app.HActivityManager
import com.virtual.box.reflect.android.app.HActivityManagerNative
import com.virtual.box.reflect.android.util.HSingleton

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/25
 **/
class ActivityManagerHookHandle: BaseHookHandle() {
    override fun initTargetObj(): Any? {
        val iActivityManager: Any? = if (BuildCompat.isAtLeastOreo) {
            HActivityManager.IActivityManagerSingleton.get()
        } else {
            HActivityManagerNative.gDefault.get()
        }
        return HSingleton.get.call(iActivityManager)
    }

    override fun hookInject(target: Any, proxy: Any) {
        val iActivityManager: Any? = if (BuildCompat.isAtLeastOreo) {
            HActivityManager.IActivityManagerSingleton.get()
        } else {
            HActivityManagerNative.gDefault.get()
        }
        HSingleton.mInstance.set(iActivityManager, proxy)
    }

    override fun isSupport(): Boolean {
        return HSingleton.mInstance.get() != proxyInvocation
    }

    fun openContentUri(methodHandle: MethodHandle, uriString: String): ParcelFileDescriptor?{
        return methodHandle.invokeOriginMethod() as? ParcelFileDescriptor
    }

    fun registerUidObserver(methodHandle: MethodHandle, observer: Any?, watch: Int, cutPoint: Int, callingPackage: String){
        methodHandle.invokeOriginMethod()
    }

    fun unregisterUidObserver(methodHandle: MethodHandle, observer: Any?){
        methodHandle.invokeOriginMethod()
    }

    fun isUidActive(methodHandle: MethodHandle, uid: Int, callingPackage: String): Boolean{
        return methodHandle.invokeOriginMethod() as Boolean
    }

}