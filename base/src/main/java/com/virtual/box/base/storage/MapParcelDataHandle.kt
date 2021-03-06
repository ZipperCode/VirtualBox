package com.virtual.box.base.storage

import android.os.Debug
import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class MapParcelDataHandle<T: Parcelable>(val fileName: String, private val clazz: Class<T>): IParcelDataHandle<T> {

    private val mmkv: MMKV = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE)

    override fun save(key: String, data: T?) {
        mmkv.encode(key, data)
    }

    override fun load(key: String): T? {
        return mmkv.decodeParcelable(key, clazz)
    }

}