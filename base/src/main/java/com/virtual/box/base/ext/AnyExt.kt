@file:JvmName("ObjectUtils")
package com.virtual.box.base.ext

fun Any.asInt(): Int{
    try {
        if (this is Number){
            return this.toInt()
        }else if (this is String){
            return Integer.parseInt(this)
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
    return 0
}

fun Any.asDouble(): Double{
    try {
        if (this is Number){
            return this.toDouble()
        }else if (this is String){
            return this.toDouble()
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
    return 0.0
}
