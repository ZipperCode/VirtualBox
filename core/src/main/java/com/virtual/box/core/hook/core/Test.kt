package com.virtual.box.core.hook.core

import com.virtual.box.core.hook.annotation.IHook
import com.virtual.hook.annotator.HookMethod
import java.lang.reflect.Method

class Test {

    @HookMethod(name = "scheduleReceiver")
    class ScheduleReceiver : IHook{

         fun hook(who: Any, method: Method, args: Array<out Any>?): Any? {
            return method.invoke(who, args)
        }

        override fun hook() {
            println("")
        }
    }
}

class TestHelper{
    private val refFun: MutableMap<String, IHook> = mutableMapOf()

    init {
        this.refFun["scheduleReceiver"] = Test.ScheduleReceiver()
    }
}