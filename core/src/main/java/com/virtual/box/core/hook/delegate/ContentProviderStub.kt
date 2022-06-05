package com.virtual.box.core.hook.delegate

import android.os.IInterface
import com.virtual.box.base.ext.kotlinInvokeOrigin
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.hook.IInjectHook
import com.virtual.box.reflect.android.HAttributionSourceState
import com.virtual.box.reflect.android.content.HAttributionSource
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.HashSet

class ContentProviderStub: InvocationHandler, IInjectHook {
    private val logger: Logger = Logger.Companion.getLogger(L.HOOK_TAG, "ContentProviderStub")

    private var target: Any?= null

    private var proxyInvocation: IInterface? = null

    private lateinit var pkg: String

    fun wrapper(contentProviderProxy: IInterface, appPkg: String): IInterface? {
        target = contentProviderProxy
        pkg = appPkg
        initHook()
        return proxyInvocation
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        if ("asBinder" == method.name) {
            return method.kotlinInvokeOrigin(target!!, args)
        }
        if (args != null && args.isNotEmpty()) {
            val arg = args[0]
            if (arg is String) {
                args[0] = pkg
            } else if (arg?.javaClass?.name == "AttributionSource") {
                Helper.fixAttributionSource(arg, pkg)
            }
        }
        return method.kotlinInvokeOrigin(target!!, args)
    }

    override fun initHook() {
        target ?: return
        proxyInvocation = Proxy.newProxyInstance(
            target!!.javaClass.classLoader,
            getAllInterface(target!!.javaClass),
            this
        ) as? IInterface ?: return
    }

    override fun isHooked(): Boolean = true

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

    object Helper {
        internal fun fixAttributionSource(attributionSource: Any?, hostPks: String) {
            attributionSource ?: return
            val attributionSourceState = HAttributionSource.mAttributionSourceState.get(attributionSource)
            attributionSourceState ?: return
            HAttributionSourceState.packageName.set(attributionSourceState, hostPks)
            val nextAttributionSource = HAttributionSource.getNext.call(attributionSource)
            nextAttributionSource ?: return
            fixAttributionSource(nextAttributionSource, hostPks)

        }
    }
}