package com.virtual.box.core.hook.core

import com.virtual.box.core.hook.annotation.IHook
import com.virtual.hook.annotator.HookMethod
import com.zipper.free.reflect.BuildConfig
import java.lang.reflect.Method

class Test {

    @HookMethod(name = "scheduleReceiver")
    class ScheduleReceiver : IHook{

         fun hook(who: Any, method: Method, args: Array<out Any>?): Any? {
            if (BuildConfig.DEBUG && args != null){
                val intent:Any = args[0]
                val info = args[1]
                val compatInfo = args[2]
                val resultCode = args[3]
                val data = args[4]
                val extras = args[5]
                val sync = args[6]
                val sendingUser = args[7]
                val processState = args[8]
            }
            return method.invoke(who, args)
        }

        override fun hook() {
            println("")
        }
    }
}