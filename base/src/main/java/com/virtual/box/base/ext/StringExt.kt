@file:JvmName("StringUtils")
package com.virtual.box.base.ext

import android.util.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*


enum class EncodeMode{
    HEX,BASE64,NONE
}

fun CharSequence?.isNotNullOrEmpty(): Boolean{
    return !this.isNullOrEmpty()
}

fun String.formatDate(date: Date = Date()): String{
    val defaultFormat = "yyyy-MM-dd"
    return try {
        SimpleDateFormat(this, Locale.CHINA).format(date)
    }catch (e: Exception){
        SimpleDateFormat(defaultFormat, Locale.CHINA).format(date)
    }
}

fun String.encodeBase64(flag: Int = 0): String{
    return Base64.encodeToString(toByteArray(StandardCharsets.UTF_8),flag)
}

fun String.decodeBase64(flag: Int = 0): ByteArray{
    return Base64.decode(this,flag)
}

fun String.decodeBase64Str(flag: Int = 0): String{
    return String(Base64.decode(this,flag))
}

fun String.hex(): String{
    return toByteArray(StandardCharsets.UTF_8).hex()
}

fun String.md5(encodeMode: EncodeMode = EncodeMode.BASE64): String{
    val md5Data = toByteArray(StandardCharsets.UTF_8).md5()
    return when(encodeMode){
        EncodeMode.BASE64 -> md5Data.base64Str()
        EncodeMode.HEX -> md5Data.hex()
        EncodeMode.NONE -> String(md5Data)
    }
}

fun String.urlEncode(enc: String = "UTF-8"): String{
    return URLEncoder.encode(this,enc)
}

fun String.urlDecode(enc: String = "UTF-8"): String{
    return URLDecoder.decode(this,enc)
}

fun String.charset(): Charset{
    return try {
        Charset.forName(this)
    }catch (e: Exception){
        Charset.forName("UTF-8")
    }
}

fun String?.safeIntern(): String{
    this ?: return ""
    return this.intern()
}
