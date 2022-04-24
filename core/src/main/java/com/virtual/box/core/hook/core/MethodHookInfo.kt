package com.virtual.box.core.hook.core

import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.reflect.Method


/**
 *
 * @author zhangzhipeng
 * @date   2022/4/24
 **/
class MethodHookInfo(
    private val targetStubMethod: Method
) {

    private var hookResult: Boolean = false

    private var originArtMethod: Long = HExecutable.artMethod.get(targetStubMethod)

    var result: Any? = null

    private var proxyArtMethod: Long = -1

    fun checkAndSetProxyArtMethod(method: Method): MethodHookInfo{
        val originProxyArtMethod = proxyArtMethod
        proxyArtMethod = HExecutable.artMethod.get(method)
        System.err.println(">> originProxyArtMethod = $originProxyArtMethod")
        System.err.println(">> proxyArtMethod = $proxyArtMethod")
        return this
    }

    fun invoke(proxyObj: Any?, originObj: Any?, args: Array<out Any?>?): Any?{
        if (proxyArtMethod != -1L){
            invokeProxyMethod(originObj, args)
            if (hookResult){
                return result
            }
        }
        return invokeOriginMethod(proxyObj, args)
    }

    private fun invokeProxyMethod(targetObj: Any?, args: Array<out Any?>?){
        System.err.println(">> 调用代理方法")
        if (proxyArtMethod != -1L){
            VmCore.replaceMethod(proxyArtMethod, originArtMethod)
        }
        synchronized(this){
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
                if (proxyArtMethod != -1L){
                    VmCore.replaceMethod(originArtMethod, proxyArtMethod)
                }
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