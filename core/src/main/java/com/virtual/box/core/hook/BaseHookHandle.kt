package com.virtual.box.core.hook

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

abstract class BaseHookHandle : InvocationHandler, IInjectHook {
    private var mTarget: Any? = null
    private var mProxyInvocation: Any? = null

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        TODO("Not yet implemented")
    }

    override fun initHook() {
        mTarget = initTargetObj() ?: return
        mProxyInvocation = Proxy.newProxyInstance(
            mTarget!!.javaClass.classLoader,
            getAllInterface(mTarget!!.javaClass),
            this
        )
        hookInject(mTarget!!, mProxyInvocation!!)
    }

    protected abstract fun initTargetObj(): Any?

    protected abstract fun hookInject(target: Any, proxy: Any)

    open fun getAllInterface(clazz: Class<*>): Array<Class<*>?> {
        val classes = HashSet<Class<*>>()
        getAllInterfaces(clazz, classes)
        val result: Array<Class<*>?> = arrayOfNulls(classes.size)
        classes.toArray(result)
        return result
    }

    open fun getAllInterfaces(clazz: Class<*>, interfaceCollection: HashSet<Class<*>>) {
        val classes = clazz.interfaces
        if (classes.isNotEmpty()) {
            interfaceCollection.addAll(listOf(*classes))
        }
        if (clazz.superclass != Any::class.java) {
            getAllInterfaces(clazz.superclass, interfaceCollection)
        }
    }
}