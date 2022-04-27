package com.virtual.box.base.ext

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

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

fun File.deleteFile(): Boolean{
    try {
        if (!exists()) {
            return false
        }
        return delete()
    }catch (e: Exception){
        e.printStackTrace()
        return false
    }
}

fun File.deleteDir(){
    deleteDir(this)
}

private fun File.deleteDir(dir: File?){
    if (dir?.exists() == false){
        return
    }
    dir?.listFiles()?.forEach {
        if (it.isFile){
            it.delete()
        }else if(it.isDirectory){
            deleteDir(it)
        }
    }
    dir?.delete()
}

fun File.copyTo(targetFile: File){
    FileInputStream(this).use { input->
        FileOutputStream(targetFile).use {  output->
            val inChannel = input.channel
            val outChannel = output.channel
            val size = inChannel.size()
            var position = 0L
            val transFromSize = 1024 * 1024L
            while (position < size){
                position += inChannel.transferTo(position, transFromSize, outChannel)
            }
        }
    }
}