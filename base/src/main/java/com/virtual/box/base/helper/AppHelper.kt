package com.virtual.box.base.helper

object AppHelper {

    var isDebug: Boolean = false
        private set

    var isRelease: Boolean = false
        private set

    fun init(isDebug: Boolean = false){
        this.isDebug = isDebug
        this.isRelease = !isDebug
    }

}