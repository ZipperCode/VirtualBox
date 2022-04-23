package com.virtual.test

class NativeLib {

    companion object {
        @JvmStatic
        external fun kotlinStaticRegister()
        @JvmStatic
        external fun kotlinDynamicRegister()

        // Used to load the 'test' library on application startup.
        init {
            System.err.println(">> HookTest 半生类代码快执行，同时加载so")
            System.loadLibrary("test")
        }
    }
}