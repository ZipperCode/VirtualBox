package com.virtual.box.core.hook.service

import com.virtual.box.reflect.android.content.HIClipboard
import com.virtual.box.core.VirtualBox
import com.virtual.box.reflect.MirrorReflection
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import com.virtual.box.core.hook.core.MethodHandle

class ClipboardServiceHookHandle : BaseBinderHookHandle(Context.CLIPBOARD_SERVICE) {
    override fun getOriginObject(): Any? {
        return HIClipboard.Stub.asInterface.call(originBinder)
    }

    override fun hookInject(target: Any, proxy: Any) {
        super.hookInject(target, proxy)
        val systemService = VirtualBox.get().hostContext
            .getSystemService(Context.CLIPBOARD_SERVICE)
        MirrorReflection.on("android.content.ClipboardManager")
            .field<Any>("mService")[systemService] = proxy
    }

    fun setPrimaryClip(methodHandle: MethodHandle, clip: ClipData?, callingPackage: String?, userId: Int) {
        methodHandle.invokeOriginMethod(
            arrayOf(
                clip, hostPkg, userId
            )
        )
    }

    fun setPrimaryClipAsPackage(
        methodHandle: MethodHandle, clip: ClipData?, callingPackage: String?, userId: Int,
        sourcePackage: String?
    ) {
        methodHandle.invokeOriginMethod(
            arrayOf(
                clip, hostPkg, userId, sourcePackage
            )
        )
    }

    fun clearPrimaryClip(methodHandle: MethodHandle, callingPackage: String?, userId: Int) {
        methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        )
    }

    fun getPrimaryClip(methodHandle: MethodHandle, pkg: String?, userId: Int): ClipData? {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        ) as ClipData?
    }

    fun getPrimaryClipDescription(methodHandle: MethodHandle, callingPackage: String?, userId: Int): ClipDescription? {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        ) as ClipDescription?
    }

    fun hasPrimaryClip(methodHandle: MethodHandle, callingPackage: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        ) as Boolean
    }

    fun addPrimaryClipChangedListener(
        methodHandle: MethodHandle, listener: Any?,
        callingPackage: String?, userId: Int
    ) {
        methodHandle.invokeOriginMethod(
            arrayOf(
                listener, hostPkg, userId
            )
        )
    }

    fun removePrimaryClipChangedListener(
        methodHandle: MethodHandle, listener: Any?,
        callingPackage: String?, userId: Int
    ) {
        methodHandle.invokeOriginMethod(
            arrayOf(
                listener, hostPkg, userId
            )
        )
    }

    fun hasClipboardText(methodHandle: MethodHandle, callingPackage: String?, userId: Int): Boolean {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        ) as Boolean
    }

    fun getPrimaryClipSource(methodHandle: MethodHandle, callingPackage: String?, userId: Int): String? {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                hostPkg, userId
            )
        ) as String?
    }
}