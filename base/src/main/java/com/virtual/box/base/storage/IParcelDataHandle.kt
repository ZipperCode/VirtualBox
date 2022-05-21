package com.virtual.box.base.storage

import android.os.Parcelable

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
interface IParcelDataHandle<T> {

    fun save(key: String, data: T?)

    fun load(key: String): T?
}