package com.virtual.box.core.helper

import android.content.pm.ProviderInfo
import android.net.Uri
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.compat.ProviderCompat
import com.virtual.box.core.hook.delegate.ContentProviderStub

object ProviderHelper {

    fun replaceNewProvider(holder: Any?) {
        replaceNewProvider(holder, VirtualBox.get().hostPkg)
    }

    fun replaceNewProvider(holder: Any?, packageName: String) {
        val providerInterface = ProviderCompat.getProviderByHolder(holder) ?: return
        val proxyInterface = ContentProviderStub().wrapper(providerInterface, packageName)
        ProviderCompat.setProviderByHolder(holder, proxyInterface)
    }

    fun replaceProviderAndInfo(holder: Any?, newProviderInfo: ProviderInfo, packageName: String){
        replaceNewProvider(holder, packageName)
        ProviderCompat.setProviderInfoWithHolder(holder, newProviderInfo)
    }


    fun cleanAndInitProvider(){
        try {
            ProviderCompat.clearSettingProvider()
            // 清除Setting的ContentProvider后重新获取
            VirtualBox.get().hostContext.contentResolver.call(Uri.parse("content://settings"), "", null, null)
        }catch (e: Exception){
            L.printStackTrace(e)
        }

    }
}