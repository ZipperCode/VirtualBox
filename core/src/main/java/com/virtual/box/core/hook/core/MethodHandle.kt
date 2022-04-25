package com.virtual.box.core.hook.core

import java.lang.reflect.Method

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

   var hasRestoreMethod: Boolean = false
        private set

   fun invokeOriginMethod(): Any?{
       return invokeOriginMethod(args)
   }

    fun invokeOriginMethod(args: Array<out Any?>?): Any?{
        if (nativeHolderPtr != 0L){
            VmCore.restoreMethod(nativeHolderPtr, targetNativeHolderPtr)
            hasRestoreMethod = true
        }
        return if (args == null){
            targetMethod.invoke(originObj)
        }else{
            targetMethod.invoke(originObj, *args)
        }
    }
}