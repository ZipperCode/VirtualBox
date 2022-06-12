@file:JvmName("AndroidExt")
package com.virtual.box.base.ext

import android.os.Parcel

fun Parcel.writeStringArrays(array: Array<String>?) {
    val size = array?.size ?: 0
    writeInt(size)
    if (array != null) {
        for (s in array) {
            writeString(s)
        }
    }
}

fun Parcel.readStringArrays(): Array<String>{
    val size = readInt()
    val list = ArrayList<String>(size)
    for (i in 0 until size){
        list.add(readString() ?: "")
    }
    return list.toTypedArray()
}