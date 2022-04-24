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
    private val proxyMethod: Method,
    private val targetStubMethod: Method
) {

    private var hookResult: Boolean = false

    private var originArtMethod: Long = HExecutable.artMethod.get(targetStubMethod)

    var result: Any? = null

    private var proxyArtMethod: Long = HExecutable.artMethod.get(proxyMethod)

    fun checkAndSetProxyArtMethod(method: Method): MethodHookInfo{
        val originProxyArtMethod = proxyArtMethod
        proxyArtMethod = HExecutable.artMethod.get(method)
        System.err.println(">> originProxyArtMethod = $originProxyArtMethod")
        System.err.println(">> proxyArtMethod = $proxyArtMethod")
        return this
    }

    fun checkAndSetOriginArtMethod(method: Method): MethodHookInfo{
        val originOldArtMethod = originArtMethod
        originArtMethod = HExecutable.artMethod.get(method)
        System.err.println(">> originOldArtMethod   = $originOldArtMethod")
        System.err.println(">> originArtMethod      = $originArtMethod")
        return this
    }

    fun invoke(proxyObj: Any?, originObj: Any?, args: Array<out Any?>?): Any?{
        invokeProxyMethod(proxyObj, args)
        if (hookResult){
            return result
        }
        return invokeOriginMethod(originObj, args)
    }

    fun invoke1(proxyObj: Any?, originObj: Any?, method: Method,args: Array<out Any?>?): Any?{
        val targetMethodPtr = HExecutable.artMethod.get(method)
        val proxyMethodPtr = HExecutable.artMethod.get(proxyMethod)
        HExecutable.artMethod.set(method, proxyMethodPtr)
        HExecutable.artMethod.set(proxyMethod, targetMethodPtr)
        try {
            if (args == null){
                method.invoke(proxyObj)
            }
            method.invoke(originObj, args);
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            HExecutable.artMethod.set(method, targetMethodPtr)
            HExecutable.artMethod.set(proxyMethod, proxyMethodPtr)
        }
        if (args == null){
            return method.invoke(originObj)
        }
        return method.invoke(originObj, args);
    }

    private fun invokeProxyMethod(targetObj: Any?, args: Array<out Any?>?){
        System.err.println(">> 调用代理方法")
        synchronized(this){
            val holderPtr = VmCore.replaceMethod(proxyArtMethod, originArtMethod)
            try {
                result = if (args == null){
                    targetStubMethod.invoke(targetObj)
                }else{
                    targetStubMethod.invoke(targetObj, args)
                }
                hookResult = true
            }catch (t: Throwable){
                t.printStackTrace()
                hookResult = false
            }finally {
                VmCore.restoreMethod(holderPtr, proxyArtMethod)
            }
        }
    }

    private fun invokeOriginMethod(targetObj: Any?, args: Array<out Any?>?): Any?{
        System.err.println(">> 调用原方法")
        if (args == null){
            return targetStubMethod.invoke(targetObj)
        }
        return targetStubMethod.invoke(targetObj, *args)
    }

    override fun toString(): String {
        return "MethodHookInfo(proxyArtMethod=$proxyArtMethod, targetStubMethod=$targetStubMethod, hookResult=$hookResult, originArtMethod=$originArtMethod, result=$result)"
    }


    companion object{
        @JvmStatic
        fun getMethodIdentifier(method: Method): String{
            return method.name + "#" + getParametersString(*method.parameterTypes)
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