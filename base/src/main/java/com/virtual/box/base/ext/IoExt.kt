package com.virtual.box.base.ext

import java.io.Closeable
import java.io.File
import java.lang.Exception

fun File.checkAndMkdirs(): Boolean{
    return try {
        !exists() && mkdirs()
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}

fun File.checkOrCreateDirAndFile(): Boolean{
    return try {
        if (parentFile?.exists() == false){
            parentFile?.mkdirs()
        }
        !exists() && createNewFile()
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}

fun Closeable?.closeHandle(){
    try {
        this?.close()
    }catch (e: Exception){
        e.printStackTrace()
    }
}