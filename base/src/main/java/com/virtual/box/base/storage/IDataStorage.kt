package com.virtual.box.base.storage

import android.os.Parcelable
import java.io.Serializable

interface IDataStorage {
    fun save(key: String, data: Int)

    fun save(key: String, data: Float)

    fun save(key: String, data: Double)

    fun save(key: String, data: Long)

    fun save(key: String, data: String)

    fun save(key: String, data: ByteArray)

    fun save(key: String, data: Parcelable)

    fun save(key: String, data: Set<String>, encode: Boolean = false)

    fun save(key: String, data: Any?)

    fun load(key: String, defaultValue: Int): Int

    fun load(key: String, defaultValue: Float): Float

    fun load(key: String, defaultValue: Double): Double

    fun load(key: String, defaultValue: Long): Long

    fun load(key: String, defaultValue: String): String

    fun load(key: String, defaultValue: ByteArray): ByteArray

    fun <T : Parcelable> load(key: String, defaultValue: T): T

    fun <T : Parcelable> load(key: String, clazz: Class<T>): T?

    fun <T> load(key: String, clazz: Class<T>): T?

    fun load(key: String, defaultValue: Set<String>, encode: Boolean = false): Set<String>

    fun containKey(key: String): Boolean

    fun keys(): Set<String>

    fun remove(key: String)

    fun clear()
}