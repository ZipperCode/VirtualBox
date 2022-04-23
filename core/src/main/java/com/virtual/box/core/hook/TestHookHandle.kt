package com.virtual.box.core.hook

import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.reflect.java.lang.reflect.HExecutable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class TestHookHandle: InvocationHandler {

    private val obj: ITest = TestImpl()
    private var proxy: ITest

    private val thisToObjArtMethodPtrMap: MutableMap<Long, Long> = mutableMapOf()
    private val objToThisArtMethodPtrMap: MutableMap<Long, Long> = mutableMapOf()
    private val proxyMethodMap: HashMap<Method, Method?> = HashMap()

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
        val declaredMethods = proxy.javaClass.declaredMethods
        val declaredMethods1 = proxy.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            for (declaredMethod1 in declaredMethods1) {
                if (declaredMethod.equals(declaredMethod1)){
                    if (!proxyMethodMap.containsKey(declaredMethod)){
                        proxyMethodMap[declaredMethod] = declaredMethod1

                    }
                }

            }
        }


        println()
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        System.err.println("调用Invocation方法")
        val thisArtMethodPtr = HExecutable.artMethod.get(thisMethod)
        val targetArtMethodPtr = HExecutable.artMethod.get(method)
        if (!thisToObjArtMethodPtrMap.containsKey(thisArtMethodPtr)){
            thisToObjArtMethodPtrMap[thisArtMethodPtr] = targetArtMethodPtr
        }
        if (!objToThisArtMethodPtrMap.containsKey(targetArtMethodPtr)){
            objToThisArtMethodPtrMap[targetArtMethodPtr] = thisArtMethodPtr
        }
        println("> thisArtMethodPtr = $thisArtMethodPtr")
        println("> thisArtMethodPtr = $targetArtMethodPtr")
//        VmCore.replaceMethod(thisArtMethodPtr, targetArtMethodPtr)
//        println("> 替换成功, 调用方法执行")
//        val result = method.invoke(this, )
//        println("> 方法执行完成")
        return null
    }

    fun test(){
        System.err.println("执行Hook函数的test方法")
    }


    fun todoMethod(){
        proxy?.test()
        proxy?.test()
    }
}

class TestImpl: ITest{
    override fun test() {
        System.err.println("执行源函数的test方法")
    }

}