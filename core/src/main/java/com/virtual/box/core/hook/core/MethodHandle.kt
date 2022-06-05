package com.virtual.box.core.hook.core

import com.virtual.box.base.ext.kotlinInvokeOrigin
import com.virtual.box.base.util.log.L
import com.virtual.box.core.exception.CalledOriginMethodException
import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/25
 **/
class MethodHandle(
    val originObj: Any?,
    val targetMethod: Method,
    val args: Array<out Any?>?,
    private val nativeHolderPtr: Long,
    private val targetNativeHolderPtr: Long
) {

   val hasRestoreMethod: AtomicBoolean = AtomicBoolean(false)

   fun invokeOriginMethod(): Any?{
       return invokeOriginMethod(args)
   }

    fun invokeOriginMethod(args: Array<out Any?>?): Any?{
        try {
            if (nativeHolderPtr != 0L) {
                VmCore.restoreMethod(nativeHolderPtr, targetNativeHolderPtr)
                HExecutable.artMethod.set(targetMethod, targetNativeHolderPtr)
                hasRestoreMethod.set(true)
            }
            return targetMethod.kotlinInvokeOrigin(originObj, args)
        } catch (e: Throwable) {
            L.printStackTrace(e)
            throw CalledOriginMethodException(e.cause)
        }
    }
}