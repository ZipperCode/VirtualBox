package com.virtual.box.core.hook

import com.virtual.box.base.ext.kotlinInvokeOrigin
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.MethodHookInfo
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

abstract class BaseHookHandle : InvocationHandler, IInjectHook {

    private val logger = Logger.Companion.getLogger(L.HOOK_TAG,"BaseHookHandle")
    @JvmField
    protected var hostPkg: String = VirtualBox.get().hostPkg

    protected open var target: Any? = null

    protected var proxyInvocation: Any? = null
        private set

    private val proxyTargetMethodCache: HashMap<String, MethodHookInfo> = HashMap()

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        val methodIdentifier = method.name
        val methodParamIdentifier = getMethodParamIdentifier(method)
        val identifier = "$methodIdentifier#$methodParamIdentifier"
        if (proxyTargetMethodCache.containsKey(identifier)){
            return proxyTargetMethodCache[identifier]!!.invoke(this, target!!, method, args)
        }
        return kotlinInvokeOrigin(target!!, method, args)
    }

    protected open fun kotlinInvokeOrigin(proxy: Any, method: Method, args: Array<out Any?>?): Any?{
        return method.kotlinInvokeOrigin(target!!, args)
    }

    override fun initHook() {
        try {
            target = getOriginObject() ?: return
            if (target is Proxy){
                return
            }
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
                        val selfMethodParamIdentifier = getSelfMethodParamIdentifier(selfDeclareMethod)
                        val methodParamIdentifier = getMethodParamIdentifier(proxyDeclareMethod)
                        val selfIdentifier = "$methodIdentifier#$selfMethodParamIdentifier"
                        val proxyIdentifier = "$methodIdentifier#$methodParamIdentifier"
                        if (selfIdentifier == proxyIdentifier){
                            val methodHookInfo = MethodHookInfo(selfDeclareMethod)
                            proxyTargetMethodCache[proxyIdentifier] = methodHookInfo
                        }

                    }
                }
            }

            hookInject(target!!, proxyInvocation!!)
        }catch (e: Throwable){
            L.printStackTrace(e)
        }
    }

    protected abstract fun getOriginObject(): Any?

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

    private fun getSelfMethodParamIdentifier(method: Method): String{
        return "${method.parameterTypes.size - 1}"
//        val paramType = method.parameterTypes
//        val newParamType = arrayOfNulls<Any>(paramType.size - 1)
//        for (i in newParamType.indices){
//            newParamType[i] = paramType[i+1]
//        }
//        return newParamType.contentToString()
    }

    private fun getMethodParamIdentifier(method: Method): String{
//        return method.parameterTypes.contentToString()
        return "${method.parameterTypes.size}"
    }
}