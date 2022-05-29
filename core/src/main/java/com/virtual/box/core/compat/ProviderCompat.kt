package com.virtual.box.core.compat

import android.content.pm.ProviderInfo
import android.os.IInterface
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.reflect.android.app.HIActivityManager
import com.virtual.box.reflect.android.app.HContentProviderHolder
import com.virtual.box.reflect.android.providers.HSettings
import java.lang.reflect.Proxy

object ProviderCompat {

    fun getProviderByHolder(holder: Any?): IInterface?{
        var targetIInterface: IInterface? = null
        targetIInterface = if (BuildCompat.isAtLeastOreo){
            HContentProviderHolder.provider.get(holder)
        }else{
            HIActivityManager.ContentProviderHolder.provider.get(holder)
        }

        if (targetIInterface !is Proxy){
            return targetIInterface
        }
        return null
    }

    fun setProviderInfoWithHolder(holder: Any?, newProviderInfo: ProviderInfo){
        holder ?: return
        if (BuildCompat.isAtLeastOreo){
            HContentProviderHolder.info.set(holder, newProviderInfo)
        }else{
            HIActivityManager.ContentProviderHolder.provider.set(holder, newProviderInfo)
        }

    }


    fun setProviderByHolder(holder: Any?, proxyInterface: IInterface?){
        if (BuildCompat.isAtLeastOreo){
            HContentProviderHolder.provider.set(holder, proxyInterface)
        }else{
            HIActivityManager.ContentProviderHolder.provider.set(holder, proxyInterface)
        }
    }


    fun clearSettingProvider() {
        var cache: Any
        // 获取系统已经存在的ContentProvider
        cache = HSettings.System.sNameValueCache.get()
        if (cache != null) {
            // 清除本地所有的ContentProvider
            clearContentProvider(cache)
        }
        cache = HSettings.Secure.sNameValueCache.get()
        if (cache != null) {
            clearContentProvider(cache)
        }
        if (HSettings.Global.REF.getClazz() != null) {
            cache = HSettings.Global.sNameValueCache.get()
            if (cache != null) {
                clearContentProvider(cache)
            }
        }
    }

    fun clearContentProvider(cache: Any?) {
        if (BuildCompat.isAtLeastOreo) {
            val holder = HSettings.NameValueCacheOreo.mProviderHolder.get(cache)
            if (holder != null) {
                HSettings.ContentProviderHolder.mContentProvider.set(holder, null)
            }
        } else {
            HSettings.NameValueCache.mContentProvider.set(cache, null)
        }
    }

}