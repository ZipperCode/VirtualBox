package com.virtual.box.core.hook.core

import com.virtual.box.base.util.log.L
import java.lang.reflect.Method
import java.util.*

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
        if (args == null){
            L.hdParam("invokeMethod >> ${targetMethod.name} ")
        }else{
            L.hdParam("invokeMethod >> ${targetMethod.name} \n args => %s", Arrays.toString(args))
        }
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