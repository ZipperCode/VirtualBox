package com.virtual.box.core.proxy

import com.virtual.box.core.BuildConfig
import com.virtual.box.core.server.VmServiceProvider
import java.util.*

/**
 *
 */
object ProxyManifest {
    /**
     * 最大的进程数量
     */
    const val FREE_COUNT = 100
    @JvmStatic
    fun isProxy(msg: String?): Boolean {
        return serviceProvider == msg || msg?.contains("virtualbox.proxy_content_provider_") == true
    }

    /**
     * 服务端进程 provider authorities [VmServiceProvider]
     * @return authority com.virtual.box.core.virtualbox.VmServiceProvider
     */
    @JvmStatic
    val serviceProvider: String get() = "${BuildConfig.LIBRARY_PACKAGE_NAME}.virtualbox.VmServiceProvider"

    /**
     * 获取虚拟化进程的 content uri
     */
    @JvmStatic
    fun getProxyAuthorities(index: Int): String {
        return String.format(Locale.CHINA, "%s.virtualbox.proxy_content_provider_%d",
            BuildConfig.LIBRARY_PACKAGE_NAME, index)
    }

    /**
     * 获取代理的Activity类名
     */
    @JvmStatic
    fun getProxyActivity(index: Int): String {
        return String.format(Locale.CHINA, "%s.proxy.ProxyActivity\$P%d", BuildConfig.LIBRARY_PACKAGE_NAME,index)
    }

    fun getProxyService(index: Int): String {
        return String.format(Locale.CHINA, "%s.proxy.ProxyService\$P%d", BuildConfig.LIBRARY_PACKAGE_NAME, index)
    }


    fun getProxyJobService(index: Int): String {
        return String.format(Locale.CHINA, "%s.proxy.ProxyJobService\$P%d", BuildConfig.LIBRARY_PACKAGE_NAME, index)
    }

    /**
     * 获取固定的进程名称
     * @param vmPid 自定义管理的进程id，不是系统进程的id
     */
    @JvmStatic
    fun getProcessName(vmPid: Int): String {
        return "${BuildConfig.HOST_PACKAGE}:p$vmPid"
    }
}