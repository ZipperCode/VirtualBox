package com.virtual.box.base.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.reflect.Field
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SpUtil {

    private lateinit var appContext: Context

    private const val BASE_NAME = "sp_base"

    private val cacheSp: MutableMap<String, SharedPreferencesWrapper> by lazy {
        mutableMapOf<String, SharedPreferencesWrapper>()
    }

    @JvmStatic
    fun init(context: Context){
        this.appContext = context.applicationContext
    }

    @JvmStatic
    fun instance(spName: String): SharedPreferencesWrapper {
        if (cacheSp.containsKey(spName)) {
            return cacheSp[spName]!!
        }
        val spw = SharedPreferencesWrapper(appContext.getSharedPreferences(spName, Context.MODE_PRIVATE))
        cacheSp[spName] = spw
        return spw
    }

    @JvmStatic
    fun instance(): SharedPreferencesWrapper {
        if (cacheSp.containsKey(BASE_NAME)) {
            return cacheSp[BASE_NAME]!!
        }
        val spw =
            SharedPreferencesWrapper(appContext.getSharedPreferences(BASE_NAME, Context.MODE_PRIVATE))
        cacheSp[BASE_NAME] = spw
        return spw
    }

    @JvmStatic
    fun put(key: String, value: Boolean) {
        cacheSp[BASE_NAME]!!.put(key, value)
    }

    class SharedPreferencesWrapper(val sp: SharedPreferences) {

        @SuppressLint("ApplySharedPref")
        @Suppress("UNCHECKED_CAST")
        fun<T> put(key: String, value: T){
            when (value) {
                is Boolean -> sp.edit().putBoolean(key, value).commit()
                is Int -> sp.edit().putInt(key, value).commit()
                is String -> sp.edit().putString(key,value).commit()
                is Long -> sp.edit().putLong(key, value).commit()
                is Float -> sp.edit().putFloat(key, value).commit()
                is Set<*> -> sp.edit().putStringSet(key,value as Set<String>).commit()
                else -> throw IllegalArgumentException("unknown value param type")
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun<T> putAsync(key: String, value: T){
            when (value) {
                is Boolean -> sp.edit().putBoolean(key, value).apply()
                is Int -> sp.edit().putInt(key, value).apply()
                is String -> sp.edit().putString(key,value).apply()
                is Long -> sp.edit().putLong(key, value).apply()
                is Float -> sp.edit().putFloat(key, value).apply()
                is Set<*> -> sp.edit().putStringSet(key,value as Set<String>).apply()
                else -> throw IllegalArgumentException("unknown value param type")
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> get(key: String, default: T): T {
            return when (default) {
                is Boolean -> sp.getBoolean(key, default) as T
                is Int -> sp.getInt(key, default) as T
                is String -> sp.getString(key, default) as T
                is Long -> sp.getLong(key, default) as T
                is Float -> sp.getFloat(key,default) as T
                is Set<*> -> sp.getStringSet(key, emptySet<String>()) as T
                else -> throw IllegalArgumentException("unknown default param type")
            }
        }

        fun remove(key: String){
            sp.edit().remove(key).apply()
        }

        fun all(condition: String = ""):Map<String,*>{
            return sp.all.filter { it.toString().contains(condition) }
        }

        fun clear(){
            sp.edit().clear().apply()
        }
    }


}