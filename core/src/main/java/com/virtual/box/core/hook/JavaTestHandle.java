package com.virtual.box.core.hook;

import android.util.Log;

import com.virtual.box.base.util.log.L;
import com.virtual.box.core.hook.core.MethodHookInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class JavaTestHandle implements InvocationHandler {

    private Object target = new TestImpl();

    private ITest proxy;

    private final HashMap<String, MethodHookInfo> proxyTargetMethodCache = new HashMap<>();

    public JavaTestHandle(){
        proxy = (ITest) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{ITest.class},
        this
        );
        Method[] targetDeclareMethods = proxy.getClass().getDeclaredMethods();
        Method[] selfProxyDeclareMethods = getClass().getDeclaredMethods();
        for (Method selfProxyDeclareMethod : selfProxyDeclareMethods) {
            for (Method targetDeclareMethod : targetDeclareMethods) {
                selfProxyDeclareMethod.setAccessible(true);
                String methodIdentifier = MethodHookInfo.getMethodIdentifier(selfProxyDeclareMethod);
                if (methodIdentifier.equals(MethodHookInfo.getMethodIdentifier(targetDeclareMethod))){
                    System.err.println("> methodIdentifier = " + methodIdentifier);
                    MethodHookInfo methodHookInfo = new MethodHookInfo(targetDeclareMethod, selfProxyDeclareMethod);
                    proxyTargetMethodCache.put(methodIdentifier,methodHookInfo);
                    System.err.println("> methodHookInfo = " + methodHookInfo);
                }
            }
        }

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e(L.VM_TAG,">> method = "+method.getName() +" stack " + Log.getStackTraceString(new Throwable()));
        String key = MethodHookInfo.getMethodIdentifier(method);
        if (!proxyTargetMethodCache.containsKey(key)){
            return method.invoke(target, args);
        }
        MethodHookInfo methodHookInfo = proxyTargetMethodCache.get(key);
        return methodHookInfo.checkAndSetOriginArtMethod(method).invoke1(this, target,method, args);
    }

    private void test(){
        System.err.println(">> 执行目标函数前");
    }

    public void todoTest(){
        try {
            proxy.test();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            proxy.test();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            proxy.test();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
