package com.virtual.box.core.hook.core

import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.Exception
import java.lang.reflect.Method


/**
 *
 * @author zhangzhipeng
 * @date   2022/4/24
 **/
class MethodHookInfo(
    private val targetStubMethod: Method
) {
    private var originArtMethod: Long = HExecutable.artMethod.get(targetStubMethod)

    fun invoke1(proxyObj: Any?, originObj: Any?, method: Method, args: Array<out Any?>?): Any?{
        var result: Any? = null
        val curProxyMethod1Ptr = originArtMethod
        val curTargetMethodPtr = HExecutable.artMethod.get(method)
        synchronized(targetStubMethod){
            var hookResult: Boolean = false
            val holderPtr = VmCore.replaceMethod(curProxyMethod1Ptr, curTargetMethodPtr)
            val methodHandle = MethodHandle(
                originObj, method, args, holderPtr, curTargetMethodPtr
            )
            try {
                result = if (args == null){
                    targetStubMethod.invoke(proxyObj, methodHandle)
                }else{
                    targetStubMethod.invoke(proxyObj, methodHandle, *args)
                }
                hookResult = true
            }catch (t: Throwable){
                t.printStackTrace()
                hookResult = false
            }finally {
                if (!methodHandle.hasRestoreMethod){
                    VmCore.restoreMethod(holderPtr, curTargetMethodPtr)
                }
            }
            if (hookResult){
                return result
            }
            HExecutable.artMethod.set(targetStubMethod, curTargetMethodPtr)
            if (args == null){
                return targetStubMethod.invoke(originObj)
            }
            return targetStubMethod.invoke(originObj, *args)
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