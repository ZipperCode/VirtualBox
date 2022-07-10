package com.virtual.box.core.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Debug
import com.virtual.box.base.ext.checkAndCreate
import com.virtual.box.base.ext.checkAndMkdirs
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import com.virtual.box.reflect.android.content.pm.HPackageInfo
import java.io.File
import java.lang.Exception
import java.util.HashMap

object IoHelper {
    @SuppressLint("SdCardPath")
    fun enableRedirect(context: Context, packageInfo: ApplicationInfo) {
        val packageName = context.packageName
        val rule: MutableMap<String, String> = HashMap()
        try {
            val installAppDir = HApplicationInfo.scanPublicSourceDir.get(packageInfo)
//            val appOatDir = File(installAppDir,"oat").absolutePath
//            when {
//                packageInfo.nativeLibraryDir.contains("arm64") -> {
//                    rule["/data/app/${packageName}/lib/arm64"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/arm64"] = File(appOatDir,"arm64").absolutePath
//                }
//                packageInfo.nativeLibraryDir.contains("arm") -> {
//                    rule["/data/app/${packageName}/lib/arm"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/arm"] = File(appOatDir,"arm").absolutePath
//                }
//                packageInfo.nativeLibraryDir.contains("x86") -> {
//                    rule["/data/app/${packageName}/lib/x86"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/x86"] = File(appOatDir,"x86").absolutePath
//                }
//            }
//            rule["/data/app/$packageName/oat"] = appOatDir
//            rule["/data/app/$packageName/lib"] = HApplicationInfo.nativeLibraryRootDir.get(packageInfo)
            rule["/data/app/$packageName"] = installAppDir

            // /data/data
            rule["/data/data/$packageName"] = packageInfo.dataDir
            rule["/data/user/0/$packageName"] = packageInfo.dataDir

            // /storage/emulated/0/Android/data/com.sinyee.babybus.world/cache
            // /storage/emulated/0/Android/data
            // /storage/0DE7-2F1C/Android/data/com.sinyee.babybus.world/cache
            // /sbin/su
            // /system/bin/su
            // null/downloadCache
            // /data
            // /proc/25168/cmdline
            // /system/lib
            // /storage/emulated/0
            // /system/product/lib
            // /data/local/tmp/webview-command-line
            // /system/product/app/webview/webview.apk
            // /system/lib/libwebviewchromium_plat_support.so
            // /proc/meminfo
            if (VirtualBox.get().hostContext.externalCacheDir != null){
                val external = VirtualBox.get().hostContext.externalCacheDir!!.parentFile!!
                rule["/sdcard/Android/data/$packageName"] = File(external, "/sdcard/Android/data/$packageName").absolutePath
                rule["/sdcard/android/data/$packageName"] = File(external, "/sdcard/android/data/$packageName").absolutePath
                rule["/storage/emulated/0/android/data/$packageName"] = File(external, "/storage/emulated/0/android/data/$packageName").absolutePath
                rule["/storage/emulated/0/Android/data/$packageName"] = File(external, "/storage/emulated/0/Android/data/$packageName").absolutePath
                rule["/storage/emulated/0/Android/media/$packageName"] = File(external, "/storage/emulated/0/Android/data/$packageName").absolutePath
                //Debug.waitForDebugger()
                try {
                    val hostPackage = VirtualBox.get().hostPkg
                    val listFiles = File("/storage").listFiles()
                    listFiles?.filter { !it.name.contains("/storage/emulated/") }?.forEach {
                        val key = "${it.absolutePath}/Android/data/$packageName"
                        val value = "${it.absolutePath}/Android/data/${hostPackage}/$packageName"
                        rule[key] = value
                    }
                }catch (e: Throwable){
                    L.printStackTrace(e)
                }
            }
        }catch (e: Exception){
            L.printStackTrace(e)
        }
        val originPaths = ArrayList<String>(rule.size)
        val targetPaths = ArrayList<String>(rule.size)
        for (key in rule.keys) {
            originPaths.add(key)
            targetPaths.add(rule[key]!!)
            // 如果映射文件不存在，这边需要创建文件夹
            File(rule[key]!!).checkAndMkdirs()
        }
        VmCore.addIoRules(originPaths.toTypedArray(), targetPaths.toTypedArray())
    }
}