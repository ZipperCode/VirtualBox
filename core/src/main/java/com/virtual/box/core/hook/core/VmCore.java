package com.virtual.box.core.hook.core;

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
}
