package com.virtual.box.core.hook.core;

import com.virtual.box.core.hook.method.ArtMethod;

import java.lang.reflect.Method;

/**
 * @author zhangzhipeng
 * @date 2022/4/19
 **/
public class VmCore {

    static {
        System.loadLibrary("virtual");
    }

    public static native int init(int buildSdkVersion, boolean isDebug);

    public static native void addIoRules(String[] originPaths, String[] targetPaths);

    public static native void nativeHook();

    public static native String redirectPath(String originPath);

    public static native long replaceMethod(long replaceMethod, long targetMethod);

    public static native int restoreMethod(long methodContentPtr, long targetMethodPtr);
}
