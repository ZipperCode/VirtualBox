package com.virtual.box.core.hook

import com.virtual.box.core.hook.core.MethodHookInfo
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

abstract class BaseHookHandle : InvocationHandler, IInjectHook {
    protected open var target: Any? = null
    protected var proxyInvocation: Any? = null
        private set

    private val proxyTargetMethodCache: HashMap<String, MethodHookInfo> = HashMap()

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        val methodIdentifier = method.name
        if (proxyTargetMethodCache.containsKey(methodIdentifier)){
            return proxyTargetMethodCache[methodIdentifier]!!.invoke1(this, proxy, method, args)
        }
        return kotlinInvokeOrigin(proxy, method, args)
    }

    protected open fun kotlinInvokeOrigin(proxy: Any, method: Method, args: Array<out Any?>?): Any?{
        return if (args == null){
            method.invoke(proxy)
        }else{
            method.invoke(proxy, *args)
        }
    }

    override fun initHook() {
        target = initTargetObj() ?: return
        proxyInvocation = Proxy.newProxyInstance(
            target!!.javaClass.classLoader,
            getAllInterface(target!!.javaClass),
            this
        ) ?: return
        val selfDeclareMethods = javaClass.declaredMethods
        val proxyDeclareMethods = proxyInvocation!!.javaClass.declaredMethods

        for (selfDeclareMethod in selfDeclareMethods) {
            for (proxyDeclareMethod in proxyDeclareMethods) {
                selfDeclareMethod.isAccessible = true
                val methodIdentifier = selfDeclareMethod.name
                if (methodIdentifier == proxyDeclareMethod.name){
                    val methodHookInfo = MethodHookInfo(selfDeclareMethod)
                    proxyTargetMethodCache[methodIdentifier] = methodHookInfo
                }
            }
        }

        hookInject(target!!, proxyInvocation!!)
    }

    protected abstract fun initTargetObj(): Any?

    protected abstract fun hookInject(target: Any, proxy: Any)

    private fun getAllInterface(clazz: Class<*>): Array<Class<*>?> {
        val classes = HashSet<Class<*>>()
        getAllInterfaces(clazz, classes)
        val result: Array<Class<*>?> = arrayOfNulls(classes.size)
        classes.toArray(result)
        return result
    }

    private fun getAllInterfaces(clazz: Class<*>, interfaceCollection: HashSet<Class<*>>) {
        val classes = clazz.interfaces
        if (classes.isNotEmpty()) {
            interfaceCollection.addAll(listOf(*classes))
        }
        if (clazz.superclass != Any::class.java) {
            getAllInterfaces(clazz.superclass, interfaceCollection)
        }
    }
}