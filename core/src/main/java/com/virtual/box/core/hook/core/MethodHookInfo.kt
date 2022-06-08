package com.virtual.box.core.hook.core

import android.os.Debug
import android.util.Log
import com.virtual.box.base.ext.kotlinInvokeOrigin
import com.virtual.box.base.util.log.L
import com.virtual.box.core.exception.CalledOriginMethodException
import com.virtual.box.core.hook.service.ActivityManagerHookHandle
import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import kotlin.Exception


/**
 *
 * @author zhangzhipeng
 * @date   2022/4/24
 **/
class MethodHookInfo(
    private val targetStubMethod: Method
) {
    private var originArtMethod: Long = HExecutable.artMethod.get(targetStubMethod)

    fun invoke(proxyObj: Any?, originObj: Any?, method: Method, args: Array<out Any?>?): Any?{
        var result: Any? = null
        val curProxyMethod1Ptr = originArtMethod
        val curTargetMethodPtr = HExecutable.artMethod.get(method)
        synchronized(this){
            var hookResult: Boolean = false
            val holderPtr = VmCore.replaceMethod(curProxyMethod1Ptr, curTargetMethodPtr)
            val methodHandle = MethodHandle(
                originObj, method, args, holderPtr, curTargetMethodPtr
            )
            var throwable: Throwable? = null
            try {
                result = if (args == null){
                    targetStubMethod.invoke(proxyObj, methodHandle)
                }else{
                    targetStubMethod.invoke(proxyObj, methodHandle, *args)
                }
                hookResult = true
            } catch (t: Throwable){
                if (t.cause is CalledOriginMethodException){
                    Log.e("HOOK", Log.getStackTraceString(t.cause!!.cause!!))
                    throw t.cause!!.cause!!
                }
                throwable = t
                hookResult = false
            }finally {
                if (!methodHandle.hasRestoreMethod.get()){
                    VmCore.restoreMethod(holderPtr, curTargetMethodPtr)
                    HExecutable.artMethod.set(targetStubMethod, curTargetMethodPtr)
                }else{
                    if (throwable != null){
                        throw throwable
                    }
                }
                L.printStackTrace(throwable)
            }
            if (hookResult){
                return result
            }
            return targetStubMethod.kotlinInvokeOrigin(originObj, args)
        }
    }

    override fun toString(): String {
        return "MethodHookInfo(targetStubMethod=$targetStubMethod, originArtMethod=$originArtMethod)"
    }

    companion object{
        @JvmStatic
        fun getMethodIdentifier(method: Method): String{
            return method.name
        }

        private fun getParametersString(vararg clazzes: Class<*>?): String {
            val sb = StringBuilder("(")
            var first = true
            for (clazz in clazzes) {
                if (first) first = false else sb.append(",")
                if (clazz != null) sb.append(clazz.canonicalName) else sb.append("null")
            }
            sb.append(")")
            return sb.toString()
        }
    }

}