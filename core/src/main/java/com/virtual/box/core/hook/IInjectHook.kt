package com.virtual.box.core.hook

interface IInjectHook {

    fun initHook()

    fun isHooked(): Boolean
}