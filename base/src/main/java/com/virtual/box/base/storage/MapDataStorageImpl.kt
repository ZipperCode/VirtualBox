package com.virtual.box.base.storage

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import java.lang.Exception

class MapDataStorageImpl(
    storageName: String
) : IDataStorage {

    private val mmkv: MMKV = MMKV.mmkvWithID(storageName, MMKV.SINGLE_PROCESS_MODE)

    private val gson = Gson()

    override fun save(key: String, data: Int) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: Float) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: Double) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: Long) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: String) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: ByteArray) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: Parcelable) {
        mmkv.encode(key, data)
    }

    override fun save(key: String, data: Any?) {
        data ?: return
        val objectJson = gson.toJson(data)
        mmkv.encode(key, objectJson)
    }

    override fun load(key: String, defaultValue: Int): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    override fun load(key: String, defaultValue: Float): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    override fun load(key: String, defaultValue: Double): Double {
        return mmkv.decodeDouble(key, defaultValue)
    }

    override fun load(key: String, defaultValue: Long): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    override fun load(key: String, defaultValue: String): String {
        return mmkv.decodeString(key, defaultValue) ?: ""
    }

    override fun load(key: String, defaultValue: ByteArray): ByteArray {
        return mmkv.decodeBytes(key, defaultValue) ?: ByteArray(0)
    }

    override fun <T : Parcelable> load(key: String, defaultValue: T): T {
        return mmkv.decodeParcelable(key, defaultValue.javaClass) ?: defaultValue
    }

    override fun <T : Parcelable> load(key: String, clazz: Class<T>): T? {
        return mmkv.decodeParcelable(key, clazz)
    }

    override fun <T> load(key: String, clazz: Class<T>): T? {
        try {
            val objectJson = mmkv.decodeString(key)
            return gson.fromJson<T>(objectJson, clazz)
        }catch (e: Exception){
            return null
        }
    }

    override fun containKey(key: String): Boolean {
        return mmkv.containsKey(key)
    }

    override fun keys(): Set<String> {
        val set = HashSet<String>()
        set.addAll(mmkv.allKeys() ?: emptyArray())
        return set
    }

    override fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }

    override fun clear() {
        mmkv.clearAll()
    }


}