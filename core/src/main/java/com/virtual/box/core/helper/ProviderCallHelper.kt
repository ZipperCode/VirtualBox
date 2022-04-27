package com.virtual.box.core.helper

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import android.os.*
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import java.lang.Exception

/**
 * Provider调用辅助类
 */
object ProviderCallHelper {
    /**
     * 跨进程调用
     *
     * @param authority
     * @param methodName
     * @param arg
     * @param bundle
     * @return
     */
    fun callSafely(authority: String, methodName: String, arg: String?, bundle: Bundle?): Bundle? {
        try {
            return call(authority, VirtualBox.get().hostContext, methodName, arg, bundle, 5)
        } catch (e: IllegalAccessException) {
            L.printStackTrace(e)
        }
        return null
    }

    @Throws(IllegalAccessException::class)
    fun call(authority: String, context: Context, method: String, arg: String?, bundle: Bundle?, retryCount: Int): Bundle? {
        val uri = Uri.parse("content://$authority")
        return call(context, uri, method, arg, bundle, retryCount)
    }

    /**
     * 调用
     */
    @JvmStatic
    @Throws(IllegalAccessException::class)
    fun call(
        context: Context,
        uri: Uri?,
        method: String?,
        arg: String?,
        extras: Bundle?,
        retryCount: Int
    ): Bundle? {
        val client = acquireContentProviderClientRetry(context, uri, retryCount)
        return try {
            if (client == null) {
                throw IllegalAccessException()
            }
            client.call(method!!, arg, extras)
        } catch (e: RemoteException) {
            throw IllegalAccessException(e.message)
        } finally {
            releaseQuietly(client)
        }
    }


    private fun acquireContentProviderClient(context: Context, uri: Uri?): ContentProviderClient? {
        try {
            return context.contentResolver.acquireUnstableContentProviderClient(uri!!)
        } catch (e: SecurityException) {
            L.printStackTrace(e)
        }
        return null
    }
    @JvmStatic
    fun acquireContentProviderClientRetry(
        context: Context,
        uri: Uri?,
        retryCount: Int
    ): ContentProviderClient? {
        var client = acquireContentProviderClient(context, uri)
        if (client == null) {
            var retry = 0
            while (retry < retryCount && client == null) {
                SystemClock.sleep(100)
                retry++
                client = acquireContentProviderClient(context, uri)
            }
        }
        return client
    }
    @JvmStatic
    fun acquireContentProviderClientRetry(
        context: Context,
        name: String,
        retryCount: Int
    ): ContentProviderClient? {
        var client = acquireContentProviderClient(context, name)
        if (client == null) {
            var retry = 0
            while (retry < retryCount && client == null) {
                SystemClock.sleep(100)
                retry++
                client = acquireContentProviderClient(context, name)
            }
        }
        return client
    }

    private fun acquireContentProviderClient(
        context: Context,
        name: String
    ): ContentProviderClient? {
        return context.contentResolver.acquireUnstableContentProviderClient(name)
    }

    private fun releaseQuietly(client: ContentProviderClient?) {
        if (client != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    client.close()
                } else {
                    client.release()
                }
            } catch (ignored: Exception) {
            }
        }
    }
}