package com.virtual.box.core.hook.core

import android.util.Log
import com.virtual.box.base.util.log.L
import com.virtual.box.core.exception.CalledOriginMethodException
import java.lang.Exception
import java.lang.reflect.Method
import java.util.*
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
            if (nativeHolderPtr != 0L){
                VmCore.restoreMethod(nativeHolderPtr, targetNativeHolderPtr)
                hasRestoreMethod.set(true)
            }
            val result =  if (args == null){
                targetMethod.invoke(originObj)
            }else{
                targetMethod.invoke(originObj, *args)
            }
            return result
        }catch (e: Throwable){
            L.printStackTrace(e)
            throw CalledOriginMethodException(e.cause)
        }
    }
}