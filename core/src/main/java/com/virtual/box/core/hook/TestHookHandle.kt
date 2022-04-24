package com.virtual.box.core.hook

import com.virtual.box.core.hook.core.MethodHookInfo
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.HashMap

class TestHookHandle: InvocationHandler {

    private val obj: Any = TestImpl()
    private var proxy: ITest

    private val thisToObjArtMethodPtrMap: MutableMap<Long, Long> = mutableMapOf()
    private val objToThisArtMethodPtrMap: MutableMap<Long, Long> = mutableMapOf()
    private val proxyMethodMap: HashMap<Method, Method?> = HashMap()

    private val proxyTargetMethodCache: HashMap<String, MethodHookInfo> = HashMap()

    private val filterMethodName = listOf(
        "toString","hashCode"
    )

    private var thisMethod: Method? = null
    init {
        proxy = Proxy.newProxyInstance(
            obj.javaClass.classLoader,
            arrayOf(ITest::class.java),
            this
        ) as ITest
        thisMethod = this.javaClass.getDeclaredMethod("test")
        val method = this.javaClass.getDeclaredMethod("test")
        println("thisMethod = $thisMethod method = $method")
        init()
    }


    private fun init(){
        val selfProxyDeclareMethods = javaClass.declaredMethods
        val targetDeclareMethods = if (proxy is Proxy){
            proxy.javaClass.declaredMethods
        }else{
            val interList = mutableListOf<Method>()
            for (inters in proxy.javaClass.interfaces) {
                interList.addAll(inters.declaredMethods)
            }
            interList.toTypedArray()
        }
        System.err.println(">> selfProxyDeclareMethods 的方法 ${Arrays.toString(targetDeclareMethods)}")
        System.err.println("> TestImplMethods ${Arrays.toString(proxy.javaClass.declaredMethods)}")
        for (selfProxyDeclareMethod in selfProxyDeclareMethods) {
            for (targetDeclareMethod in targetDeclareMethods) {
                val methodIdentifier = MethodHookInfo.getMethodIdentifier(selfProxyDeclareMethod);
                if (methodIdentifier == MethodHookInfo.getMethodIdentifier(targetDeclareMethod)){
                    System.err.println("> methodIdentifier = $methodIdentifier")
                    val methodHookInfo = MethodHookInfo(targetDeclareMethod, selfProxyDeclareMethod)
                    proxyTargetMethodCache[methodIdentifier] = methodHookInfo
                    System.err.println("> methodHookInfo = $methodHookInfo")
                }
            }
        }

        println(proxyTargetMethodCache)
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        val methodName = method.name
        if (filterMethodName.contains(methodName)){
            return if (args == null){
                method.invoke(obj)
            }else{
                method.invoke(obj, args)
            }
        }
        val key = MethodHookInfo.getMethodIdentifier(method)
        System.err.println("调用Invocation方法 ${method.name} - ${method.declaringClass.name}")
        System.err.println("obj = $obj proxy = ${proxy}")
        if (!proxyTargetMethodCache.containsKey(key)){
            return if (args == null){
                method.invoke(proxy)
            }else{
                method.invoke(proxy, args)
            }
        }
        val methodHookInfo = proxyTargetMethodCache[key]!!
        return methodHookInfo.checkAndSetOriginArtMethod(method).invoke(this, obj, args)

    }

    fun test(){
        System.err.println("执行Hook函数的test方法")
    }


    fun todoMethod(){
        proxy.test()
        proxy.test()
    }
}

class TestImpl: ITest{
    override fun test() {
        System.err.println("执行源函数的test方法")
    }

}