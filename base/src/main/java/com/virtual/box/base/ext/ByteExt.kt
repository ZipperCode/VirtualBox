@file:JvmName("ByteUtils")
package com.virtual.box.base.ext

import android.util.Base64
import java.security.MessageDigest
import kotlin.experimental.and

fun ByteArray.base64Str(flag: Int = 0): String{
    return Base64.encodeToString(this, flag)
}

fun ByteArray.hex(): String{
    val stringBuilder = StringBuilder(this.size)
    for (b in this) {
        stringBuilder.append(String.format("%02X", b and 0xFF.toByte()))
    }
    return stringBuilder.toString()
}

fun ByteArray.string(): String{
    return String(this)
}

fun ByteArray.md5(): ByteArray{
    return try {
        MessageDigest.getInstance("MD5").digest(this)
    }catch (e: Exception){
        e.printStackTrace()
        ByteArray(0)
    }
}

fun Byte.hex(): String{
    var tmp = toInt()
    if (tmp < 0){
        tmp = this + 256
    }
    return String.format("%02X", tmp and 0xFF)
}
