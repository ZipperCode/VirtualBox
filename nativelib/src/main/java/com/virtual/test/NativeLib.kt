package com.virtual.test

class NativeLib {

    companion object {
        @JvmStatic
        external fun kotlinStaticRegister()
        @JvmStatic
        external fun kotlinDynamicRegister()

        // Used to load the 'test' library on application startup.
        init {
            System.loadLibrary("test")
        }
    }
}